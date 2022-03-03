<!--用户授权弹窗-->
<template>
  <el-row>
    <el-col :span="6">
      <div class="border" style="padding-right: 0">
        <el-scrollbar style="height: 420px;">
          <PermissionTree @node-click="nodeClick"></PermissionTree>
        </el-scrollbar>
      </div>
    </el-col>
    <el-col :span="18">
      <div class="border" style="height: 460px;margin-left: 10px">
        <SearchBar>
          <SearchBarItem ref="isPermissions" :type="'radio'"
                         v-model="searchParam.isPermissions"
                         :label="'是否已授权：'" :default-value="'1'"
                         :data="initData.isPermissions"></SearchBarItem>
          <SearchBarItem :type="'input'"
                         v-model="searchParam.filterText"
                         :label="'权限名称：'"
                         :placeholder="'请输入权限名称模糊查询'"></SearchBarItem>
        </SearchBar>
        <el-row style="margin-bottom: 10px">
          <el-col :span="22">
            <el-button size="mini" @click="batchAddPurview"
                       :disabled="loading || selectedRows.length === 0">批量授权
            </el-button>
            <el-button size="mini" @click="batchDelPurview"
                       :disabled="loading || selectedRows.length === 0">批量取消授权
            </el-button>
          </el-col>
          <el-col :span="2">
            <el-button size="mini" @click="refresh">
              <svg-icon icon-class="refresh"></svg-icon>
            </el-button>

          </el-col>
        </el-row>
        <el-table :data="userPermissions" size="" max-height="250"
                  @selection-change="handleSelectionChange"
                  @select="handleSelect"
                  @select-all="handleSelectAll"
                  v-loading="loading"
                  :cell-style="{padding:'1px'}">
          <el-table-column
            type="selection"
            align="center"
            width="55"
            :selectable="isSelectable">
          </el-table-column>
          <el-table-column prop="purviewId" label="ID"
                           width="60"></el-table-column>
          <el-table-column prop="purviewName" width="150" label="权限名称"></el-table-column>
          <el-table-column prop="purviewCode" label="权限编码"
                           width="150"></el-table-column>
          <el-table-column prop="purviewType" label="权限类型"
                           width="80">
            <template slot-scope="{row}">
              <span v-if="row.purviewType==='1'">
                菜单权限
              </span>
              <span v-if="row.purviewType==='2'">
                接口权限
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="purviewObject" label="授权对象"
                           width="80">
            <template slot-scope="{row}">
              <span v-if="row.purviewObject==='role'">
                角色
              </span>
              <span v-if="row.purviewObject==='user'">
                用户
              </span>
              <span v-if="!row.purviewObject">
                -
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="effDate" width="180" label="授权时间">
            <template slot-scope="{row}">
            <span v-if="row.isPermissions==='1'">{{row.effDate}}
            </span>
              <span v-if="row.isPermissions==='0'">-</span>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center"
            width="120"
            fixed="right">
            <template slot-scope="{row}">
              <el-button :disabled="row.purviewObject==='role'" type="text" v-if="row.isPermissions==='1'"
                         @click="delPurview(row)">解除授权
              </el-button>
              <el-button :disabled="row.purviewObject==='role'" type="text" v-if="row.isPermissions==='0'"
                         @click="addPurview(row)">授权
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-row style="margin-top:10px;">
          <el-col :span="9" class="secondary" >
            <span style="font-size: 12px ">角色授权修改请到角色管理处修改</span>
          </el-col>
          <el-col :span="15">
            <el-pagination
              background
              small
              size="mini"
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

</template>

<script>
  import PermissionTree from "../permission-manage/permission-tree";
  import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
  import SearchBarItem
    from "../../../../components/common/SearchBar/search-bar-item";

  export default {
    name: "user-permission-grant-dialog",
    components: {PermissionTree, SearchBarItem, SearchBar},
    props: {
      currUserId: Number
    },
    data() {
      return {
        initData: {
          isPermissions: [
            {itemText: '已授权(含角色授权)', itemCode: '1'},
            {itemText: '未授权', itemCode: '0'},
          ]
        },
        searchParam: {
          pageSize: 5,
          isPermissions: '1',
          purviewId: null,
          path:null
        },
        userPermissions: [],
        pageInfo: {},
        loading: false,
        selectedRows: []
      }
    },
    mounted() {
      this.init();
    },
    watch: {
      searchParam: {
        handler(newVal, oldVal) {
          this.search();
        },
        deep: true,
      },
      "searchParam.isPermissions": {
        handler(newVal, oldVal) {
          this.cleanSelectRows();
        },
        deep: true
      }
    },
    methods: {
      init() {
        //解决初始化无法默认选中的问题
        this.$refs['isPermissions'].value = '1';
        this.search();
      },
      search() {
        this.qryUserPermissions();
      },
      qryUserPermissions() {
        let reqParam = {
          userId:this.currUserId,
          path: this.searchParam.path,
          isPermissions: this.searchParam.isPermissions,
          filterText:this.searchParam.filterText
        };
        this.R.get('/api/auth/userManage/permissions', reqParam).then(res => {
          this.pageInfo.total = res.data.total;
          this.userPermissions = res.data.list;
        })
      },
      currentChange(currPage) {
        this.searchParam.pageNum = currPage;
        this.search();
      },/**
       * 表格行选中
       * @param selection
       * @param row
       */
      handleSelect(selection, row) {
        this.selectedRows = this.U.copy(selection);
      },
      handleSelectionChange(selection) {
        //alert(JSON.stringify(selection))
      },
      /**
       * 表格全选
       */
      handleSelectAll(selection) {
        this.cleanSelectRows();
        if (selection.length === 0) {
          this.selectedRows = [];
        } else {
          this.selectedRows = this.U.copy(selection);
        }
      },
      /**
       * 是可选中
       */
      isSelectable(row){
        if(row.purviewObject==='user' || this.searchParam.isPermissions==='0'){
          return true;
        }
      },
      cleanSelectRows() {
        this.selectedRows = [];
      },
      refresh() {
        this.clean();
        this.cleanSelectRows();
        this.search();
      },
      /**
       * 新增授权 option=1
       * @param row
       */
      addPurview(row) {
        let reqParam = {
          userIds: [this.currUserId],
          permissions: [row.purviewId],
          option: '1'
        };
        this.R.postJson('/api/auth/userManage/permissions', reqParam).then(res => {
          if (res.code === 0) {
            this.$message({
              type: 'success',
              message: res.msg
            });
            this.init();
            this.cleanSelectRows();
          }
        })
      },
      /**
       * 解除授权 option=2
       * @param row
       */
      delPurview(row) {
        let reqParam = {
          userIds: [this.currUserId],
          permissions: [row.purviewId],
          option: '2'
        };
        this.R.postJson('/api/auth/userManage/permissions', reqParam).then(res => {
          if (res.code === 0) {
            this.$message({
              type: 'success',
              message: res.msg
            });
            this.init();
            this.cleanSelectRows();
          }
        })
      },
      /**
       * 批量授权
       */
      batchAddPurview() {
        let selectPermissionsIds = this.selectedRows.map(item => item.purviewId);
        let reqParam = {
          userIds: [this.currUserId],
          permissions: selectPermissionsIds,
          option: '1'
        };
        this.R.postJson('/api/auth/userManage/permissions', reqParam).then(res => {
          if (res.code === 0) {
            this.$message({
              type: 'success',
              message: res.msg
            });
            this.init();
            this.cleanSelectRows();
          }
        })
      },
      /**
       * 批量解除授权
       */
      batchDelPurview() {
        let selectPermissionsIds = this.selectedRows.map(item => item.purviewId);
        let reqParam = {
          userIds: [this.currUserId],
          permissions: selectPermissionsIds,
          option: '2'
        };
        this.R.postJson('/api/auth/userManage/permissions', reqParam).then(res => {
          if (res.code === 0) {
            this.$message({
              type: 'success',
              message: res.msg
            });
            this.init();
            this.cleanSelectRows();
          }
        })
      },
      /**
       * 树节点点击事件
       * @param data
       */
      nodeClick(data){
        this.R.get('/api/auth/permission/' + data.purviewId, {}).then(res => {
          this.searchParam.purviewId = data.purviewId;
          this.searchParam.path = res.data.path
        });

      },
      clean(){
        this.searchParam.purviewId = null;
      }
    }
  }
</script>

<style scoped>

</style>
