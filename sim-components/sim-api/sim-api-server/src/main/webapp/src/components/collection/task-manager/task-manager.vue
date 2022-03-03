<!--任务列表组件-->
<style lang='less'>
</style>
<template>
  <div class="search-list-vue frame-page h-panel">
    <div class="h-panel-bar"><span class="h-panel-title">任务管理</span></div>
    <div class="h-panel-bar">
      <div class="search-picker">
        <SearchFilter v-model="params" :datas="dicts.taskType" prop="taskType" title="任务类型"></SearchFilter>
        <SearchFilter v-model="params" :datas="dicts.taskStatus" prop="taskStatus" title="任务状态"></SearchFilter>
        <!--        <SearchFilter v-model="params" :datas="dicts.crawStatus" prop="crawlStatus" title="采集状态"></SearchFilter>
                <SearchFilter v-model="params" :datas="dicts.parseStatus" prop="parseStatus" title="解析状态"></SearchFilter>-->
        <SearchFilter v-model="params" prop="taskName" title="任务名称" search></SearchFilter>
      </div>
    </div>
    <div class="h-panel-body">
      <!--<div>
        <AItem v-for="d of datas" :key="d.id" :item="d" :loading="loading"></AItem>
      </div>
      <Pagination v-if="pagination.total>0" align="right" v-model="pagination" @change="changePage" />
      -->
      <div>
        <p>
          <router-link :to="{name:'CreateTask'}">
            <button class="h-btn h-btn-green"><i class="h-icon-plus"></i> 新建任务</button>
          </router-link>
        </p>
        <Table :datas="datas" :border="true" :checkbox="false" :stripe="false" :loading="loading">
          <!-- <TableItem title="Index" prop="$index"></TableItem> -->
          <TableItem title="序号" v-if="false" prop="$serial" :width="80" :align="'center'"></TableItem>
          <TableItem title="任务ID" prop="taskId" :width="120" :align="'center'"></TableItem>
          <TableItem title="任务名称" prop="taskName" :width="200" :align="'center'"></TableItem>
          <TableItem title="任务类型" prop="taskType" :width="100" :align="'center'"></TableItem>
          <TableItem title="任务状态" prop="" :width="100" :align="'center'">
            <template slot-scope="{data}">
              <i class="h-icon-spinner green-color" v-if="data.taskStatus==='进行中'"></i>
              <span> {{data.taskStatus}}</span>
            </template>
          </TableItem>
          <TableItem title="成功采集数" prop="crawSuccessNum" :width="100" :align="'center'"></TableItem>
          <TableItem title="成功解析数" prop="parseSuccessNum" :width="100" :align="'center'"></TableItem>
          <TableItem title="失败采集数" prop="crawFailNum" :width="100" :align="'center'"></TableItem>
          <TableItem title="失败解析数" prop="parseFailNum" :width="100" :align="'center'"></TableItem>
          <TableItem title="操作" :align="'center'" :width="200" >
            <template slot-scope="{data}">
              <h-button :text="true" size="xs" :disabled="!(data.taskStatus==='未进行'||data.taskStatus==='已完成')" @click="start(data)">启动</h-button>
              <h-button :text="true" size="xs" :disabled="!(data.taskStatus==='进行中')"  @click="stop(data)">停止</h-button>
              <h-button :text="true" size="xs" :disabled="data.taskStatus==='进行中'" @click="details(data)">删除</h-button>
              <h-button :text="true" size="xs" @click="details(data)">详情</h-button>
            </template>
          </TableItem>
          <div slot="empty">暂无数据</div>
        </Table>
        <p></p>
        <Pagination v-if="pagination.total>0" align="right" v-model="pagination" @change="changePage" layout="pager"></Pagination>
      </div>
    </div>

  </div>
</template>
<script>
  import Ajax from "../../../js/common/ajax";
  import Request from "../../../js/common/request";

  export default {
    data() {
      return {
        dicts: {
          taskType: [],
          taskStatus: [],
          crawStatus: [],
          parseStatus: [],
        },
        pagination: {
          page: 1,
          size: 10,
          total: 0
        },
        datas: [],
        loading: true,
        params: {
          taskType: '',
          taskStatus: '',
          taskName: '',
          crawStatus: '',
          parseStatus: ''
        }
      };
    },
    mounted() {
      this.init();
    },
    watch: {
      params() {
        this.getData(true);
      }
    },
    methods: {
      changePage(page) {
        this.pagination = page;
        this.getData();
      },
      init() {
        let that = this;
        Request.Dict.get('task_type').then((res) => {
          that.dicts.taskType = res.data || [];
        });
        Request.Dict.get('task_status').then((res) => {
          that.dicts.taskStatus = res.data || [];
        });
        Request.Dict.get('crawl_status').then((res) => {
          that.dicts.crawStatus = res.data || [];
        });
        Request.Dict.get('parse_status').then((res) => {
          that.dicts.parseStatus = res.data || [];
        });
        this.getData();
      },
      getData(reload = false) {
        let that = this;
        if (reload) {
          this.pagination.page = 1;
        }
        this.loading = true;
        this.params.pagination = this.pagination;
        console.log(this.params);
        Ajax.get('/manager/task/qryTaskList', this.params).then((res) => {
          console.log(res);
          that.datas = res.data.list;
          //更新分页
          that.pagination.total = res.data.total;
          that.loading = false;
        });
      },
      remove(datas, data) {
        this.$Confirm('确定删除？', '自定义title').then(() => {
          datas.splice(datas.indexOf(data), 1);
          this.$Message.success('删除成功！' + JSON.stringify(data));
        }).catch(() => {
          //this.$Message.error('取消');
        });

      },
      edit(data) {
        this.$Message.success('编辑：' + JSON.stringify(data));
      },
      details(data) {
        this.$Message.info('详情：' + JSON.stringify(data));
      },
      start(data){
        let that = this;
        //TODO 这里需要改为请求到task，由task工程统一分发任务
        Ajax.post('/crawler/start', {taskId: data.taskId}).then((res) => {
          if(res.code===0){
            that.$Notice(res.data);
            that.getData();
          }
        });
      },
      stop(data) {
        let that = this;

        this.$Confirm('停止后任务将不可恢复，需要重新启动任务哦', '停止采集任务').then(() => {
          //TODO 这里需要改为请求到task，由task工程统一分发任务
          Ajax.post('/crawler/stop', {taskId: data.taskId}).then((res) => {
            if(res.code===0){
              that.$Notice(res.data);
              that.getData();
            }
          });
        });
      }
    },
    computed: {}
  };
</script>
