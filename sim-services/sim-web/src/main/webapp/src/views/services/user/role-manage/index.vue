<template>
  <Card>
    <el-row :gutter="10">
      <el-col :span="24">
        <div class="box-card">

          <div class="grid-content bg-purple-light">
            <Search v-model.sync="searchParam"
                    :options="searchOptions"
                    size="small"
                    show-add-button
                    :inline="true"
                    @search="search"
                    @add="addUser"
            ></Search>
            <el-table-plus
              ref="tenant-table"
              :option-column="optionColumn"
              :load-data="loadData"
              :columns="columns"

              is-compact
              :search-data="searchParam"
              :auto-page-size="true"
            >
            </el-table-plus>
          </div>
          <el-row style="margin-top:10px;">
            <el-col>
              <el-pagination
                background
                layout="total, prev, pager, next"
                :total="pageInfo.total"
                :page-size="searchParam.pageSize"
                @current-change="currentChange"
                style="text-align: right">
              </el-pagination>
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>

    <el-dialog :title="'添加/修改角色'" :visible.sync="isShowAddRoleDialog" width="30%"
               v-if="isShowAddRoleDialog" :close-on-click-modal="false">
      <add-role-dialog ref="add-role-dialog" :data="currRow"></add-role-dialog>
      <span slot="footer" class="dialog-footer">
        <el-button @click="onAddRoleCancel">取 消</el-button>
        <el-button type="primary" @click="onAddRoleOk">保 存</el-button>
      </span>
    </el-dialog>

    <el-dialog
      v-if="isShowRolePermissionDialog"
      :title="'授权给角色'"
      :visible.sync="isShowRolePermissionDialog"
      :close-on-click-modal="false"
      width="70%"
    >
      <role-permission
        ref="role-permission"
        :curr-role-id="currRow.roleId"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="onRolePermissionCancel">取 消</el-button>
        <!--<el-button type="primary" @click="onRolePermissionOk">保 存</el-button>-->
      </span>
    </el-dialog>
  </Card>
</template>

<script>
  import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
  import SearchBarItem
    from "../../../../components/common/SearchBar/search-bar-item";
  import AddRoleDialog from "./add-role-dialog";
  import RolePermission from './role-permission'
  import Card from "../../../../components/Card/Card";
  import ElTablePlus from '../../../../components/Table/el-table-plus'
  import Search from "../../../../components/Search/search";

  export default {
    name: "index",
    components: {ElTablePlus,Search,Card, AddRoleDialog, SearchBarItem, SearchBar,RolePermission},
    data() {
      return {
        searchOptions:[
          {
            type: 'select',
            prop: 'statusCd',
            label: '角色状态',
            code:'USER_STATUS',
            items:[]
          }
        ],
        pageInfo: {},
        columns: [
          { prop: 'roleId', label: 'ID', width: '70', align: 'center' },
          { prop: 'roleName', label: '角色名称',  align: 'center' },
          { prop: 'roleCode', label: '角色编码', width: '120', align: 'center' },
          { prop: 'userCount', label: '用户数量', width: '100', align: 'center' },
          { prop: 'createDate', label: '创建时间', width: '180', align: 'center' },
          { prop: 'createDate', label: '更新时间', width: '180', align: 'center',isSlot:true },
          { prop: 'statusName', label: '状态', width: '80', align: 'center' }
        ],
        optionColumn: {
          options: [
            {
              type: 'text',
              text: '编辑',
              handler: (row) => {
                this.editRole(row)
              }
            }, {
              type: 'text',
              text: '授权',
              handler: (row) => {
                this.addRolePurview(row)
              }
            }, {
              type: 'text',
              text: '删除',
              handler: (row) => {
                this.deleteRole(row)
              },
              disabled:(row)=>{
                return row.notUpdate;
              }
            }
          ],
          fixed: 'right',
          width: '150'
        },
        initData: {
          roleList: [],
          status: []
        },
        searchParam: {
          filterText: '',
          pageSize: 10
        },
        loading: false,
        isShowAddRoleDialog: false,
        currRow: null,

        isShowRolePermissionDialog:false
      };
    },
    mounted() {
      this.init();
    },
    watch: {
      searchParam: {
        handler(newVal, oldVal) {
          this.search();
        },
        deep: true
      }
    },
    methods: {
      init() {
      },
      loadData(resolve, pageInfo, searchData) {
        this.R.get('/api/auth/role', { ...pageInfo, ...searchData }).then(res => {
          resolve(res.data)
        })
      },
      search(){
        this.$refs['role-table'].refresh();
      },
      showDetailModal(row) {

      },
      addRole() {
        this.currRow = null;
        this.isShowAddRoleDialog = true;
      },
      onAddRoleCancel() {
        this.isShowAddRoleDialog = false;
      },
      onAddRoleOk() {
        this.$refs['add-role-dialog'].save(success => {
          if (success) {
            this.isShowAddRoleDialog = false;

            this.search();
          }
        })
      },
      currentChange(currPage) {
        this.searchParam.pageNum = currPage;
        this.search();
      },
      editRole(row) {
        this.currRow = row;
        this.isShowAddRoleDialog = true;
      },
      deleteRole(row) {
        this.$confirm('删除后可能会导致拥该角色的用户失去权限，确认删除角色?', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(()=>{
          this.R.forUserApi.delete('/role/' + row.roleId).then(res => {
            this.$message.success('删除成功');
            this.search();
          })
        })
      },
      addRolePurview(row){
        this.currRow = row;
        this.isShowRolePermissionDialog = true;
      },
      onRolePermissionOk() {
        this.isShowRolePermissionDialog = false
      },
      onRolePermissionCancel() {
        this.isShowRolePermissionDialog = false
      },
    }

  }
</script>

<style scoped>

</style>
