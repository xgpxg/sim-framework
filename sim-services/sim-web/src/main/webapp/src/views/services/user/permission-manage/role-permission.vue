<!--角色授权-->
<template>
  <div>
    <div class="border">已将权限 <span style="color: #1890ff;">{{currPermission.purviewName}}</span>
      授权给 {{total}} 个角色
    </div>
    <div class="border">
      <SearchBar>
        <SearchBarItem :type="'radio'"
                       v-model="searchParam.isPermissions"
                       :label="'是否已授权：'"
                       :data="initData.isPermissions"></SearchBarItem>
        <SearchBarItem :type="'input'"
                       v-model="searchParam.filterText"
                       :label="'查询角色：'"
                       :placeholder="'请输入角色名称模糊查询'"></SearchBarItem>
      </SearchBar>
      <el-row style="margin-bottom: 10px">
        <el-col :span="22">
          <el-button size="mini" @click="batchAddPurview" :disabled="loading || selectedRows.length === 0">批量授权</el-button>
          <el-button size="mini" @click="batchDelPurview" :disabled="loading || selectedRows.length === 0">批量取消授权</el-button>
        </el-col>
        <el-col :span="2">
          <el-button size="mini" @click="refresh">
            <svg-icon icon-class="refresh"></svg-icon>
          </el-button>

        </el-col>
      </el-row>
      <el-table :data="permissionRoles"
                @selection-change="handleSelectionChange"
                @select="handleSelect"
                @select-all="handleSelectAll"
                v-loading="loading"
                :cell-style="{padding:'1px'}">
        <el-table-column
          type="selection"
          align="center"
          width="55">
        </el-table-column>
        <el-table-column prop="roleId" label="ID" width="80"></el-table-column>
        <el-table-column prop="roleName" label="角色名称"></el-table-column>
        <el-table-column prop="roleCode" label="角色编码"
                         width="120"></el-table-column>
        <el-table-column prop="purviewTime" label="授权时间">
          <template slot-scope="{row}">
            <span v-if="row.isPermissions==='1'">{{row.purviewTime}}
            </span>
            <span v-if="row.isPermissions==='0'">-</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          align="center"
          width="120">
          <template slot-scope="{row}">
            <el-button type="text" v-if="row.isPermissions==='1'"
                       @click="delPurview(row)">解除授权
            </el-button>
            <el-button type="text" v-if="row.isPermissions==='0'"
                       @click="addPurview(row)">授权
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-row style="margin-top:10px;">
        <el-col>
          <el-pagination
            background
            small
            layout="total, prev, pager, next"
            :total="pageInfo.total"
            :page-size="searchParam.pageSize"
            @current-change="currentChange"
            style="text-align: right">
          </el-pagination>
        </el-col>
      </el-row>
    </div>
  </div>

</template>

<script>
  import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
  import SearchBarItem
    from "../../../../components/common/SearchBar/search-bar-item";

  export default {
    name: "role-permission",
    components: {SearchBarItem, SearchBar},
    props: {
      permissionId: Number
    },
    data() {
      return {
        loading: false,
        initData: {
          isPermissions: [
            {itemText: '全部', itemCode: null},
            {itemText: '已授权', itemCode: 1},
            {itemText: '未授权', itemCode: 0},
          ]
        },
        currPermission: {},
        pageInfo: {},
        searchParam: {
          isPermissions:null,
          filterText: '',
          pageSize: 5
        },
        /**
         * 已授权的角色数量
         */
        total: 0,
        permissionRoles: [],

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
        deep: true
      },
      "searchParam.isPermissions":{
        handler(newVal, oldVal) {
          this.cleanSelectRows();
        },
        deep: true
      }
    },
    methods: {
      init() {
        let that = this;
        let purviewId = this.permissionId;
        this.R.get('/api/auth/permission/' + purviewId, {}).then(res => {
          that.currPermission = res.data;
          that.search();
        })
      },
      search() {
        this.qryTotal();
        this.qryPermissionRoles();
      },
      qryTotal() {
        let reqParam = {};
        reqParam.purviewId = this.permissionId;
        reqParam.isPermissions = '1';
        this.R.get('/api/auth/permission/roles', reqParam).then(res => {
          this.total = res.data.total;
        })
      },
      qryPermissionRoles() {
        let reqParam = this.searchParam;
        reqParam.purviewId = this.permissionId;
        this.loading = true;
        this.R.get('/api/auth/permission/roles', reqParam).then(res => {
          this.permissionRoles = res.data.list;
          this.pageInfo.total = res.data.total;
          this.loading = false;
        })
      },
      currentChange(currPage) {
        this.searchParam.pageNum = currPage;
        this.search();
      },
      /**
       * 新增授权 option=1
       * @param row
       */
      addPurview(row) {
        let reqParam = {
          roles: [row.roleId],
          permissions: [this.permissionId],
          option: '1'
        };
        this.R.postJson('/api/auth/permission/purview', reqParam).then(res => {
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
          roles: [row.roleId],
          permissions: [this.permissionId],
          option: '2'
        };
        this.R.postJson('/api/auth/permission/purview', reqParam).then(res => {
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
        let selectRoleIds = this.selectedRows.map(item => item.roleId);
        let reqParam = {
          roles: selectRoleIds,
          permissions: [this.permissionId],
          option: '1'
        };
        this.R.postJson('/api/auth/permission/purview', reqParam).then(res => {
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
        let selectRoleIds = this.selectedRows.map(item => item.roleId);
        let reqParam = {
          roles: selectRoleIds,
          permissions: [this.permissionId],
          option: '2'
        };
        this.R.postJson('/api/auth/permission/purview', reqParam).then(res => {
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
      cleanSelectRows() {
        this.selectedRows = [];
      },
      refresh() {
        this.cleanSelectRows();
        this.search();
      }
    }
  }
</script>

<style scoped>

</style>
