<template>
  <Card>
    <el-row :gutter="0">
      <el-col :span="4">
        <div class="box-card">

          <div class="grid-content bg-purple-light">

            <el-card
              class="box-card"
              shadow="never"
            >
              <div slot="header" class="clearfix">
                <svg-icon icon-class="title" /><span>权限树</span>
              </div>
              <el-scrollbar style="height: 75vh;">
                <permission-tree
                  ref="permission-tree"
                  @node-click="nodeClick"
                />
              </el-scrollbar>
            </el-card>

          </div>
        </div>
      </el-col>
      <el-col :span="7">
        <div class="box-card">
          <div class="grid-content bg-purple-light">
            <el-card
              class="box-card"
              shadow="never"
            >
              <div slot="header" class="clearfix">
                <svg-icon icon-class="title" /><span>权限详情</span>
              </div>
              <div style="height: 75vh;">
                <el-form
                  ref="form"
                  v-loading="loading"
                  :model="form"
                  label-width="80px"
                  size="mini"
                >
                  <el-form-item label="权限名称">
                    <el-input
                      v-model="form.purviewName"
                      :disabled="formDisabled"
                    />
                  </el-form-item>
                  <el-form-item label="权限ID">
                    <el-input
                      v-model="form.purviewId"
                      :disabled="true"
                    />
                  </el-form-item>
                  <el-form-item label="权限编码">
                    <el-input
                      v-model="form.purviewCode"
                      :disabled="formDisabled"
                    />
                  </el-form-item>
                  <el-form-item label="权限类型">
                    <el-radio-group
                      v-model="form.purviewType"
                      :disabled="formDisabled"
                    >
                      <el-radio v-if="form.purviewType==='1'" label="1">菜单权限</el-radio>
                      <el-radio v-if="form.purviewType==='3'" label="3">页面元素</el-radio>
                      <el-radio v-if="form.purviewType==='2'" label="2">接口权限</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item :label="form.purviewType==='2'?'接口URL':'菜单URL'" prop="url" :title="form.url">
                    <el-input
                      v-model="form.url"
                      :disabled="formDisabled"
                    />
                  </el-form-item>
                  <el-form-item v-if="form.purviewType==='1'" label="页面组件">
                    <el-input
                      v-model="form.component"
                      :disabled="formDisabled"
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
                  <el-form-item label="展示顺序">
                    <el-input
                      v-model="form.orderNum"
                      :disabled="formDisabled"
                    />
                  </el-form-item>
                  <el-form-item label="授予角色">
                    <el-button
                      size="mini"
                      :disabled="!form.purviewId"
                      @click="isShowRolePermissionDialog=true"
                    >查看授权
                    </el-button>
                  </el-form-item>
                 <!-- <el-form-item label="授予用户">
                    <el-button
                      size="mini"
                      :disabled="!form.purviewId"
                      @click="isShowUserPermissionDialog=true"
                    >查看授权
                    </el-button>
                  </el-form-item>-->
                  <el-form-item>
                    <el-button
                      type="success"
                      size="mini"
                      @click="addPermission"
                    >新增
                    </el-button>
                    <el-button
                      type="primary"
                      size="mini"
                      :disabled="!form.purviewId"
                      @click="updatePermission"
                    >编辑
                    </el-button>
                    <el-button
                      type="warning"
                      size="mini"
                      :disabled="!form.purviewId"
                      @click="deletePermission"
                    >删除
                    </el-button>
                  </el-form-item>
                </el-form>

              </div>
            </el-card>
          </div>
        </div>
      </el-col>
      <el-col :span="13">
        <div class="box-card">
          <div class="grid-content bg-purple-light">
            <el-card
              class="box-card"
              shadow="never"
            >
              <div slot="header" class="clearfix">
                <svg-icon icon-class="title" /><span>列表</span>
              </div>
              <div style="height: 74vh">
                <permission-table :parent-id="currPermissionId" />
              </div>
            </el-card>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-dialog
      v-if="isShowAddDialog"
      :title="'新增/修改权限'"
      :visible.sync="isShowAddDialog"
      :close-on-click-modal="false"
      width="35%"
    >
      <AddPermissionDialog
        ref="add-permission-dialog"
        :permission-id="currPermissionId"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="onAddCancel">取 消</el-button>
        <el-button type="primary" @click="onAddOk">保 存</el-button>
      </span>
    </el-dialog>

    <el-dialog
      v-if="isShowRolePermissionDialog"
      :title="'授权给角色'"
      :visible.sync="isShowRolePermissionDialog"
      :close-on-click-modal="false"
    >
      <role-permission
        ref="role-permission"
        :permission-id="currPermissionId"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="onRolePermissionCancel">取 消</el-button>
        <!--<el-button type="primary" @click="onRolePermissionOk">保 存</el-button>-->
      </span>
    </el-dialog>

    <el-dialog
      v-if="isShowUserPermissionDialog"
      :title="'授权给用户'"
      :visible.sync="isShowUserPermissionDialog"
    >
      <user-permission
        ref="user-permission"
        :permission-id="currPermissionId"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="onUserPermissionCancel">取 消</el-button>
        <!--<el-button type="primary" @click="onUserPermissionOk">保 存</el-button>-->
      </span>
    </el-dialog>
  </Card>
</template>

<script>
import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
import SearchBarItem
from '../../../../components/common/SearchBar/search-bar-item'
import PermissionTree from './permission-tree'
import AddPermissionDialog from './add-permission-dialog'
import RolePermission from './role-permission'
import UserPermission from './user-permission'
import PermissionTable from './permission-table'
import Card from "../../../../components/Card/Card";

export default {
  name: 'Index',
  components: {
    Card,
    PermissionTable,
    UserPermission,
    RolePermission,
    AddPermissionDialog, PermissionTree, SearchBarItem, SearchBar
  },
  data() {
    return {
      loading: false,
      title: '权限详情',
      form: {
        purviewType:'1'
      },
      currPermissionId: null,
      formDisabled: true,
      isShowAddDialog: false,
      isShowRolePermissionDialog: false,
      isShowUserPermissionDialog: false,
    }
  },
  methods: {
    nodeClick(data) {
      const that = this
      const purviewId = data.purviewId
      this.currPermissionId = purviewId
      this.loading = true
      this.R.get('/api/auth/permission/' + purviewId, {}).then(res => {
        that.form = res.data
        this.loading = false
      })
    },
    addPermission() {
      this.currPermissionId = null
      this.isShowAddDialog = true
    },
    updatePermission() {
      this.currPermissionId = this.form.purviewId
      this.isShowAddDialog = true
    },
    deletePermission() {
      this.$confirm('确定删除权限？', '提示').then(() => {
        this.R.delete('/api/auth/permission/' + this.currPermissionId).then(res => {
          if (res.code === 0) {
            this.$message.success('删除成功')
            this.form = {}
            this.currPermissionId = null
            this.$refs['permission-tree'].refresh()
          }
        })
      }).catch(() => {
        // this.$Message.error('取消');
      })
    },
    onAddOk() {
      // 调用子组件保存 保存成功关闭弹框
      this.$refs['add-permission-dialog'].submit((isOk) => {
        if (isOk) {
          this.isShowAddDialog = false
          //this.$refs['permission-tree'].refresh()
        }
      })
      // 刷新权限树 定位到新增的节点
    },
    onAddCancel() {
      this.isShowAddDialog = false
    },

    onRolePermissionOk() {
      this.isShowRolePermissionDialog = false
    },
    onRolePermissionCancel() {
      this.isShowRolePermissionDialog = false
    },
    onUserPermissionOk() {
      this.isShowUserPermissionDialog = false
    },
    onUserPermissionCancel() {
      this.isShowUserPermissionDialog = false
    },
    refresh() {
      if (this.currPermissionId) {
        this.loading = true
        this.R.get('/api/auth/permission/' + this.currPermissionId, {}).then(res => {
          this.form = res.data
          this.loading = false
        })
      }
    }
  }
}
</script>

<style scoped>
  /deep/ input[disabled], input:disabled, input.disabled {
    -webkit-text-fill-color: #5c5c5c;
    -webkit-opacity: 1;
    opacity: 1;
    border: none;
    font-size: 14px;

  }

  /deep/ .el-radio.is-disabled {
    -webkit-text-fill-color: #5c5c5c;
    -webkit-opacity: 1;
    opacity: 1;
    border: none;
  }

  /deep/ .el-input.is-disabled .el-input__inner {
    background-color: #ffffff;
  }

  /deep/ .el-form-item__label {
    font-weight: 500;
  }

</style>
