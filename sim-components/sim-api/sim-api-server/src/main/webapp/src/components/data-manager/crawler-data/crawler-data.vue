<!--爬虫数据管理组件-->
<style lang='less'>
  .h-collapse-item-header{
    border: none;
    height: 50px;
  }
</style>
<template>
  <div class="search-list-vue frame-page h-panel">
    <div class="h-panel-bar"><span class="h-panel-title">爬虫数据管理</span></div>
    <div class="h-panel-body">
      <Row :space="50">
        <Cell width="6" style="border-right: solid 1px #eee;">
          <p><Search style="width: 100%" @search="search" position="front" trigger-type="input"></Search></p>
          <Table :datas="initData.crawlerList" @trclick="loadCrawlerData" :height="500" >
            <TableItem title="我的爬虫" prop="crawlerName" ></TableItem>
            <template slot="expand" slot-scope="{index, data}">
              <Form :readonly="true">
                <FormItem label="爬虫名称">{{data.crawlerName}}</FormItem>
                <FormItem label="说明信息">{{data.description}}</FormItem>
              </Form>
            </template>
          </Table>
<!--          <Collapse v-model="value" accordion  style="height: 500px;overflow: scroll;">
            <CollapseItem :title="c.crawlerName" v-for="c of initData.crawlerList" @click="loadCrawlerData(c)" style="border: none; ">
              <Form :readonly="true">
                <FormItem label="爬虫名称">{{c.crawlerName}}</FormItem>
                <FormItem label="采集字段">{{c.fields}}</FormItem>
                <FormItem label="说明信息">{{c.description}}</FormItem>
              </Form>
            </CollapseItem>
          </Collapse>-->
        </Cell>
        <Cell width="18">
          <div class="h-panel">
            <div class="h-panel-bar"><span class="h-panel-title" v-font="14">xxx 采集数据</span></div>
            <Form :label-width="110">
                <FormItem label="展示字段" style="padding-bottom: 10px">
                  <Checkbox :indeterminate="params.showFields.length>0&&params.showFields.length<initData.crawlerFields.length" :checked="params.showFields.length === this.initData.crawlerFields.length" @click.native="checkAll"  style="margin-right: 10px">全选</Checkbox>
                  <Checkbox v-model="params.showFields" :datas="initData.crawlerFields" keyName="id" titleName="fieldName" style="margin-right: 10px"></Checkbox>
                </FormItem>
                <FormItem label="非空字段" prop="inputData" style="padding-bottom: 10px">
                  <Checkbox v-model="params.notNullFields" :datas="initData.crawlerFields" keyName="id" titleName="fieldName">非空</Checkbox>
                </FormItem>
              <FormItem>
                <Button size="s">查询</button>
                <Button size="s">重置</button>
                <Button size="s">导出数据（弹窗）</button>
              </FormItem>
              </Form>
            <div class="bottom-line"></div>
            <div class="h-panel-body">
              <Table align="center" border :datas="datas" :columns="columns" :ths="ths" :height="300">
                <div slot="empty">自定义提醒：暂时无数据</div>
              </Table>
              <p></p>
              <Pagination v-if="pagination.total>0" :small="false" align="right" v-model="pagination" @change="changePage" layout="pager"></Pagination>
            </div>
          </div>

          <p></p>

        </Cell>
      </Row>
    </div>

  </div>
</template>
<script>
  import Ajax from "../../../js/common/ajax";

  export default {
    data() {
      return {
        value: [0],

        dicts:{
        },
        pagination: {
          page: 1,
          size: 100,
          total: 10
        },
        initData:{
          crawlerList:[],
          crawlerFields:[]
        },
        params:{
          notNullFields:[],
          showFields:[]
        },
        ths: [
          [
            { title: '序号', rowspan: 2,align:'center'},
            { title: '字段组1', colspan: 2,align:'center' },
            { title: '字段组2', colspan: 3,align:'center' }

          ], [
            { title: '标题',align:'center' },
            { title: '价格',align:'center' },
            { title: '评分',align:'center' },
            { title: '购买人数',align:'center' },
            { title: '推荐指数',align:'center' }
          ]
        ],
        columns: [
          {
            prop: 'index',
            width: 100,
            align: 'center'
          },
          {prop: 'id', width: 100, align: 'center'},
          {prop: 'name', width: 100, align: 'center'},
          {prop: 'age', width: 100, align: 'center'},
          {prop: 'address', width: 150, align: 'center'},
          {prop: 'address', width: 150, align: 'center'}
        ],
        datas: [
          { index: 1, id: 'abc1', name: '测试1', age: 1, address: '上海1', address2: '上海21' },
          { index: 2, id: 'abc2', name: '测试2', age: 2, address: '上海2', address2: '上海22' },
          { index: 3, id: 'abc3', name: '测试3', age: 3, address: '上海3', address2: '上海23' },
          { index: 4, id: 'abc4', name: '测试4', age: 4, address: '上海4', address2: '上海24' },
          { index: 5, id: 'abc5', name: '测试5', age: 5, address: '上海5', address2: '上海25' },
          { index: 6, id: 'abc6', name: '测试6', age: 6, address: '上海6', address2: '上海26' },
          { index: 7, id: 'abc7', name: '测试7', age: 7, address: '上海7', address2: '上海27' },
          { index: 8, id: 'abc8', name: '测试8', age: 8, address: '上海8', address2: '上海28' },
          { index: 9, id: 'abc9', name: '测试9', age: 9, address: '上海9', address2: '上海29' }
        ]
      };
    },
    created() {

    },
    mounted() {
      this.init();
    },
    watch: {
      params() {
        this.getData(true);
      },
      deep: true
    },
    methods: {
      init() {
        let that = this;
        //加载爬虫列表
        let qryParams = {};
        qryParams.pagination = this.pagination;
        Ajax.get('/manager/crawler/qryCrawlerList', qryParams).then((res) => {
          that.initData.crawlerList = res.data.list;
        });
        this.getData();
      },
      getData(reload = false) {

      },
      checkAll() {
        if (this.params.showFields.length === this.initData.crawlerFields.length) {
          this.params.showFields.splice(0, this.params.showFields.length);
        } else {
          this.params.showFields = [];
          for(let i in this.initData.crawlerFields){
            this.params.showFields.push(this.initData.crawlerFields[i].itemCode)
          }
        }
      },
      search(){

      },
      loadCrawlerData(data,event,index){

        //展开/关闭
        this.$set(data, '_expand', !data._expand);
        //关闭其他
        for(let i=0;i<this.initData.crawlerList.length;i++){
          if(i!==index){
            this.$set(this.initData.crawlerList[i], '_expand', false);
          }
        }
        let crawlerId = data.crawlerId;
        //查询爬虫字段
        let that = this;
        Ajax.get('/manager/crawler/qryCrawlerField/'+crawlerId).then((res) => {
          console.log(res)
          that.initData.crawlerFields = res.data;
          log(that.initData.crawlerFields)
        });
      },
      changePage(){

      }
    },
    computed: {}
  };
</script>
