<style lang='less'>
  .app-home-vue {
    .home-part-body {
      height: 350px;

      .echarts-vue {
        height: 350px;
      }
    }

    .home-part-body2 {
      height: 420px;

      .echarts-vue {
        height: 420px;
      }
    }

    .progress-div {
      > p {
        padding: 8px 0;
      }

      .h-progress {
        &-title {
          color: @dark2-color;
          font-size: 15px;
        }

        &-text {
          width: 40px;
        }
      }
    }

  }
</style>
<template>
  <div class="app-home-vue frame-page">
    <Row :space="30">
      <Cell :width="8" v-for="d of appList">
        <div v-width="400">
          <div class="h-panel">
            <div class="h-panel-bar">
              <span class="h-panel-title">
                <i v-if="d.status==='1'" class="h-icon-success" style="color: green;"> </i>
                <i v-if="d.status!=='1'" class="h-icon-warn" style="color: orange;"> </i>
                {{d.appName}}</span>
              <span v-if="d.status!=='1'" class="h-panel-right" style="color: orange;">not running</span>
            </div>
            <div class="h-panel-body">
              <Form :readonly="true">
                <FormItem label="应用名称：">{{d.appName}}</FormItem>
                <FormItem label="应用地址：">{{d.appAddr}}</FormItem>
                <FormItem label="接口总数：">{{d.apiNum}}</FormItem>
                <FormItem label="模拟接口：">{{d.simulationNum}}</FormItem>
              </Form>
            </div>
          </div>
        </div>
      </Cell>
    </Row>
  </div>
</template>
<script>
  import Ajax from "../../js/common/ajax";

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
        this.$Loading();
        let that = this;
        let res = await Ajax.get("/api/app-list");
        let list = res.data;
        if (list && list.length > 0) {
          for (let i = 0; i < list.length; i++) {
            let d = await Ajax.get("/api/status", {appAddr: list[i].appAddr});
            list[i]['status'] = d.data;
          }
          that.appList = list;
        }
        this.$Loading.close();
      }
    }
  };


</script>
