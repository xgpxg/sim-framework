<!--新增角色-->
<template>
  <div>
    <el-form :model="form" ref="form" :rules="rules">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="form.roleName"></el-input>
      </el-form-item>
      <el-form-item label="角色编码(不可重复)" prop="roleCode">
        <el-input v-model="form.roleCode"></el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  export default {
    name: "add-role-dialog",
    props:{
      data:{
        type:Object
      }
    },
    data() {
      return {
        form: {
          roleName: null,
          roleCode: null
        },
        rules: {
          roleName: {required: true, message: '请输入角色名称'},
          roleCode: {required: true, message: '请输入角色编码'},
        }
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init(){
        debugger;
        if(this.data){
          this.form = this.U.copy(this.data);
        }
      },
      save(callback) {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            let reqParam = this.U.copy(this.form);
            if(this.form.roleId){
              this.R.forUserApi.patchJson('/role', reqParam).then(res => {
                this.$message.success('修改成功');
                callback(true);
              })
            }else{
              this.R.forUserApi.postJson('/role', reqParam).then(res => {
                this.$message.success('保存成功');
                callback(true);
              })
            }
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
