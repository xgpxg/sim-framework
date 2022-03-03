<style>
  .container .shell-container {
    margin: auto;
    background-color: #000000;
    width: 900px;
    height: 500px;

    display: flex;
    flex-direction: column;
  }

  .container .shell-container .header-shell {
    margin: auto;
    background-color: white;
    width: 100%;
    height: 35px;
    display: flex;
  }

  .container .shell-container .body-shell {
    margin: 9px 0;
    overflow-y: scroll;
    height: 470px;
    width: 100%;
    line-height: 0px;
    display: flex;
    flex-direction: column;
    font-size: 14px;
  }

  .container .shell-container .body-shell .DEBUG {
    color: dodgerblue;
    margin: 10px 0;
  }

  .container .shell-container .body-shell .INFO {
    color: white;
    margin: 10px 0;
  }

  .container .shell-container .body-shell .WARN {
    color: orange;
    margin: 10px 0;
  }

  .container .shell-container .body-shell .ERROR {
    color: red;
    margin: 10px 0;
  }

  @keyframes pong {
    0% {
      background-color: #DFDFDF;
    }
    50% {
      background-color: #DFDFDF;
    }
    51% {
      background-color: #000000;
    }
  }

  .task-log h-modal-header {
    color: red;
  }

  .task-log fieldset {
    border: none;
    padding: 0;
    border-top: 1px solid darkgray;
    text-align: center;
    width: 300px;
    margin: auto;
  }

  .task-log legend {
    padding: 0 10px;
    font-size: 16px;
    color: darkgray;
  }


</style>
<template>
  <div class="task-log">
    <!-- h-modal-header 将自带modal头部样式 -->
    <header class="h-modal-header">{{taskInfo.taskName}} - 执行日志</header>
    <div style="padding:0">
      <div class="container">
        <div class="shell-container">
          <div class="body-shell">
            <p class="INFO"></p>
            <div v-for="(log,index) of initData.logList" :class="log.level">
              <div style="line-height: 50px"
                   v-if="initData.splitLineNumArray[index]">
                <span style="color: lightgrey">{{taskInfo.taskName}}，实例ID：{{log.instanceId}}</span>
              </div>
              <div>
                <span v-text="log.instanceId"></span>&nbsp;
                <span v-text="log.createDate"></span>&nbsp;
                <span v-text="log.level" style="width: 50px;display: inline-block"></span>&nbsp;
                [<span v-text="taskInfo.className"></span>]&nbsp;:
                <span v-text="log.log"></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Ajax from "../../js/common/ajax";


  export default {
    name: "task-log-modal",

    props: {
      taskInfo: Object
    },
    data() {
      return {
        rules: {
          required: []
        },
        initData: {
          logList: [],
          splitLineNumArray: []
        },
        params: {
          reqMethod: null
        },
        dict: {
          reqMethod: [],
          reqMethods: ['GET', 'POST', 'PATCH', 'PUT', 'DELETE']
        }
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        let that = this;
        let taskId = this.taskInfo.id;
        let reqParam = {
          taskId: taskId
        };
        Ajax.get('/simTaskManager/logList', reqParam).then((res) => {
          let list = res.data.list;
          that.initData.logList = list;
          let lastInstId = -1;
          for (let i in list) {
            let log = list[i];
            if (log.instanceId != lastInstId) {
              that.initData.splitLineNumArray[i] = true;
              lastInstId = log.instanceId;
            }
          }
          console.log(that.initData.splitLineNumArray)
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
      del() {
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
