<template>
  <div>
    <div class="title">基础配置</div>
    <el-form ref="form" :model="form" :rules="rules" label-position="left"
             label-width="auto" size="small" :disabled="action==='detail'">
      <el-form-item label="路由名称" prop="routeName">
        <el-input v-model="form.routeName" maxlength="20"
                  show-word-limit placeholder="请填写路由名称"></el-input>
      </el-form-item>
      <el-form-item label="路由规则" prop="path">
        <el-input v-model="form.path" placeholder="请填写路由规则"></el-input>
      </el-form-item>
      <el-form-item label="服务地址" prop="url">
        <el-input v-model="form.url"
                  placeholder="请填写服务完整地址,例如http://service.com"></el-input>
      </el-form-item>
      <el-form-item label="服务ID" prop="serviceId">
        <el-row :gutter="10">
          <el-col :span="18">
            <el-input v-model="form.serviceId"
                      placeholder="如果使用了服务注册中心可使用服务ID"></el-input>
          </el-col>
          <el-col :span="4">
            <el-button size="small" v-if="!routeId">检测服务</el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="路由分组" prop="url">
        <el-select v-model="form.groupId"
                   placeholder="请选择分组">
          <el-option
            v-for="item in initData.groups"
            :key="item.groupId"
            :label="item.groupName"
            :value="item.groupId">
          </el-option>
        </el-select>
      </el-form-item>
      <div class="title">扩展配置</div>
      <el-form-item label="移除前缀" prop="retryable">
        <el-checkbox v-model="form.stripPrefix" :true-label="true"
                     :false-label="false">移除
        </el-checkbox>
        <span class="secondary ml30">(移除路径前缀，如路由规则为/api/abc/**，则转发时移除前缀/api/abc/)</span>
      </el-form-item>
      <el-form-item label="失败重试" prop="retryable">
        <el-checkbox v-model="form.retryable" :true-label="true"
                     :false-label="false">允许
        </el-checkbox>
        <span class="secondary ml30">(允许失败重试，仅当配置了服务ID时有效)</span>
      </el-form-item>
      <el-form-item label="保留header" prop="customSensitiveHeaders">
        <el-checkbox v-model="form.customSensitiveHeaders" :true-label="true"
                     :false-label="false">保留
        </el-checkbox>
        <span class="secondary ml30">(允许转发时将调用方header转发到下游服务，取消后可能会导致鉴权异常，建议保留)</span>
      </el-form-item>
      <el-form-item label="Header过滤" prop="sensitiveHeaders">
        <el-input v-model="form.sensitiveHeaders"
                  placeholder="转发到下游服务时，需要过滤的敏感header，如认证信息等"></el-input>
      </el-form-item>
      <el-form-item label="版本号" prop="version">
        <el-input v-model="form.version" placeholder="版本号,如1.0"></el-input>
        <span class="secondary ml30">(可用于灰度发布)</span>
      </el-form-item>
      <div class="title">限流配置</div>
      <p class="secondary">暂未配置限流规则</p>
    </el-form>
  </div>
</template>

<script>
  export default {
    name: "add-route",
    props: {
      routeId: {type: Number},
      action: {type: String}
    },
    data() {
      return {
        initData: {
          groups: [
            {groupId: -1, groupName: '默认分组'}
          ]
        },
        form: {
          routeName: null,
          path: null,
          url: null,
          serviceId: null,
          retryable: null,
          sensitiveHeaders: null,
          customSensitiveHeaders: true,
          version: null,
          groupId: null,
          stripPrefix:false
        },
        rules: {
          routeName: {required: true, message: '请填写路由名称'},
          path: {required: true, message: '请填写路由规则'},
          groupId: {required: true, message: '请选择分组'},
        }
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        if (this.routeId) {
          this.loadDetail();
        }
      },
      loadDetail() {
        this.R.get('/api/gateway/route/' + this.routeId, {}).then(res => {
          if (res.code === 0) {
            this.form = res.data;
          }
        })
      },
      save(callback) {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            if (!this.form.serviceId && !this.form.url) {
              this.$message.error('服务地址和服务ID至少填写其中一个');
              return;
            }
            let reqParam = this.U.copy(this.form);
            debugger
            if(reqParam.sensitiveHeaders){
              reqParam.sensitiveHeaders = reqParam.sensitiveHeaders.split(',');
            }else{
              reqParam.sensitiveHeaders = [];
            }
            if (!this.routeId) {
              this.R.postJson('/api/gateway/route', reqParam).then(res => {
                if (res.code === 0) {
                  callback(true)
                }
              })
            } else {
              this.R.patchJson('/api/gateway/route', reqParam).then(res => {
                if (res.code === 0) {
                  callback(true)
                }
              })
            }
          }
        })
      }
    }
  }
</script>

<style scoped>
  /deep/ .el-input.is-disabled input {
    color: #4c4c4c;
    background-color: unset;
    border: unset;
  }

  /deep/ .el-input.is-disabled .el-input__suffix {
    display: none;
  }

  /deep/ .el-checkbox.is-disabled .el-checkbox__label {
    color: #4c4c4c;
  }
</style>
