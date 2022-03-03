<!--新增权限、权限授予角色-->
<template>
  <div>
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-width="80px"
      size="mini"
    >
      <el-form-item label="权限名称" prop="purviewName">
        <el-input
          v-model="form.purviewName"
          :disabled="formDisabled"
        />
      </el-form-item>
      <el-form-item label="权限编码" prop="purviewCode">
        <el-input
          v-model="form.purviewCode"
          :disabled="formDisabled"
        >
          <templete v-if="!this.permissionId" slot="prepend">
            {{ form.purviewCodePrefix }}
          </templete>
        </el-input>
      </el-form-item>
      <el-form-item label="权限目录" prop="parentId">
        <permission-select-tree
          :value="form.parentId"
          @on-select="onCatalogSelect"
        />
      </el-form-item>

      <el-form-item label="权限类型" prop="purviewType">
        <el-radio-group
          v-model="form.purviewType"
          :disabled="formDisabled"
        >
          <el-radio label="1">菜单权限</el-radio>
          <el-radio label="2">接口权限</el-radio>
          <el-radio label="3">页面元素</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="form.purviewType==='2'?'接口URL':'菜单URL'" prop="url">
        <el-input
          v-model="form.url"
          :disabled="formDisabled"
          placeholder="以斜线/开头表示内部菜单或接口，否则为跳转菜单，支持正则"
        />
      </el-form-item>
      <el-form-item v-if="form.purviewType==='1'" label="页面元素">
        <el-input
          v-model="form.component"
          :disabled="formDisabled"
          placeholder="须以斜线/开头"
        />
      </el-form-item>
      <el-form-item v-if="form.purviewType==='1'" label="图标">
        <el-input
          v-model="form.icon"
          :disabled="formDisabled"
        >
          <span slot="prefix"><svg-icon
            :icon-class="form.icon"
            class="icon"
          /></span>
        </el-input>

      </el-form-item>
      <el-form-item label="展示排序">
        <el-input
          v-model="form.orderNum"
          :disabled="formDisabled"
        />
      </el-form-item>
      <el-form-item v-if="form.purviewType==='1'" label="是否隐藏">
        <el-radio-group v-model="form.hidden">
          <el-radio :label="false">否</el-radio>
          <el-radio :label="true">是</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

import PermissionSelectTree from './permission-select-tree'
export default {
  name: 'AddPermissionDialog',
  components: { PermissionSelectTree },
  props: {
    permissionId: {
      type: Number,
      default: {}
    }
  },
  data() {
    return {
      form: {
        purviewName: null,
        purviewCode: null,
        purviewType: null,
        parentId: null,

        purviewCodePrefix: '上级编码'
      },
      rules: {
        purviewName: { required: true, message: '请填写权限名称' },
        purviewCode: { required: true, message: '请填写权限编码' },
        parentId: { required: true, message: '请填选择上级目录' },
        purviewType: { required: true, message: '请选择权限类型' }
      }
    }
  },
  mounted() {
    if (this.permissionId) {
      this.init()
    }
  },
  methods: {
    init() {
      const that = this
      const permissionId = this.permissionId
      this.R.get('/api/auth/permission/' + permissionId, {}).then(res => {
        that.form = res.data
      })
    },
    submit(callback) {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          const reqParam = this.U.copy(this.form)
          if (!this.permissionId) {
            reqParam.purviewCode = this.form.purviewCodePrefix + this.form.purviewCode
          }
          this.R.postJson('/api/auth/permission', reqParam).then(res => {
            if (res.code === 0) {
              this.$message({
                type: 'success',
                message: '保存成功'
              })
              callback(true)
            }
          })
        }
      })
    },
    onCatalogSelect(value) {
      this.form.parentId = value
      this.getPurviewCodePrefix()
    },
    getPurviewCodePrefix() {
      const permissionId = this.form.parentId
      this.R.get('/api/auth/permission/' + permissionId, {}).then(res => {
        this.form.purviewCodePrefix = res.data.purviewCode + '_'
      })
    }
  }
}
</script>

<style scoped>

</style>
