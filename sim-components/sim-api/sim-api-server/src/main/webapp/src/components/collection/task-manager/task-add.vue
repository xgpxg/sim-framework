<template>
  <div class="search-list-vue frame-page h-panel">
    <div class="h-panel-bar"><span class="h-panel-title">新建采集任务</span></div>
    <div class="h-panel-body">
      <Row :space="50">
        <Cell width="15">
          <Form :label-width="110" :model="formData" ref="form" :rules="rules" showErrorTip>
            <FormItem label="任务名称" prop="taskName">
              <input type="text" v-model="formData.taskName"/>
            </FormItem>
            <FormItem label="任务类型" prop="taskType">
              <Select v-model="formData.taskType" :datas="initData.taskType"></Select>
            </FormItem>
            <FormItem label="爬虫" >
              <div v-padding="5" @click="opened=true" class="text-hover"><i class="h-icon-edit"></i> 选择爬虫</div>
                  <template>
                    <h-button  class="h-btn" v-if="crawlerInfo!=null" style="width:100%">
                      <Form labelPosition="left" readonly>
                        <FormItem label="爬虫ID">{{crawlerInfo.crawlerId}}</FormItem>
                        <FormItem label="爬虫名称">{{crawlerInfo.crawlerName}}</FormItem>
                        <FormItem label="源站类型">{{crawlerInfo.websiteType}}</FormItem>
                        <FormItem label="源站名称">{{crawlerInfo.websiteName}}</FormItem>
                        <FormItem label="源地址">{{crawlerInfo.baseUrl}}</FormItem>
                        <FormItem label="匹配地址">{{crawlerInfo.matchUrl}}</FormItem>
                        <FormItem label="采集字段">{{crawlerInfo.fields}}</FormItem>
                      </Form>
                    </h-button>
                  </template>
            </FormItem>
            <FormItem label="预计采集量" prop="crawlMaxNum">
              <input type="text" v-model="formData.crawlMaxNum"/>
            </FormItem>
            <FormItem v-if="formData.taskType==='2000'" label="开始时间" prop="startTime">
              <DatePicker v-model="formData.startTime" type="datetime"></DatePicker>
            </FormItem>
            <FormItem v-if="formData.taskType==='2000'" label="结束时间" prop="endTime">
              <DatePicker v-model="formData.endTime" type="datetime"></DatePicker>
            </FormItem>
            <FormItem label="采集间隔" prop="crawlMaxNum">
              <Select v-model="formData.timeInterval" :datas="initData.timeInterval"></Select>
            </FormItem>
            <FormItem label="线程数" prop="crawlMaxNum">
              <Select v-model="formData.threadNum" :datas="initData.threadNum"></Select>
            </FormItem>
            <FormItem label="节点数" prop="crawlMaxNum">
              <Select v-model="formData.nodeNum" :datas="initData.nodeNum"></Select>
            </FormItem>
            <FormItem label="任务说明" prop="description">
              <textarea type="text" v-model="formData.description"></textarea>
            </FormItem>
            <FormItem>
              <Button color="primary" @click="submit">提交</Button>
            </FormItem>
          </Form>
        </Cell>
        <Cell width="9">
          <div>历史任务（可复制）</div>
        </Cell>
      </Row>
      <CrawlerModal v-model="crawlerInfo" :opened="opened" v-on:confirm="confirm" v-on:cancel="cancel"></CrawlerModal>
    </div>
  </div>
</template>

<script>
  import CrawlerModal from "../crawler-manager/crawler-modal";
  import Ajax from "../../../js/common/ajax";
  import Request from "../../../js/common/request";
  export default {
    name: "task-add",
    components: {CrawlerModal},
    data() {
      return {
        opened: false,
        initData:{
          threadNum:[1],
          nodeNum:[1],
          timeInterval: {100:'0.1秒',500:'0.5秒',1000:'1秒',2000:'2秒'},
          taskType:{'1000':'单次任务','2000':'周期性任务'}
        },
        formData: {
          taskName:null,
          crawlMaxNum:null,
          startTime:null,
          endTime:null,
          description:null,
          crawlerInfo: null,
          timeInterval: null,
          threadNum: null,
          nodeNum: null,
          crawlerId: null,
          taskType: null,
        },
        crawlerInfo: null,
        rules: {
          //url: ['baseUrl'],
          //required: ['crawlerName', 'crawlerType', 'baseUrl', 'websiteType']
        }
      }
    },
    watch: {

    },
    created(){
      let that = this;
      Request.Dict.get("crawl_mode").then((res)=>{
        that.initData.crawMode = res.data;
      })
    },
    methods: {
      confirm(data) {
        //关闭modal
        this.opened = false;
        this.crawlerInfo = data;
        //this.formData.crawlerInfo = data;
      },
      cancel() {
        //关闭modal
        this.opened = false;
      },
      submit(){
        this.formData.crawlerId = this.crawlerInfo.crawlerId;
        Ajax.postJson('/manager/task/createTask',this.formData).then((res)=>{
          console.log(res);
        })
      }
    }
  }
</script>

<style scoped>

</style>
