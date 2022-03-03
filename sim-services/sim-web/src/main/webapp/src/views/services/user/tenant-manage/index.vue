<!--用户列表-->
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
              <template slot="expDate"  slot-scope="{row}">
                {{row.expDate?row.expDate:'长期有效'}}
              </template>
              <template slot="viewPermission" slot-scope="{row}">
                <el-button @click="userPurview(row)" type="text" size="">
                  服务授权
                </el-button>
              </template>
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

    <el-dialog :title="'给租户【'+currUserName+'】授权'"
               :visible.sync="isShowUserPermissionDialog"
               width="70%"
               v-if="isShowUserPermissionDialog" :close-on-click-modal="false">
      <service-permission-grant-dialog ref="user-permission"
                                       :curr-user-id="currUserId"></service-permission-grant-dialog>
      <span slot="footer" class="dialog-footer">
        <el-button @click="onUserPermissionCancel">取 消</el-button>
        <!--<el-button type="primary" @click="onRolePermissionOk">保 存</el-button>-->
      </span>
    </el-dialog>
  </Card>
</template>

<script>

  import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
  import SearchBarItem
    from "../../../../components/common/SearchBar/search-bar-item";
  import ServicePermissionGrantDialog from "./service-permission-grant-dialog";
  import Card from "../../../../components/Card/Card";
  import ElTablePlus from '../../../../components/Table/el-table-plus'
  import Search from "../../../../components/Search/search";

  export default {
    name: "index",
    components: {
      Search,
      Card,
      ServicePermissionGrantDialog, SearchBarItem, SearchBar,ElTablePlus
    },
    data() {
      return {
        searchParam: {
          filterText: '',
          pageSize: 10
        },
        searchOptions:[
          {
            type: 'select',
            prop: 'statusCd',
            label: '租户状态',
            code:'USER_STATUS',
            items:[]
          },{
            type: 'input',
            prop: 'filterText',
            label: '模糊查询',
            placeholder:'请输入openid/租户名'
          },
        ],
        pageInfo: {},
        columns: [
          { prop: 'userId', label: 'ID', width: '70', align: 'center' },
          { prop: 'userName', label: '租户名', align: 'center' },
          { prop: 'openId', label: 'OPENID', width: '100', align: 'center' },
          { prop: 'secretKey', label: '密钥', width: '100', align: 'center' },
          { prop: 'roles', label: '归属角色', width: '150', align: 'center' },
          { prop: 'viewPermission', label: '查看权限', width: '100', align: 'center',isSlot:true },
          { prop: 'createDate', label: '创建时间', width: '160', align: 'center' },
          { prop: 'expDate', label: '过期时间', width: '160', align: 'center',isSlot:true },
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
                return row.notUpdate || row.status!=='1000';
              }
            }, {
              type: 'text',
              text: '冻结',
              handler: (row) => {
                this.updateStatus(row,'1800');
              },
              disabled:(row)=>{
                return row.notUpdate || row.status==='1000';
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
                return row.status==='1000' && row.agentLevel!==0 || row.notUpdate;
              }
            }
          ],
          fixed: 'right',
          width: '230'
        },
        currUserId: null,
        currUserName: null,

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
        let param = this.U.copy(searchData);
        param.userType = '3000';
        this.R.get('/api/auth/userManage', { ...pageInfo, ...param }).then(res => {
          resolve(res.data)
        })
      },
      search(){
        this.$refs['tenant-table'].refresh();
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
        this.currUserName = row.userName;
        this.isShowUserPermissionDialog = true;
      },
      onUserPermissionCancel() {
        this.isShowUserPermissionDialog = false;
      },
      edit(row) {
        this.$router.push({path: '/user-add?userId=' + row.userId})
      }
    }
  }
</script>

<style scoped>

</style>
