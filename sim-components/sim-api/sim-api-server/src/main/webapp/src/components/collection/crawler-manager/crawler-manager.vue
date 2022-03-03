<!--爬虫列表组件-->
<style lang='less'>
</style>
<template>
  <div class="search-list-vue frame-page h-panel">
    <div class="h-panel-bar"><span class="h-panel-title">爬虫管理</span></div>
    <div class="h-panel-bar">
      <div class="search-picker">
        <SearchFilter v-model="params" :datas="dicts.status" prop="status" title="状态"></SearchFilter>
        <SearchFilter v-model="params" :datas="dicts.websiteType" prop="websiteType" title="网站类型"></SearchFilter>
        <SearchFilter v-model="params" prop="crawlerName" title="搜索" search ></SearchFilter>
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
          <router-link :to="{name:'CreateCrawler'}">
            <button class="h-btn h-btn-green"><i class="h-icon-plus"></i> 新建爬虫</button>
          </router-link>
        </p>
        <Table :datas="datas" :border="true" :checkbox="false" :stripe="false" :loading="loading" >
          <!-- <TableItem title="Index" prop="$index"></TableItem> -->
          <TableItem title="序号" prop="$serial" :width="80" :align="'center'"></TableItem>
          <TableItem title="爬虫名称" prop="crawlerName" :align="'center'"></TableItem>
          <TableItem title="共享类型" prop="shareType" :align="'center'"></TableItem>
          <TableItem title="采集网站类型" prop="websiteType" :align="'center'"></TableItem>
          <TableItem title="源地址" :width="300"  :align="'center'">
            <template slot-scope="{data}">
              <div :title="data.baseUrl" class="text-ellipsis">{{data.baseUrl}}</div>
            </template>
          </TableItem>
          <TableItem title="状态" prop="status" :align="'center'"></TableItem>
          <TableItem title="创建时间" prop="createTime" :align="'center'" :width="150"></TableItem>
          <TableItem title="操作"  :align="'center'" :width="200">
            <template slot-scope="{data}">
              <h-button class="h-btn h-btn-xs h-btn-red" :disabled="data.status==='运行中'" @click="remove(datas, data)">删除</h-button>
              <h-button class="h-btn h-btn-xs h-btn-yellow" :disabled="data.status==='运行中'" @click="edit(data)">
                <router-link  v-color="'white'" :to="{name:'CreateCrawler',query:{crawlerId:data.crawlerId}}">
                  编辑
                </router-link>
              </h-button>
              <h-button class="h-btn h-btn-xs h-btn-blue" >
                <router-link  v-color="'white'" :to="{name:'CrawlerDetail',query:{crawlerId:data.crawlerId}}">
                   详情
                </router-link>
              </h-button>
              <h-button class="h-btn h-btn-xs h-btn-green" >
                <router-link   v-color="'white'" :to="{name:'CrawlerDetail',query:{crawlerId:data.crawlerId}}">
                  数据
                </router-link>
              </h-button>
            </template>
          </TableItem>
          <div slot="empty">暂无数据</div>
        </Table>
        <p></p>
        <Pagination v-if="pagination.total>0" :small="false" align="right" v-model="pagination" @change="changePage" layout="pager"></Pagination>
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
          status: [],
          websiteType:[]
        },
        pagination: {
          page: 1,
          size: 10,
          total: 0
        },
        datas: [],
        loading: true,
        params: {
          status:'',
          websiteType:''
        }
      };
    },
    created(){
      /*let that = this;
      let param = {};
      param.pagination = this.pagination;
      this.loading = true;
      Ajax.get('/manager/qryCrawlerList',param).then((res)=>{
        that.datas = res.data;
        this.loading = false;
      });*/
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
      init() {
        let that = this;
        //加载采集网站类型字典
        Request.Dict.get('website_type').then((res) => {
          that.dicts.websiteType = res.data || [];
        });
        //加载爬虫状态字典
        Request.Dict.get('crawler_status').then((res) => {
          that.dicts.status = res.data || [];
        });
        this.getData();
      },
      changePage(page) {
        this.pagination = page;
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
      remove(datas, data) {
        let that = this;
        this.$Confirm('删除爬虫后该爬虫的所有数据将不可用，确定删除？', '提示').then(() => {
          Ajax.post('/manager/crawler/delCrawler',{crawlerId:data.crawlerId}).then((res)=>{
            console.log(res)
            datas.splice(datas.indexOf(data), 1);
            that.$Message.success(res.data);
          });
        }).catch(() => {
          this.$Message.error('删除失败');
        });
      },
      edit(data){
        this.$Message.success('编辑：'+JSON.stringify(data));
      },
      details(data){
        this.$Message.info('详情：'+JSON.stringify(data));
      }
    },
    computed: {}
  };
</script>
