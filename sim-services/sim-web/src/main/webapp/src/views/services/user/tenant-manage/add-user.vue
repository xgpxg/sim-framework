<template>
  <div class="app-container">
    <el-card>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px"
               :inline="false">
        <el-row>
          <el-col :span="4">
            <el-form-item label="用户头像">
              <el-upload
                class="avatar-uploader"
                action="https://jsonplaceholder.typicode.com/posts/"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
                <img v-if="imageUrl" :src="imageUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="20">
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="form.userName" type="text" maxlength="10"
                        show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="用户简介">
              <el-input v-model="form.description" type="text" maxlength="100"
                        show-word-limit></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户类型" prop="userType">
              <el-radio-group v-model="form.userType">
                <el-radio label="1000">普通用户</el-radio>
                <el-radio label="2000">管理员</el-radio>
                <el-radio label="3000">租户</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" type="text" maxlength="11"
                        show-word-limit></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" type="email"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">

          </el-col>
          <el-col :span="12">
            <el-form-item label="QQ号">
              <el-input v-model="form.qq" type="text"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期">
              <el-date-picker
                v-model="form.date"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark"></el-input>
        </el-form-item>
        <el-form-item label="">
          <el-button size="small" @click="$router.go(-1)">取消</el-button>
          <el-button type="primary" size="small" @click="save">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script>
  export default {
    name: 'add-user-base-info',
    data() {
      return {
        form: {
          userName: null,
          description: null,
          userType: '1000',
          phone: null,
          email: null
        },
        rules: {
          userName: {required: true, message: '请输入用户名'},
          userType: {required: true, message: '请选择用户类型'},
          phone: {required: true, message: '请输入手机号'},
          password: {required: true, message: '请输入初始密码'},
        }
      }
    },
    computed: {
      userId() {
        return this.$route.query.userId;
      }
    },
    mounted() {
      if (this.userId) {
        this.loadUserDetail();
      }
    },
    methods: {
      save() {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            let reqPram = this.U.copy(this.form);

            if (this.userId) {
              this.R.forUserApi.patchJson('/userManage', reqPram).then(res => {
                this.$message.success('修改成功');
              })
            } else {
              this.R.forUserApi.postJson('/userManage', reqPram).then(res => {
                this.$message.success('添加成功');
                setTimeout(() => {
                  this.$router.go(-1);
                }, 500)
              })
            }
          }
        });
      },
      loadUserDetail() {
        this.R.forUserApi.get('/userManage/' + this.userId).then(res => {
          this.form = res.data;
        })
      }
    }
  }
</script>

<style scoped>
  /deep/ .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  /deep/ .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }

  /deep/ .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
  }

  /deep/ .avatar {
    width: 100px;
    height: 100px;
    display: block;
  }

  /deep/ .el-form-item__label {
    font-weight: normal;
  }

</style>
