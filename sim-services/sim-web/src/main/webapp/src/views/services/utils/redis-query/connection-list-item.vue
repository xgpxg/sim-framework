<template>
  <div>
    <el-collapse-item :name="instance.id">
      <template slot="title">
        <div style="width: 100%;">
          <!--单机-->
          <p style="float: left;" v-if="instance.connectType==='standalone'">
            <svg-icon v-if="!instance.active" icon-class="redis"
                      style="color: grey"/>
            <svg-icon v-if="instance.active" icon-class="redis"
                      style="color: green"/>
            <span
              style="margin-left: 10px">{{instance.name}}</span>
            <el-tag size="mini" type="" effect="plain"
                    style="margin-left: 10px">单机
            </el-tag>


            <el-tag v-if="instance.active" size="mini"
                    type="success" effect="plain"
                    style="margin-left: 10px">已连接
            </el-tag>
          </p>
          <!--集群-->
          <p style="float: left;" v-if="instance.connectType==='cluster'">
            <svg-icon v-if="!instance.active" icon-class="redis"
                      style="color: grey"/>
            <svg-icon v-if="instance.active" icon-class="redis"
                      style="color: green"/>
            <span
              style="margin-left: 10px">{{instance.name}}</span>
            <el-tag size="mini" type="" effect="plain"
                    style="margin-left: 10px">集群
            </el-tag>
            <el-tag v-if="instance.active" size="mini"
                    type="success" effect="plain"
                    style="margin-left: 10px">已连接
            </el-tag>
          </p>
          <p align="right" style="margin-right: 10px">
            <!--  <el-tag size="small" type="" @click.stop="">监控
              </el-tag>-->
            <el-tag size="small" type="" @click.stop="connect(-1)" effect=""
                    class="ml5">连接
            </el-tag>
          </p>
        </div>
      </template>
      <!--单机-->
      <div class="info" v-if="instance.connectType==='standalone'">
        <el-row class="row">
          <el-col span="4" class="title">
            主机
          </el-col>
          <el-col span="20">
            {{instance.host}}
          </el-col>
        </el-row>
        <el-row class="row">
          <el-col span="4" class="title">
            端口
          </el-col>
          <el-col span="20">
            {{instance.port}}
          </el-col>
        </el-row>
        <el-row class="row">
          <el-col span="4" class="title">
            选择DB
          </el-col>
          <el-col span="20">
            <el-select v-model="database" placeholder="选择数据库" size="mini"
                       @visible-change="loadDb"
                       style="width: 40%" @change="changeDb">
              <el-option v-for="db of databases" :label="db" :value="db"></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>

      <!--集群-->
      <div class="info" v-if="instance.connectType==='cluster'">

        <el-row class="row">
          <el-col span="4" class="title">
            节点
          </el-col>
          <el-col span="20">
            <div v-for="node of instance.nodes.split(',')">{{node}}</div>
          </el-col>
        </el-row>
      </div>

      <small style="float: right">
        <!--<el-button type="text" size="mini">编辑</el-button>-->
        <el-button type="text" size="mini" @click="del">删除</el-button>
      </small>
    </el-collapse-item>
  </div>
</template>

<script>
  export default {
    name: "connect-list-item",
    props: {
      instance: Object
    },
    data() {
      return {
        active: false,
        databases: [],
        database: 0
      }
    },
    mounted() {

    },
    methods: {
      loadDb() {
        let that = this;
        that.databases = [];
        that.R.get('/api/cache-service/showDb', {}).then(res => {
          for (let i = 0; i < parseInt(res.data.databases); i++) {
            that.databases.push(i);
          }
        });
      },
      changeDb() {
        this.connect();
      },
      connect() {
        let that = this;
        let reqParam = {
          host: that.instance.host,
          port: that.instance.port,
          cluster: that.instance.cluster,
          database: that.database
        };
        that.R.postJson('/api/cache-service/connect', reqParam).then(res => {
          if (res.code === 0) {
            that.$message({
              type: 'success',
              message: '连接成功'
            });
            that.active = true;
            that.$emit('success', that.instance);
          }
        })
      },
      del() {
        let that = this;
        that.R.delete('/api/cache-service/deleteInstance?cacheInstanceId=' + this.instance.cacheInstanceId, {}).then(res => {
          that.$message({
            type: 'success',
            message: '删除成功'
          });
          that.$emit('del', that.instance);
        })
      }
    }
  }
</script>

<style scoped>
  .title {
    /*border-left: #20a0ff 5px solid;*/
    padding-left: 10px;
    border-radius: 1px;
  }

  .row {
    margin-top: 5px;
  }

  .info {
    color: #909399;
  }

  .ml5 {
    margin-left: 5px;
  }

  /deep/ .el-collapse-item__header {
    border: none;
  }

  /deep/ .is-active {
    background-color: #f6f6f6;
  }

  /deep/ .el-collapse-item {
    padding: 10px;
  }

  /deep/ .el-collapse-item__wrap {
    background-color: #f6f6f6;
    border: none;
    padding: 10px;
  }
</style>
