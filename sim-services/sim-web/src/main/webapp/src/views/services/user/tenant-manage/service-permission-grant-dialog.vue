<!--用户授权弹窗-->
<template>
  <el-row>
    <el-col :span="24">
      <div class="" style="height: 460px;margin-left: 10px">
        <SearchBar>
          <SearchBarItem
            ref="isPermissions"
            v-model="searchParam.isAuth"
            :type="'radio'"
            :label="'是否已授权：'"
            :default-value="'1'"
            :data="initData.isPermissions"
          />
          <SearchBarItem
            v-model="searchParam.filterText"
            :type="'input'"
            :label="'服务名称：'"
            :placeholder="'请输入服务名称模糊查询'"
          />
        </SearchBar>
        <el-row style="margin-bottom: 10px">
          <el-col :span="22">
            <el-button
              size="mini"
              :disabled="loading || selectedRows.length === 0"
              @click="batchAddPurview"
            >批量授权
            </el-button>
            <el-button
              size="mini"
              :disabled="loading || selectedRows.length === 0"
              @click="batchDelPurview"
            >批量取消授权
            </el-button>
          </el-col>
          <el-col :span="2">
            <el-button size="mini" @click="refresh">
              <svg-icon icon-class="refresh" />
            </el-button>

          </el-col>
        </el-row>
        <el-table
          v-loading="loading"
          :data="userPermissions"
          size=""
          max-height="250"
          @selection-change="handleSelectionChange"
          @select="handleSelect"
          @select-all="handleSelectAll"
        >
          <el-table-column
            type="selection"
            align="center"
            width="55"
          />
          <el-table-column
            prop="serviceId"
            label="ID"
            width="60"
          />
          <el-table-column prop="serviceName" label="服务名称" />
          <el-table-column
            prop="groupName"
            label="服务分组"
            width="80"
          >默认分组</el-table-column>
          <el-table-column
            prop="url"
            label="服务地址"
            width=""
          />
          <el-table-column
            prop="type"
            label="服务类型"
            width="100"
          >
            <template slot-scope="{row}">
              <span v-if="row.serviceType==='1000'">
                接口服务
              </span>
              <span v-if="row.serviceType==='2000'">
                页面服务
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="effDate" width="180" label="授权时间">
            <template slot-scope="{row}">
              <span v-if="row.isAuth==='1'">{{ row.effDate }}
              </span>
              <span v-if="row.isAuth==='0'">-</span>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center"
            width="120"
            fixed="right"
          >
            <template slot-scope="{row}">
              <el-button
                v-if="row.isAuth==='1'"
                type="text"
                @click="delPurview(row)"
              >解除授权
              </el-button>
              <el-button
                v-if="row.isAuth==='0'"
                type="text"
                @click="addPurview(row)"
              >授权
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
              style="text-align: right"
              @current-change="currentChange"
            />
          </el-col>
        </el-row>
      </div>
    </el-col>
  </el-row>

</template>

<script>
import PermissionTree from '../permission-manage/permission-tree'
import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
import SearchBarItem
from '../../../../components/common/SearchBar/search-bar-item'

export default {
  name: 'ServicePermissionGrantDialog',
  components: { PermissionTree, SearchBarItem, SearchBar },
  props: {
    currUserId: Number
  },
  data() {
    return {
      initData: {
        isPermissions: [
          { itemText: '已授权', itemCode: '1' },
          { itemText: '未授权', itemCode: '0' }
        ]
      },
      searchParam: {
        pageSize: 5,
        isAuth: '1'
      },
      userPermissions: [],
      pageInfo: {},
      loading: false,
      selectedRows: []
    }
  },
  watch: {
    searchParam: {
      handler(newVal, oldVal) {
        this.search()
      },
      deep: true
    },
    'searchParam.isAuth': {
      handler(newVal, oldVal) {
        this.cleanSelectRows()
      },
      deep: true
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      // 解决初始化无法默认选中的问题
      this.$refs['isPermissions'].value = '1'
      this.qryTotal()
      this.search()
    },
    search() {
      this.qryUserPermissions()
    },
    qryTotal() {
      const reqParam = this.U.copy(this.searchParam)
      reqParam.userId = this.currUserId
      reqParam.isAuth = '1'
      this.R.get('/api/auth/tenantManager/service', reqParam).then(res => {
        this.total = res.data.total
      })
    },
    qryUserPermissions() {
      const reqParam = this.U.copy(this.searchParam)
      reqParam.userId = this.currUserId
      this.R.get('/api/auth/tenantManager/service', reqParam).then(res => {
        this.pageInfo.total = res.data.total
        this.userPermissions = res.data.list
      })
    },
    currentChange(currPage) {
      this.searchParam.pageNum = currPage
      this.search()
    }, /**
       * 表格行选中
       * @param selection
       * @param row
       */
    handleSelect(selection, row) {
      this.selectedRows = this.U.copy(selection)
    },
    handleSelectionChange(selection) {
      // alert(JSON.stringify(selection))
    },
    /**
       * 表格全选
       */
    handleSelectAll(selection) {
      this.cleanSelectRows()
      if (selection.length === 0) {
        this.selectedRows = []
      } else {
        this.selectedRows = this.U.copy(selection)
      }
    },
    cleanSelectRows() {
      this.selectedRows = []
    },
    refresh() {
      this.clean()
      this.cleanSelectRows()
      this.search()
    },
    /**
       * 新增授权 option=1
       * @param row
       */
    addPurview(row) {
      const reqParam = {
        userIds: [this.currUserId],
        services: [row.serviceId],
        option: '1'
      }
      this.R.postJson('/api/auth/tenantManager/service', reqParam).then(res => {
        if (res.code === 0) {
          this.$message({
            type: 'success',
            message: res.msg
          })
          this.init()
          this.cleanSelectRows()
        }
      })
    },
    /**
       * 解除授权 option=2
       * @param row
       */
    delPurview(row) {
      const reqParam = {
        userIds: [this.currUserId],
        services: [row.serviceId],
        option: '2'
      }
      this.R.postJson('/api/auth/tenantManager/service', reqParam).then(res => {
        if (res.code === 0) {
          this.$message({
            type: 'success',
            message: res.msg
          })
          this.init()
          this.cleanSelectRows()
        }
      })
    },
    /**
       * 批量授权
       */
    batchAddPurview() {
      const selectServiceIds = this.selectedRows.map(item => item.serviceId)
      const reqParam = {
        userIds: [this.currUserId],
        services: selectServiceIds,
        option: '1'
      }
      this.R.postJson('/api/auth/tenantManager/service', reqParam).then(res => {
        if (res.code === 0) {
          this.$message({
            type: 'success',
            message: res.msg
          })
          this.init()
          this.cleanSelectRows()
        }
      })
    },
    /**
       * 批量解除授权
       */
    batchDelPurview() {
      const selectServiceIds = this.selectedRows.map(item => item.serviceId)
      const reqParam = {
        userIds: [this.currUserId],
        services: selectServiceIds,
        option: '2'
      }
      this.R.postJson('/api/auth/tenantManager/service', reqParam).then(res => {
        if (res.code === 0) {
          this.$message({
            type: 'success',
            message: res.msg
          })
          this.init()
          this.cleanSelectRows()
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
