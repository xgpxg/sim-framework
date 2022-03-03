
<template>
  <div class="app-home-vue frame-page">
    <el-row >
      <el-col :span="6" v-for="d of appList">
        <div class="pl-5">
          <el-card>
            <div slot="header" class="clearfix">
              <span>
                <i v-if="d.status==='1'" class="h-icon-success" style="color: green;"> </i>
                <i v-if="d.status!=='1'" class="h-icon-warn" style="color: orange;"> </i>
                {{d.appName}}
              </span>
              <el-tag size="" style="float: right; padding: 3px 0"><span v-if="d.status!=='1'" style="color: orange;">not running</span></el-tag>
            </div>
            <div class="h-panel-bar">
            </div>
            <div class="h-panel-body">
              <el-form :readonly="true">
                <el-form-item label="应用名称：">{{d.appName}}</el-form-item>
                <el-form-item label="应用地址：">{{d.appAddr}}</el-form-item>
                <el-form-item label="接口总数：">{{d.apiNum}}</el-form-item>
                <el-form-item label="模拟接口：">{{d.simulationNum}}</el-form-item>
              </el-form>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>

  </div>
</template>
<script>
  export default {
    data() {
      return {
        appList: []
      };
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        let that = this;

        that.getAppList();

        setInterval(() => {
          that.getAppList();
        }, 60000);
      },
      async getAppList() {
        //this.$Loading();
        let that = this;
        let res = await that.R.get("/api/sim-api/api/app-list");
        let list = res.data;
        if (list && list.length > 0) {
          for (let i = 0; i < list.length; i++) {
            let d = await that.R.get("/api/sim-api/api/status", {appAddr: list[i].appAddr});
            list[i]['status'] = d.data;
          }
          that.appList = list;
        }
        //this.$Loading.close();
      }
    }
  };


</script>
<style lang='scss'>

</style>
