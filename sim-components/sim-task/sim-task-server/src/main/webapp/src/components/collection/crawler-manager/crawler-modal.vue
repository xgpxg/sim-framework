<!--爬虫选择弹窗组件-->
<style lang="less">

</style>
<template>
  <div>
    <Modal v-model="opened" :closeOnMask="false">
      <div slot="header">选择爬虫</div>
      <Row :space="50">
        <Cell width="3">
          <div v-width="850" v-height="400">
            <Tabs :datas="dicts" class-name="h-tabs-menu" v-model="selected" @change="change"></Tabs>
          </div>
        </Cell>
        <Cell width="21">
          <Table :datas="datas"  :border="true" :checkbox="false" :stripe="false" :loading="loading" @select="selectTr" :getTrClass="getTrClass" :radio="true" selectWhenClickTr>
            <!-- <TableItem title="Index" prop="$index"></TableItem> -->
            <TableItem title="序号" prop="$serial" :width="80" :align="'center'" ></TableItem>
            <TableItem title="爬虫名称" prop="crawlerName" :width="100" :align="'center'"></TableItem>
            <TableItem title="源站名称" prop="websiteName" :width="100" :align="'center'"></TableItem>
            <TableItem title="源地址" :width="200"  :align="'center'">
              <template slot-scope="{data}">
                <div :title="data.baseUrl" class="text-ellipsis">{{data.baseUrl}}</div>
              </template>
            </TableItem>            <TableItem title="状态" prop="status" :width="100" :align="'center'"></TableItem>
            <TableItem title="操作" :align="'center'" :width="100">
              <template slot-scope="{data}">
                <span class="text-hover" @click="openDetail(data)">{{data._expand?'收起':'详情'}} </span>
                <span class="text-hover" @click="selectTr(data)">
                  选择
                </span>
              </template>
            </TableItem>
            <template slot="expand" slot-scope="{index, data}">
              <Form labelPosition="left" readonly mode="twocolumn">
                <FormItem label="爬虫ID">{{data.crawlerId}}</FormItem>
                <FormItem label="爬虫名称">{{data.crawlerName}}</FormItem>
                <FormItem label="源站类型">{{data.websiteType}}</FormItem>
                <FormItem label="源站名称">{{data.websiteName}}</FormItem>
                <FormItem label="源地址">{{data.baseUrl}}</FormItem>
                <FormItem label="匹配地址">{{data.matchUrl}}</FormItem>
                <FormItem label="采集字段">{{data.fields}}</FormItem>
              </Form>
              <Loading :loading="data.loading"></Loading>
            </template>
            <div slot="empty">暂无数据</div>
          </Table>
          <p></p>
          <Pagination v-if="pagination.total>0" align="right" v-model="pagination" @change="changePage" layout="pager"/>
        </Cell>
      </Row>
      <div slot="footer">
        <button class="h-btn" @click="cancel">取消</button>
        <button class="h-btn h-btn-blue" @click="confirm">确认</button>
      </div>
    </Modal>
  </div>

</template>

<script>
  import Ajax from "../../../js/common/ajax";
  import Request from "../../../js/common/request";

  export default {
    props: {
      fruit: String,
      opened: Boolean
    },
    data() {
      return {
        isOpen: this.$props.opened,
        dicts: [],
        selected: 1000,
        loading:true,
        datas: [],
        pagination: {
          page: 1,
          size: 5,
          total: 0
        },
        params: {},
        selectedData:null
      };
    },
    created(){
      let that = this;
      Request.Dict.get("crawler_share_type").then((res)=>{
        that.dicts = res.data;
      })
    },
    mounted() {
      this.init();
    },
    methods: {
      change(data) {
        this.params.shareType = data.itemCode;
        this.getData(true);
      },
      /**
       *
       */
      confirm() {
        if (this.selectedData === null) {
          this.$Message('请选择一个爬虫');
          return false;
        }
        this.$emit('confirm', this.selectedData)
      },
      cancel() {
        this.$emit('cancel')
      },
      changePage(page) {
        this.pagination = page;
        this.getData();
      },
      init() {
        this.getData();
      },
      getData(reload = false) {
        let that = this;
        if (reload) {
          this.pagination.page = 1;
        }
        this.loading = true;
        this.params.pagination = this.pagination;
        console.log(this.params)
        Ajax.get('/manager/crawler/qryCrawlerList',this.params).then((res)=>{
          console.log(res)
          that.datas = res.data.list;
          //更新分页
          that.pagination.total = res.data.total;
          that.loading = false;
        });
      },
      selectTr(data){
        this.selectedData = data;
      },
      openDetail(data){
        this.$set(data, '_expand', !data._expand);
      },
      getTrClass(data, index) {
        if (data === this.selectedData) {
          return ['bg-blue-color','white-color'];
        }
      }
    }
  };
</script>
