<!--用户授权弹窗-->
<template>
  <el-row>
    <el-col :span="6">
      <div class="border" style="padding-right: 0">
        <el-scrollbar style="height: 420px;">
          <PermissionTree ref="permission-tree" @node-click="nodeClick"></PermissionTree>
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
                         v-model="searchParam.purviewName"
                         :label="'权限名称：'"
                         :placeholder="'请输入权限名称模糊查询'"
                          @reset="reset"></SearchBarItem>
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
            <el-button size="mini" @click="reset">
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
            >
          </el-table-column>
          <el-table-column prop="purviewId" label="ID"
                           width="60"></el-table-column>
          <el-table-column prop="purviewName" width="150"
                           label="权限名称"></el-table-column>
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
              <el-button :disabled="row.purviewObject==='role'" type="text"
                         v-if="row.isPermissions==='1'"
                         @click="delPurview(row)">解除授权
              </el-button>
              <el-button :disabled="row.purviewObject==='role'" type="text"
                         v-if="row.isPermissions==='0'"
                         @click="addPurview(row)">授权
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
              :page-size="pageInfo.pageSize"
              @current-change="currentChange"
              :current-page="pageInfo.pageNum"
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
    name: "role-permission",
    components: {PermissionTree, SearchBarItem, SearchBar},
    props: {
      currRoleId: Number
    },
    data() {
      return {
        initData: {
          isPermissions: [
            {itemText: '全部', itemCode: ''},
            {itemText: '已授权', itemCode: '1'},
            {itemText: '未授权', itemCode: '0'},
          ]
        },
        searchParam: {
          pageSize: 5,
          isPermissions: '',
          purviewId: null,
          purviewName: null
        },
        searchParam_:null,
        userPermissions: [],
        pageInfo: {
          pageNum:1,
          pageSize:5,
          total:0
        },
        loading: false,
        selectedRows: []
      }
    },
    mounted() {
      this.searchParam_ = this.U.copy(this.searchParam);
      this.$refs['isPermissions'].value = '';

      this.init();
    },
    watch: {
      searchParam: {
        handler(newVal, oldVal) {
          this.cleanSelectRows();
          this.search();
        },
        deep: true,
      },
      "searchParam.isPermissions":{
        handler(){
          this.pageInfo.pageNum = 1;
        }
      }

    },
    methods: {
      init() {
        this.pageInfo.pageNum = 1;
        this.search();
      },
      search() {
        this.qryUserPermissions();
      },
      qryUserPermissions() {
        let reqParam = this.U.copy(this.searchParam);
        reqParam = {...reqParam,...this.pageInfo};
        reqParam.roleId = this.currRoleId;
        this.R.get('/api/auth/permission/rolePermission', reqParam).then(res => {
          this.pageInfo.total = res.data.total;
          this.userPermissions = res.data.list;
        })
      },
      currentChange(currPage) {
        this.pageInfo.pageNum = currPage;
        this.search();
      }, /**
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
        if (row.purviewObject === 'user' || this.searchParam.isPermissions === '0') {
          return true;
        }
      },
      cleanSelectRows() {
        this.selectedRows = [];
      },
      /**
       * 新增授权 option=1
       * @param row
       */
      addPurview(row) {
        let reqParam = {
          roles: [this.currRoleId],
          permissions: [row.purviewId],
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
          roles: [this.currRoleId],
          permissions: [row.purviewId],
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
        let selectPermissionsIds = this.selectedRows.map(item => item.purviewId);
        let reqParam = {
          roles: [this.currRoleId],
          permissions: selectPermissionsIds,
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
        let selectPermissionsIds = this.selectedRows.map(item => item.purviewId);
        let reqParam = {
          roles: [this.currRoleId],
          permissions: selectPermissionsIds,
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
       * 树节点点击事件
       * @param data
       */
      nodeClick(data) {
        this.R.get('/api/auth/permission/' + data.purviewId, {}).then(res => {
          this.searchParam.purviewId = data.purviewId;
        });

      },
      clean() {
        this.searchParam.purviewId = null;
      },
      reset(){
        this.$refs['permission-tree'].refresh();
        this.pageInfo.pageNum = 1;
        this.$refs['isPermissions'].value = '';
        this.searchParam = {...this.searchParam_};
      }
    }
  }
</script>

<style scoped>

</style>
