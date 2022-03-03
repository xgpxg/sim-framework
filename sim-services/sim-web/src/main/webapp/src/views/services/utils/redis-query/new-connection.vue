<template>
  <div style="margin-bottom: 10px">
    <el-button size="small" @click="dialogVisible=true">+ 新建连接</el-button>

    <el-dialog title="新建连接" :visible.sync="dialogVisible">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="formData.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="主机">
          <el-input v-model="formData.host" autocomplete="off"
                    :placeholder="hostInputPlaceholder"></el-input>
        </el-form-item>
        <el-form-item label="端口" v-if="connectType==='standalone'">
          <el-input v-model="formData.port" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="连接模式">
          <el-select v-model="connectType" placeholder="请选择连接模式">
            <el-option label="单机" value="standalone"></el-option>
            <el-option label="集群" value="cluster"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="">
          <span style="color: red" v-if="!testConnectMsg.success">{{testConnectMsg.msg}}</span>
          <span style="color: green" v-if="testConnectMsg.success">{{testConnectMsg.msg}}</span>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="testConnect">测试连接</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit">保 存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "new-connection",
    data() {
      return {
        dialogVisible: false,
        formData: {
          name: null,
          host: '127.0.0.1',
          port: 6379,
          cluster: null
        },
        connectType: 'standalone',
        hostInputPlaceholder: '',
        testConnectMsg: {
          msg: null,
          success: false
        }
      }
    },
    watch: {
      connectType(newVal) {
        if (newVal === 'cluster') {
          this.formData.host = null;
          this.hostInputPlaceholder = 'IP:PORT(多个逗号分割)';
        } else {
          this.formData.host = null;
          this.hostInputPlaceholder = '';
        }
      }
    },
    methods: {
      testConnect() {
        let that = this;
        //以集群方式连接
        if (this.connectType === 'cluster') {
          this.formData.cluster = {
            nodes: that.formData.host.split(",")
          }
        } else {
          this.formData.cluster = null;
        }

        let reqParam = this.U.copy(this.formData);
        that.R.postJson('/api/cache-service/connect', reqParam).then(res => {
          if (res.code === 0) {
            that.$message({
              type: 'success',
              message: res.data
            });
            that.testConnectMsg.msg = res.data;
            that.testConnectMsg.success = true;
          } else {
            that.testConnectMsg.msg = res.msg;
            that.testConnectMsg.success = false;
          }
        });
      },
      onSubmit() {
        let that = this;

        let reqParam = {};
        reqParam.name = this.formData.name?this.formData.name:this.formData.host.split(',')[0];
        reqParam.host = this.formData.host;
        reqParam.port = this.formData.port;
        reqParam.connectType = this.connectType;
        reqParam.nodes = this.formData.host;
        that.R.postJson('/api/cache-service/addInstance', reqParam).then(res => {
          that.$message({
            type: 'success',
            message: '添加成功'
          });
          that.$emit('reload');
        });
        this.dialogVisible = false;

      }
    }
  }
</script>

<style scoped>

</style>
