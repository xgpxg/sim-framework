<template>
  <div>
    <el-form :model="form" :rules="rules" ref="form" label-width="100px" size="small">
      <el-form-item label="数据源名称" prop="name">
        <el-input v-model="form.name" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="类型">
        <el-radio-group v-model="form.type">
          <el-radio label="1">关系型数据库</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-divider><span class="secondary">数据源配置</span></el-divider>
      <el-form-item label="连接串" prop="configJson.url">
        <el-input v-model="form.configJson.url"></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="configJson.username">
        <el-input v-model="form.configJson.username"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="configJson.password">
        <el-input v-model="form.configJson.password" show-password></el-input>
      </el-form-item>
      <el-form-item>
        <span>{{testConnectionResult}}</span>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
  export default {
    name: "add-ds",
    props: {},
    data() {
      return {
        isShow: false,
        form: {
          type: '1',
          dsConfigTemplateId: '1',
          configJson: {
            url: null,
            username: null,
            password: null,
          }
        },
        rules: {
          name: {required: true, message: '请输数据源名称'},
          'configJson.url': {required: true, message: '请输入连接串'},
          'configJson.username': {required: true, message: '请输入用户名'},
          'configJson.password': {required: true, message: '请输入密码'}
        },
        testConnectionResult:null
      }
    },
    methods: {
      save() {

        this.$refs['form'].validate((valid) => {
          if (valid) {
            let reqParam = {
              name: this.form.name,
              config: JSON.stringify(this.form.configJson),
              type: this.form.type,
              dsConfigTemplateId: this.form.dsConfigTemplateId,
            };

            this.R.postJson('/api/db-query/dsm', reqParam).then(res => {
              if(res.code === 0){
                this.$message({
                  type: 'success',
                  message: '新增成功'
                });
              }else{
                this.$message({
                  type: 'error',
                  message: res.msg
                });
              }

              this.search();
            })
          } else {
            return false;
          }
        });
        //Save data.

      },
      testConnection() {

      }
    }
  }
</script>

<style scoped>
  /deep/ .el-form-item__label {
    font-weight: 500;
  }
</style>
