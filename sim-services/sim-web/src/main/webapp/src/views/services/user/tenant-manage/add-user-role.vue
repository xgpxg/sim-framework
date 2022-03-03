<template>
    <div class="app-container">
      <el-row>
        <el-col :span="24">
          <div class="" style="height: 460px;margin-left: 10px">
            <SearchBar>
              <SearchBarItem :type="'input'"
                             v-model="searchParam.filterText"
                             :label="'权限名称：'"
                             :placeholder="'请输入权限名称模糊查询'"></SearchBarItem>
            </SearchBar>
            <el-row style="margin-bottom: 10px">
              <el-col :span="22">
                <el-button size="mini" @click="batchAddUserRole"
                           :disabled="loading || selectedRows.length === 0">批量授权
                </el-button>
                <el-button size="mini" @click="batchDelUserRole"
                           :disabled="loading || selectedRows.length === 0">批量取消授权
                </el-button>
              </el-col>
              <el-col :span="2">
                <el-button size="mini" @click="refresh">
                  <svg-icon icon-class="refresh"></svg-icon>
                </el-button>

              </el-col>
            </el-row>
            <el-table :data="userRoles" size="" max-height="250"
                      @selection-change="handleSelectionChange"
                      @select="handleSelect"
                      @select-all="handleSelectAll"
                      v-loading="loading"
                      :cell-style="{}">
              <el-table-column
                type="selection"
                align="center"
                width="55"
                :selectable="isSelectable">
              </el-table-column>
              <el-table-column prop="roleId" label="ID"
                               width="60"></el-table-column>
              <el-table-column prop="roleName"
                               label="角色名称"></el-table-column>
              <el-table-column prop="roleCode" label="角色编码"
                               width="150"></el-table-column>
              <el-table-column prop="effDate" width="180" label="授权时间">
                <template slot-scope="{row}">
            <span v-if="row.effDate">{{row.effDate}}
            </span>
                  <span v-if="!row.effDate">-</span>
                </template>
              </el-table-column>
              <el-table-column
                label="操作"
                align="center"
                width="120"
                fixed="right">
                <template slot-scope="{row}">
                  <el-button :disabled="row.notUpdate===1" type="text"
                             v-if="row.effDate"
                             @click="delUserRole(row)">解除授权
                  </el-button>
                  <el-button type="text"
                             v-if="!row.effDate"
                             @click="addUserRole(row)">授权
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-row style="margin-top:10px;">
              <el-col :span="24">
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
    </div>
</template>

<script>
  import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
  import SearchBarItem
    from "../../../../components/common/SearchBar/search-bar-item";

  export default {
    name: "add-user-role",
    components: {SearchBarItem, SearchBar},
    props: {
      currUserId: {
        type:Number,
        default:-1
      }
    },
    data() {
      return {
        initData: {},
        searchParam: {
          filterText: '',
          pageSize: 10,
          isPermissions: '1',
          purviewId: null
        },
        userRoles: [],
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
        this.search();
      },
      search() {
        this.qryUserRoles();
      },
      qryUserRoles() {
        let reqParam = this.U.copy(this.searchParam);
        reqParam.userId = this.currUserId;
        this.R.get('/api/auth/userManage/roles', reqParam).then(res => {
          this.pageInfo.total = res.data.total;
          this.userRoles = res.data.list;
        })
      },
      currentChange(currPage) {
        this.searchParam.pageNum = currPage;
        this.search();
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
      /**
       * 是可选中
       */
      isSelectable(row) {
        return row.notUpdate !== 1;
      },
      cleanSelectRows() {
        this.selectedRows = [];
      },
      refresh() {
        this.cleanSelectRows();
        this.clean();
        this.search();
      },
      clean() {
        this.searchParam.filterText = '';
      },
      /**
       * 新增授权 option=1
       * @param row
       */
      addUserRole(row) {
        let reqParam = {
          userIds: [this.currUserId],
          roleIds: [row.roleId],
          option: '1'
        };
        this.R.postJson('/api/auth/userManage/roles', reqParam).then(res => {
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
      delUserRole(row) {
        let reqParam = {
          userIds: [this.currUserId],
          roleIds: [row.roleId],
          option: '2'
        };
        this.R.postJson('/api/auth/userManage/roles', reqParam).then(res => {
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
      batchAddUserRole() {
        let selectRoleIds = this.selectedRows.map(item => item.roleId);
        let reqParam = {
          userIds: [this.currUserId],
          roleIds: selectRoleIds,
          option: '1'
        };
        this.R.postJson('/api/auth/userManage/roles', reqParam).then(res => {
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
      batchDelUserRole() {
        let selectRoleIds = this.selectedRows.map(item => item.roleId);
        let reqParam = {
          userIds: [this.currUserId],
          roleIds: selectRoleIds,
          option: '2'
        };
        this.R.postJson('/api/auth/userManage/roles', reqParam).then(res => {
          if (res.code === 0) {
            this.$message({
              type: 'success',
              message: res.msg
            });
            this.init();
            this.cleanSelectRows();
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
