<template>
  <div style="margin-top: 10px">
    <NewConnection @reload="reload"></NewConnection>
    <el-collapse accordion v-model="activeName">
      <connect-list-item v-for="item of initData.redisList"
                         :instance="item"
                         @success="connectSuccess"
                         @del="onDel"
      ></connect-list-item>
    </el-collapse>
  </div>
</template>

<script>
  import ConnectListItem from "./connection-list-item";
  import NewConnection from "./new-connection";

  export default {
    name: "connect-list",
    components: {NewConnection, ConnectListItem},
    data() {
      return {
        activeName: null,
        initData: {
          redisList: [{
            id: 1,
            name: '',
            host: '127.0.0.1',
            port: '6379',
            type: 'standalone',
            active: false
          }/*, {
            id: 4,
            name: '',
            host: '192.168.10.157:7000',
            port: '',
            type: 'cluster',
            active: false,
            cluster: {
              nodes: ['192.168.10.157:7000']
            }
          }*/]
        }
      };
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        let that = this;

        that.R.get('/api/cache-service/qryInstance', {}).then(res => {
          res.data.forEach(item => {
            if (!item.active) {
              item.active = false;
            }
            if (item.connectType === 'cluster') {
              item.cluster = {nodes: item.nodes.split(',')}
            }
          });
          that.initData.redisList = res.data;
        })
      },
      /**
       * 新增后重新刷新列表
       */
      reload() {
        this.init();
      },
      connectSuccess(instance) {
        this.initData.redisList.forEach(item => {
          if (item.cacheInstanceId === instance.cacheInstanceId) {
            item.active = true;
          } else {
            item.active = false;
          }
        });
      },
      /**
       * 删除后移除列表
       * @param instance
       */
      onDel(instance) {
        this.initData.redisList.splice(instance, 1);
      }
    }
  }
</script>

<style scoped>

</style>
