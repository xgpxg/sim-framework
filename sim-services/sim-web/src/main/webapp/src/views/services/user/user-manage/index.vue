<!--用户列表-->
<template>
  <Card v-permission="123">
    <el-row :gutter="10">
      <el-col :span="24">
        <div class="box-card">

          <div class="grid-content bg-purple-light">
            <div class="" style="margin-bottom: 10px">
              <Search v-model.sync="searchParam"
                      :options="searchOptions"
                      size="small"
                      show-add-button
                      :inline="true"
                      @search="search"
                      @add="addUser"
              ></Search>
            </div>
            <div>
              <el-table-plus
                ref="permission-table"
                :option-column="optionColumn"
                :load-data="loadData"
                :columns="columns"
                is-compact
                :search-data="searchParam"
                :auto-page-size="true"
              >
                <template slot="expDate"  slot-scope="{row}">
                  {{row.expDate?row.expDate:'长期有效'}}
                </template>
                <template slot="viewPermission" slot-scope="{row}">
                  <el-button @click="userPurview(row)" type="text" size="">
                    用户授权
                  </el-button>
                </template>
                <template slot="roles" slot-scope="{row}">
                  <el-button @click="userRoleGrant(row)" type="text"
                             size="">设置角色
                  </el-button>
                </template>
              </el-table-plus>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog :title="'用户授权'" :visible.sync="isShowUserPermissionDialog"
               width="70%"
               v-if="isShowUserPermissionDialog" :close-on-click-modal="false">
      <user-permission-grant-dialog ref="user-permission"
                                    :curr-user-id="currUserId"></user-permission-grant-dialog>
      <span slot="footer" class="dialog-footer">
        <el-button @click="onUserPermissionCancel">取 消</el-button>
        <!--<el-button type="primary" @click="onRolePermissionOk">保 存</el-button>-->
      </span>
    </el-dialog>
    <el-dialog :title="'修改用户角色'" :visible.sync="isShowUserRoleDialog" width="50%"
               v-if="isShowUserRoleDialog" :close-on-click-modal="false">
      <user-role-grant-dialog ref="user-role"
                              :curr-user-id="currUserId"></user-role-grant-dialog>
      <span slot="footer" class="dialog-footer">
        <el-button @click="onUserRoleCancel">取 消</el-button>
        <!--<el-button type="primary" @click="onRolePermissionOk">保 存</el-button>-->
      </span>
    </el-dialog>
  </Card>
</template>

<script>

  import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
  import SearchBarItem
    from "../../../../components/common/SearchBar/search-bar-item";
  import UserPermissionGrantDialog from "./user-permission-grant-dialog";
  import UserRoleGrantDialog from "./user-role-grant-dialog";
  import Card from "../../../../components/Card/Card";
  import Search from "../../../../components/Search/search";
  import ElTablePlus from '../../../../components/Table/el-table-plus'

  export default {
    name: "index",
    components: {
      ElTablePlus,
      Search,
      Card,
      UserRoleGrantDialog,
      UserPermissionGrantDialog, SearchBarItem, SearchBar
    },
    data() {
      return {
        searchOptions:[
          {
            type: 'radio-button',
            prop: 'userType',
            label: '用户类型',
            code:'USER_TYPE',
            items:[]
          },{
            type: 'select',
            prop: 'statusCd',
            label: '用户状态',
            code:'USER_STATUS',
            items:[]
          },{
            type: 'input',
            prop: 'filterText',
            label: '模糊查询',
            placeholder:'请输入用户ID/用户名模糊查询'
          },
        ],
        pageInfo: {},
        initData: {
          userTypes: [],
          userStatus: [],
          userList: []
        },
        searchParam: {
          filterText: '',
          pageSize: 10
        },
        columns: [
          { prop: 'userId', label: 'ID', width: '70', align: 'center' },
          { prop: 'userName', label: '名称',  align: 'center' },
          { prop: 'userTypeName', label: '类型', width: '100', align: 'center' },
          { prop: 'roles', label: '归属角色', width: '100', align: 'center' },
          { prop: 'viewPermission', label: '查看权限', width: '100', align: 'center' },
          { prop: 'createDate', label: '创建时间', width: '180', align: 'center' },
          { prop: 'expDate', label: '过期时间', width: '100', align: 'center',isSlot:true },
          { prop: 'statusName', label: '状态', width: '80', align: 'center' }
        ],
        optionColumn: {
          options: [
            {
              type: 'text',
              text: '编辑',
              handler: (row) => {
                this.edit(row)
              },
              disabled:(row)=>{
                return row.notUpdate && ['1800','1200'].indexOf(row.status)>0;
              }
            }, {
              type: 'text',
              text: '解冻',
              handler: (row) => {
                this.updateStatus(row,'1800');
              },
              disabled:(row)=>{
                return row.notUpdate || row.status!=='1800';
              }
            }, {
              type: 'text',
              text: '冻结',
              handler: (row) => {
                this.updateStatus(row,'1800');
              },
              disabled:(row)=>{
                return row.notUpdate || row.status!=='1000';
              }
            }, {
              type: 'text',
              text: '停用',
              handler: (row) => {
                this.edit(row);
              },
              disabled:(row)=>{
                return row.notUpdate || row.status==='1300';
              }
            }, {
              type: 'text',
              text: '删除',
              handler: (row) => {
                this.updateStatus(row,'1100')
              },
              disabled:(row)=>{
                return !(row.status==='1000' && row.agentLevel!==0) || row.notUpdate;
              }
            }
          ],
          fixed: 'right',
          width: '230'
        },

        loading: false,
        currUserId: null,

        isShowUserPermissionDialog: false,
        isShowUserRoleDialog: false,
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
        this.R.get('/api/auth/userManage', {...searchData,...pageInfo, }).then(res => {
          resolve(res.data)
        })
      },
      search(){
        this.$refs['permission-table'].refresh();
      },
      showDetailModal(row) {

      },
      addUser() {
        this.$router.push({path: '/user-add'})
      },
      currentChange(currPage) {
        this.searchParam.pageNum = currPage;
        this.search();
      },

      userPurview(row) {
        this.currUserId = row.userId;
        this.isShowUserPermissionDialog = true;
      },
      onUserPermissionCancel() {
        this.isShowUserPermissionDialog = false;
      },
      userRoleGrant(row) {
        this.currUserId = row.userId;
        this.isShowUserRoleDialog = true;
      },
      onUserRoleCancel() {
        this.isShowUserRoleDialog = false;
      },
      edit(row) {
        this.$router.push({path: '/user-add?userId='+row.userId})
      }
    }
  }
</script>

<style scoped>

</style>
