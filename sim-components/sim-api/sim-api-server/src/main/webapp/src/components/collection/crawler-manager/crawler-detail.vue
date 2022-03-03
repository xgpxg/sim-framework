<template>
  <div class="search-list-vue frame-page h-panel">
  <div class="h-panel h-panel-no-border">
    <div class="h-panel-bar">
      <span class="h-panel-title" v-font="16">爬虫信息</span>
    </div>
    <div class="h-panel-body">
      <Form :readonly="true" mode="twocolumn">
        <FormItem label="爬虫名称">{{formData.crawlerConfig.crawlerName}}</FormItem>
        <FormItem label="源地址" prop="baseUrl" class="text-ellipsis" :title="formData.crawlerConfig.baseUrl">
          {{formData.crawlerConfig.baseUrl}}
        </FormItem>
        <FormItem label="地址规则" prop="baseUrl" class="text-ellipsis" :title="formData.crawlerConfig.baseUrl">
          {{formData.crawlerConfig.matchUrl}}
        </FormItem>
        <FormItem label="源站类型" prop="websiteType">{{formData.crawlerConfig.websiteType}}</FormItem>
        <FormItem label="是否共享" prop="isShare">{{formData.crawlerConfig.shareType==='1'?'共享':'私有'}}</FormItem>
      </Form>
    </div>
    <div class="h-panel-bar">
      <span class="h-panel-title" v-font="16">字段信息</span>
    </div>
    <div class="h-panel-body" v-for="(item,index) of formData.fieldConfig">
      <Form labelPosition="left" :readonly="true" mode="threecolumn" >
        <FormItem label="字段ID">{{item.id}}</FormItem>
        <FormItem label="字段名称">{{item.fieldName}}</FormItem>
        <FormItem label="字段类型">{{item.fieldType}}</FormItem>
        <FormItem label="字段分组">{{item.fieldGroup}}</FormItem>
        <FormItem label="说明">{{item.description}}</FormItem>
        <FormItem label="创建时间">{{item.createTime}}</FormItem>
      </Form>
      <div class="bottom-line"></div>
    </div>
    <p class="text-center" >
      <h-button  @click="$router.go(-1)">返回</h-button>
      <h-button class="h-btn h-btn-primary"  @click="">编辑</h-button>

    </p>
    <br>
  </div>
  </div>
</template>

<script>
    import Ajax from "../../../js/common/ajax";
    import Request from "../../../js/common/request";

    export default {
        name: "crawler-detial",
      data(){
          return {
            initData:{

            },
            formData: {
              crawlerConfig: {
                crawlerName: null,
                baseUrl: null,
                matchUrl: null,
                websiteType: null,
                shareType: 0
              },
              fieldConfig: []
            }
          }
      },
      created(){

      },
      mounted() {
        let that = this;
        let crawlerId = this.$route.query.crawlerId;
        Ajax.get(`/manager/crawler/qryCrawlerDetail/${crawlerId}`,this.params).then((res)=>{
          console.log(res)
          that.formData.crawlerConfig = res.data;
        });
        Ajax.get(`/manager/crawler/qryCrawlerField/${crawlerId}`,this.params).then((res)=>{
          console.log(res)
          that.formData.fieldConfig = res.data;
        });
      }
    }
</script>

<style scoped>

</style>
