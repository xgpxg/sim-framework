<template>
  <div>
    <div class="search-list-vue frame-page h-panel">
      <div class="h-panel-bar">
        <span class="h-panel-title">新增API</span>
      </div>
    <div style="padding:15px">
      <Form :label-width="110" :model="params" :rules="rules" ref="form" :top="0.2" showErrorTip>
        <FormItem label="选择应用：" prop="appInfo">
          <Select :datas="appInfo" v-model="params.appInfo" @change="selectChange" type="object"></Select>
        </FormItem>
        <FormItem label="API名称：" prop="apiName">
          <input type="text" v-model="params.apiName" />
        </FormItem>
        <FormItem label="API地址：" prop="apiUrl">
          <input type="text" v-model="params.apiUrl" />
        </FormItem>
        <FormItem label="请求方式：" prop="reqMethod">
          <Select :datas="dict.reqMethod" v-model="params.reqMethod"></Select>
        </FormItem>
        <FormItem label="模拟数据：" prop="resData">
          <textarea type="text" v-model="params.resData" placeholder="" rows="10"/>
        </FormItem>
        <FormItem>
          <button class="h-btn h-btn-primary" @click="success">保存</button>
          <button class="h-btn" @click="reset" >重置</button>
        </FormItem>
      </Form>
    </div>
  </div>
  </div>
</template>

<script>
  export default {
    name: "api-add-modal",

    props: {
      appName: String
    },
    data() {
      return {
        rules: {
          required: ['appInfo','appName','apiName','appAddr','apiUrl','requestType','reqMethod']
        },
        params: {
          reqMethod: null,
          resData: null,
          apiUrl:null,
          apiName:null,
          appName:null,
          appAddr:null,
          appInfo:null
        },
        dict: {
          reqMethod: ['GET','POST','PATCH','PUT','DELETE']
        },
        appInfo:[]
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        this.getAppList();
      },
      success() {
        let validResult = this.$refs.form.valid();
        if (!validResult.result)
          return;

        let that = this;
        /*let reqParam = {};
        reqParam.appName=this.appName;
        reqParam.apiUrl =this.params.apiUrl;
        reqParam.apiName = this.params.apiName;
        reqParam.requestType = this.params.reqMethod;
        reqParam.appAddr = this.params.appAddr;*/
        this.params.apiType='2000';
        this.R.postJson("/api/sim-api/add-api", this.params).then((res) => {
          if (res.code === 0) {
            this.$emit('success', true);
            //this.close();
            that.$Message.success('添加成功');
          }
        });
      },
      reset() {
        this.params = {};
        this.$refs.form.resetValid();
      },
      getAppList() {
        let that = this;
        this.R.get("/api/sim-api/app-list").then((res) => {
          res.data.forEach((d) => {
            let app = {};
            app.appName = d.appName;
            app.appAddr = d.appAddr;
            let dic = {itemText:app.appName,itemCode:app.appAddr};
            that.appInfo.push(dic);
          });
        });
      },
      selectChange(data){
        this.params.appName = data.itemText;
        this.params.appAddr = data.itemCode;
      }
    }
  }
</script>

<style scoped>

</style>
