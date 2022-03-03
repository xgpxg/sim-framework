<template>
  <div>
    <div class="search-list-vue frame-page h-panel">
      <!--<div>
        <Tabs style="padding: 20px;background-color: white" :datas="appInfos" v-model="selected" @change="change">
          <template slot-scope="{tab}" slot="item">
            <span v-if="tab.status!=='1'" style="color: darkgrey"><span>{{tab.itemCode}}</span>（离线）</span>
            <span v-if="tab.status==='1'"><span>{{tab.itemCode}}</span></span>
          </template>
        </Tabs>
      </div>-->
      <div class="h-panel-bar">
        <div class="search-picker">
          <SearchFilter v-model="params" :datas="dicts.group" prop="group"
                        title="分组"></SearchFilter>
          <SearchFilter v-model="params" :datas="dicts.status" prop="state"
                        title="任务状态"></SearchFilter>
        </div>
      </div>
      <Table :datas="initData" :height="600" :loading="loading">
        <TableItem title="ID" prop="id" align="center" :width="50"></TableItem>
        <TableItem title="任务名称" :width="100" prop="taskName" align="center" ></TableItem>
        <TableItem title="分组" prop="group" align="center" :width="80"></TableItem>
        <TableItem title="任务类" align="center" :width="200" >
          <template slot-scope="{data}">
            <span v-text="data.className" :title="data.className" class="text-ellipsis" v-width="100"></span>
          </template>
        </TableItem>
        <TableItem title="定时规则" prop="cron" align="center" :width="150"></TableItem>
        <TableItem title="状态" align="center" prop="state" :width="80"></TableItem>
        <TableItem title="归属应用" prop="appName" align="center"></TableItem>
        <!--<TableItem title="应用地址" prop="appAddr" align="center"></TableItem>-->
        <TableItem title="启用状态" align="center">
          <template slot-scope="{data}">
            <h-switch v-model="data.enabled" :small="true" trueValue="1" falseValue="0"
                      @change="enabled(data)">
              <span slot="true">启用</span>
              <span slot="false">停止</span>
            </h-switch>
          </template>
        </TableItem>
        <TableItem title="应用地址" prop="appAddr" :width="150" align="center">

        </TableItem>
        <TableItem title="日志" align="center">
          <template slot-scope="{data}">
            <Button :text="true" @click="openModal(data)">日志</Button>
          </template>
        </TableItem>

        <TableItem title="操作" :width="100" fixed="right" align="center">
          <template slot-scope="{data}">
            <DropdownMenu @click="opt" :datas="data.opts" trigger="hover" class-name="h-text-dropdown" :width="100">
              <span>操作选择</span>
            </DropdownMenu>
          </template>
        </TableItem>
        <div slot="empty">暂无数据</div>
      </Table>
      <br>
      <Pagination v-if="pagination.total>0" :small="false" align="right" v-model="pagination" @change="changePage"
                  layout="pager"></Pagination>
      <br>
    </div>
  </div>
</template>

<script>
  import Ajax from "../../js/common/ajax";
  import utils from 'hey-utils';
  import TaskLogModal from './task-log-modal';

  export default {
    name: "task-list",
    components: {
      TaskLogModal
    },
    data() {
      return {
        loading: false,
        initData: [],
        pagination: {
          page: 1,
          size: 10,
          total: 0
        },
        selected: '',
        appInfo: {},
        appInfos:[],
        open: 0,
        appStatus:'',
        dicts: {
          openSimulation: [{itemCode: '1', itemText: '是'}, {itemCode: '0', itemText: '否'}],
          reqMethod: [
            {itemCode: 'GET', itemText: 'GET'},
            {itemCode: 'POST', itemText: 'POST'},
            {itemCode: 'PATCH', itemText: 'PATCH'},
            {itemCode: 'PUT', itemText: 'PUT'},
            {itemCode: 'DELETE', itemText: 'DELETE'}],
          status: [{itemCode: 'NORMAL', itemText: '正常'}, {itemCode: 'SUSPEND', itemText: '暂停'}, {itemCode: 'NONE', itemText: '停止'}],
          apiType:[{itemCode: '1000', itemText: '自动注册'},{itemCode: '2000', itemText: '手动注册'}],
          group:[{itemCode: 'DEFAULT', itemText: '默认分组'}]
        },
        params: {}
      }
    },
    mounted() {
      this.init();
    },
    watch: {
      params() {
        this.getData();
      }
    },
    methods: {
      init() {
        this.getData();

      },
      getData() {
        let that = this;
        that.loading = true;
        let reqParam = {};
        reqParam.pagination = that.pagination;
        if (this.selected !== '') {
          reqParam.appName = this.selected;
        }
        utils.extend(reqParam, this.params);
        Ajax.get("/simTaskManager/taskList", reqParam).then((res) => {
          that.initData = res.data.list;
          that.initOpt(that.initData);
          that.pagination.total = res.data.total;
          that.loading = false;
          console.log(res)
        });
      },
      initOpt(taskList){
        taskList.forEach(item=>{
          let state = item.state;
          let taskId = item.id;
          let enabled = item.enabled;
          item.opts = [
            /*{taskId:taskId,itemCode: 'edit', itemText: '编辑'},*/
            {taskId:taskId,itemCode: 'suspend', itemText: '暂停',disabled:state==='SUSPEND'||state==='NONE'||!enabled},
            {taskId:taskId,itemCode: 'resume', itemText: '恢复',disabled:state==='NORMAL'||state==='NONE'||!enabled},
            {taskId:taskId,itemCode: 'stop', itemText: '停止',disabled:state==='NONE'||!enabled},
            {taskId:taskId,itemCode: 'start', itemText: '启动',disabled:state==='NORMAL'||state==='SUSPEND'||!enabled}
          ];
        })
      },
      remove(datas, data) {
        log(this.appInfo);
        datas.splice(datas.indexOf(data), 1);
      },
      add(datas) {

      },
      /**
       * 选项卡切换
       */
      change(data) {
        let that = this;
        this.getData();
      },
      /**
       * 打开/关闭模拟
       * @param data
       */
      enabled(data) {
        let that = this;
        let taskId = data.id;
        this.$Loading();
        let enabled = data.enabled;
        if (enabled === 0) {
          enabled = 1;
        } else if (enabled === 1) {
          enabled = 0;
        }
        Ajax.postJson("/simTaskManager/enabled?taskId="+taskId+"&enabled=" + enabled).then((res) => {
          if (res.code === 0) {
            data.enabled = enabled;
            that.$Message.success(res.data);
          } else {
            data.enabled = !enabled;
          }
          that.$Loading.close();
          this.getData();
        })
      },
      changePage(page) {
        this.pagination = page;
        this.getData();
      },
      openModal(data) {
        let that = this;
        this.$Modal({
          component: {
            vue: TaskLogModal,
            datas: {taskInfo: data},
          },
          //width: 1,
          //height:1,
          hasCloseIcon:true,
          draggable:true,
          events: {
            success: (modal, data) => {
              this.value = data;
              that.getData();
            }
          }
        })
      },
      opt(code,data) {
        let that = this;
        let taskId = data.taskId;
        Ajax.postJson("/simTaskManager/state?taskId="+taskId+"&state=" + code).then((res) => {
          if (res.code === 0) {
            data.openSimulation = open;
            that.$Message.success(res.data);
          } else {
            data.openSimulation = !open;
          }
          that.$Loading.close();
          this.getData();
        });
        //this.$Message.info(`您点击了${JSON.stringify(a)}`);
      }
    }
  }
</script>

<style scoped>

</style>
