<template>
  <div>
    <!-- h-modal-header 将自带modal头部样式 -->
    <header class="h-modal-header">{{apiInfo.appName}}</header>
    <div style="padding:15px">
      <Form :label-width="110" :model="params" :rules="rules" ref="form" :top="0.2" showErrorTip>
        <FormItem label="API地址：">
          <input type="text" v-model="apiInfo.apiUrl" readonly style="border: none"/>
        </FormItem>
        <FormItem label="请求方式：" prop="reqMethod">
          <input type="text" v-model="apiInfo.reqMethod" readonly style="border: none"/>
        </FormItem>
        <FormItem label="模拟数据：" prop="resData">
          <textarea type="text" v-model="params.resData" placeholder="" rows="10"/>
        </FormItem>
      </Form>
    </div>
    <!-- h-modal-footer 将自带modal底部样式 -->
    <footer class="h-modal-footer">
      <button class="h-btn h-btn-primary" @click="success">保存</button>
      <button class="h-btn h-btn-red" @click="del" v-if="apiInfo.apiType==='2000'">删除</button>
      <button class="h-btn" @click="close">关闭</button>
    </footer>
  </div>
</template>

<script>
  import Ajax from "../../js/common/ajax";


  export default {
    name: "api-detail-modal",

    props: {
      apiInfo: Object
    },
    data() {
      return {
        rules: {
          required: []
        },
        params: {
          reqMethod: null,
          resData: null
        },
        dict: {
          reqMethod: [],
          reqMethods: ['GET','POST','PATCH','PUT','DELETE']
        }
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        let reqMethodDict = this.apiInfo.reqMethod.split(',');
        this.dict.reqMethod = reqMethodDict;

        let that = this;
        let reqParam = {};
        reqParam.apiUrl = this.apiInfo.apiUrl;
        reqParam.appName = this.apiInfo.appName;
        reqParam.requestType = this.apiInfo.reqMethod;
        Ajax.get ('/api/sim-api-data',reqParam).then((res)=>{
            that.params.resData = res.data;
        })
      },
      success() {
        let validResult = this.$refs.form.valid();
        if (!validResult.result)
          return;

        let that = this;
        let reqParam = {};
        reqParam.apiUrl = this.apiInfo.apiUrl;
        reqParam.appName = this.apiInfo.appName;
        reqParam.reqMethod = this.apiInfo.reqMethod;
        reqParam.resData = this.params.resData;
        Ajax.postJson("/api/update-api", reqParam).then((res) => {
          if (res.code === 0) {
            this.$emit('success', true);
            //this.close();
            that.$Message.success('修改成功');
          }
        });
      },
      close() {
        this.$emit('close');
      },
      del(){
        let that = this;
        let reqParam = {};
        reqParam.apiUrl = this.apiInfo.apiUrl;
        reqParam.appName = this.apiInfo.appName;
        reqParam.reqMethod = this.apiInfo.reqMethod;
        Ajax.postJson("/api/delete-api", reqParam).then((res) => {
          if (res.code === 0) {
            this.$emit('success', true);
            this.close();
            that.$Message.success('删除成功');
          }
        });
      }
    }
  }
</script>

<style scoped>

</style>
