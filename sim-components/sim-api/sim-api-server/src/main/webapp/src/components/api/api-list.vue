<template>
  <div>
    <div class="search-list-vue frame-page h-panel">
      <div>
        <Tabs style="padding: 20px;background-color: white" :datas="appInfos" v-model="selected" @change="change">
          <template slot-scope="{tab}" slot="item">
            <span v-if="tab.status!=='1'" style="color: darkgrey"><span>{{tab.itemCode}}</span>（离线）</span>
            <span v-if="tab.status==='1'"><span>{{tab.itemCode}}</span></span>
          </template>
        </Tabs>
      </div>
      <div class="h-panel-bar">
        <div class="search-picker">
          <SearchFilter v-model="params" :datas="dicts.status" prop="status"
                        title="API状态"></SearchFilter>
          <SearchFilter v-model="params" :datas="dicts.openSimulation" prop="openSimulation"
                        title="模拟状态"></SearchFilter>
          <SearchFilter v-model="params" :datas="dicts.reqMethod" prop="reqMethod" title="请求方式"></SearchFilter>
          <SearchFilter v-model="params" :datas="dicts.apiType" prop="apiType" title="注册类型"></SearchFilter>
          <SearchFilter v-model="params" prop="filter" title="搜索" search></SearchFilter>
        </div>
      </div>
      <Table :datas="initData" :height="600" :loading="loading">
        <TableItem title="ID" prop="apiId" align="center" :width="80"></TableItem>
        <TableItem title="API地址" :width="200" prop="apiUrl" align="center"></TableItem>
        <TableItem title="API名称" prop="apiName" align="center"></TableItem>
        <TableItem title="请求方式" prop="reqMethod" align="center"></TableItem>
        <TableItem title="是否模拟" align="center">
          <template slot-scope="{data}">
            <span v-if="data.openSimulation===0">否</span>
            <span v-if="data.openSimulation===1">是</span>
          </template>
        </TableItem>
        <TableItem title="状态" align="center">
          <template slot-scope="{data}">
            <span v-if="data.status==='1000'"><i class="h-icon-success" style="color: green;"></i> 有效</span>
            <span v-if="data.status==='2000'"><i class="h-icon-warn" style="color: orange;"></i> 无效</span>
          </template>
        </TableItem>
        <TableItem title="归属应用" prop="appName" align="center"></TableItem>
        <TableItem title="应用地址" prop="appAddr" align="center"></TableItem>
        <TableItem title="数据模拟" align="center">
          <template slot-scope="{data}">
            <h-switch v-model="data.openSimulation" :small="true" trueValue="1" falseValue="0"
                      @change="openOrCloseSimulation(data)">
              <span slot="open">开启</span>
              <span slot="close">关闭</span>
            </h-switch>
          </template>
        </TableItem>
        <TableItem title="操作" :width="100" fixed="right" align="center">
          <template slot-scope="{data}">
            <button class="h-btn h-btn-s h-btn-blue" @click="openModal(data)"><i class="h-icon-edit"> 编辑</i>
            </button>
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
  import ApiDetailModal from './api-detail-modal';

  export default {
    name: "api-list",
    components: {
      ApiDetailModal
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
          status: [{itemCode: '1000', itemText: '有效'}, {itemCode: '2000', itemText: '无效'}],
          apiType:[{itemCode: '1000', itemText: '自动注册'},{itemCode: '2000', itemText: '手动注册'}]
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
        this.getAppList();

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
        Ajax.postJson("/api/api-list", reqParam).then((res) => {
          that.initData = res.data.list;
          that.pagination.total = res.data.total;
          that.loading = false;
        });
      },
      getAppList() {
        let that = this;
        Ajax.get("/api/app-list").then((res) => {
          let list = res.data;
          if (list && list.length > 0) {
            for (let i = 0; i < list.length; i++) {
              console.log(list[i])
              Ajax.get("/api/status", {appAddr: list[i].appAddr}).then((d) => {
                that.appInfos.push({
                  itemCode: list[i].appName,
                  itemText: list[i].appName,
                  status: d.data
                });
                //默认选择第一项
                if (i === 0) {
                  that.selected = that.appInfos[0].itemCode;
                  that.getData();
                }
              });
            }
          }
        });
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
      openOrCloseSimulation(data) {
        let that = this;
        this.$Loading();
        let open = data.openSimulation;
        if (open === 0) {
          open = 1;
        } else if (open === 1) {
          open = 0;
        }
        Ajax.postJson("/api/open-close-simulation?apiId=" + data.apiId + "&open=" + open).then((res) => {
          if (res.code === 0) {
            data.openSimulation = open;
            that.$Message.success(res.data);
          } else {
            data.openSimulation = !open;
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
            vue: ApiDetailModal,
            datas: {apiInfo: data},
          },
          width: 700,
          events: {
            success: (modal, data) => {
              this.value = data;
              that.getData();
            }
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
