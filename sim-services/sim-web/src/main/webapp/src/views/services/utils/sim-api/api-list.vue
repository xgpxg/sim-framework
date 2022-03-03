<template>
  <div class="app-container">
    <el-row :gutter="10">
      <el-col :span="24">
        <div class="box-card">
          <div class="search-list-vue frame-page h-panel">
            <div>
              <el-tabs
                v-model="selected"
                style="padding: 20px;background-color: white"
                :datas="appInfos"
                @tab-click="change"
              >
                <template slot="item" slot-scope="{tab}">
                  <span v-if="tab.status!=='1'" style="color: darkgrey"><span>{{ tab.itemCode }}</span>（离线）</span>
                  <span
                    v-if="tab.status==='1'"
                  ><span>{{ tab.itemCode }}</span></span>
                </template>
              </el-tabs>
            </div>
            <div class="h-panel-bar">
              <div class="search-picker">

                <SearchFilter
                  v-model="params"
                  :datas="dicts.status"
                  prop="status"
                  title="API状态"
                />
                <SearchFilter
                  v-model="params"
                  :datas="dicts.openSimulation"
                  prop="openSimulation"
                  title="模拟状态"
                />
                <SearchFilter
                  v-model="params"
                  :datas="dicts.reqMethod"
                  prop="reqMethod"
                  title="请求方式"
                />
                <SearchFilter
                  v-model="params"
                  :datas="dicts.apiType"
                  prop="apiType"
                  title="注册类型"
                />
                <SearchFilter
                  v-model="params"
                  prop="filter"
                  title="搜索"
                  search
                />
              </div>
            </div>
            <el-table :data="initData" :height="600" :loading="loading">
              <el-table-column
                label="ID"
                prop="apiId"
                align="center"
                :width="80"
              />
              <el-table-column
                label="API地址"
                prop="apiUrl"
                align="center"
              />
              <el-table-column
                label="API名称"
                prop="apiName"
                align="center"
              />
              <el-table-column
                label="请求方式"
                prop="reqMethod"
                align="center"
              />
              <el-table-column label="是否模拟" align="center">
                <template slot-scope="{row}">
                  <span v-if="row.openSimulation===0">否</span>
                  <span v-if="row.openSimulation===1">是</span>
                </template>
              </el-table-column>
              <el-table-column label="状态" align="center">
                <template slot-scope="{row}">
                  <span v-if="row.status==='1000'"><i
                    class="h-icon-success"
                    style="color: green;"
                  /> 有效</span>
                  <span v-if="row.status==='2000'"><i
                    class="h-icon-warn"
                    style="color: orange;"
                  /> 无效</span>
                </template>
              </el-table-column>
              <el-table-column
                label="归属应用"
                prop="appName"
                align="center"
              />
              <el-table-column
                label="应用地址"
                prop="appAddr"
                align="center"
              />
              <el-table-column label="数据模拟" align="center">
                <template slot-scope="{row}">
                  <el-switch
                    v-model="row.openSimulation"
                    :small="true"
                    true-value="1"
                    false-value="0"
                    @change="openOrCloseSimulation(row)"
                  >
                    <span slot="open">开启</span>
                    <span slot="close">关闭</span>
                  </el-switch>
                </template>
              </el-table-column>
              <el-table-column
                label="操作"
                :width="100"
                fixed="right"
                align="center"
              >
                <template slot-scope="{data}">
                  <el-button
                    size="mini"
                    type="primary"
                    @click="openModal(data)"
                  >
                    编辑
                  </el-button>
                </template>
              </el-table-column>
              <div slot="empty">暂无数据</div>
            </el-table>
            <br>
            <el-pagination
              background
              layout="total, prev, pager, next"
              :total="pagination.total"
              :page-size="pagination.pageSize"
              style="text-align: right"
              @current-change="changePage"
            />
            <!--<Pagination v-if="pagination.total>0" :small="false" align="right"
                        v-model="pagination" @change="changePage"
                        layout="pager"></Pagination>-->
            <br>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import SearchFilter from '@/components/SearchFilter/search-filter'
import ApiDetailModal from './api-detail-modal'

export default {
  name: 'ApiList',
  components: {
    SearchFilter,
    ApiDetailModal
  },
  data() {
    return {
      loading: false,
      initData: [],
      pagination: {
        pageSize: 10
      },
      selected: '',
      appInfo: {},
      appInfos: [],
      open: 0,
      appStatus: '',
      dicts: {
        openSimulation: [{ itemCode: '1', itemText: '是' }, {
          itemCode: '0',
          itemText: '否'
        }],
        reqMethod: [
          { itemCode: 'GET', itemText: 'GET' },
          { itemCode: 'POST', itemText: 'POST' },
          { itemCode: 'PATCH', itemText: 'PATCH' },
          { itemCode: 'PUT', itemText: 'PUT' },
          { itemCode: 'DELETE', itemText: 'DELETE' }],
        status: [{ itemCode: '1000', itemText: '有效' }, {
          itemCode: '2000',
          itemText: '无效'
        }],
        apiType: [{ itemCode: '1000', itemText: '自动注册' }, {
          itemCode: '2000',
          itemText: '手动注册'
        }]
      },
      params: {}
    }
  },
  watch: {
    params() {
      this.getData()
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      this.getAppList()
    },
    getData() {
      const that = this
      that.loading = true
      const reqParam = {}
      reqParam.pagination = that.pagination
      if (this.selected !== '') {
        reqParam.appName = this.selected
      }
      that.R.extend(reqParam, this.params)
      that.R.postJson('/api/sim-api/api/api-list', reqParam).then((res) => {
        that.initData = res.data.list
        that.pagination.total = res.data.total
        that.loading = false
      })
    },
    getAppList() {
      const that = this
      that.R.get('/api/sim-api/api/app-list').then((res) => {
        debugger
        const list = res.data
        if (list && list.length > 0) {
          for (let i = 0; i < list.length; i++) {
            console.log(list[i])
            that.R.get('/api/sim-api/api/status', { appAddr: list[i].appAddr }).then((d) => {
              that.appInfos.push({
                itemCode: list[i].appName,
                itemText: list[i].appName,
                status: d.data
              })
              // 默认选择第一项
              if (i === 0) {
                that.selected = that.appInfos[0].itemCode
                that.getData()
              }
            })
          }
        }
      })
    },
    remove(datas, data) {
      log(this.appInfo)
      datas.splice(datas.indexOf(data), 1)
    },
    add(datas) {

    },
    /**
       * 选项卡切换
       */
    change(data) {
      const that = this
      this.getData()
    },
    /**
       * 打开/关闭模拟
       * @param data
       */
    openOrCloseSimulation(data) {
      const that = this
      let open = data.openSimulation
      debugger
      if (!open) {
        open = 1
      } else if (open) {
        open = 0
      }
      that.R.postJson('/api/sim-api/api/open-close-simulation?apiId=' + data.apiId + '&open=' + open).then((res) => {
        if (res.code === 0) {
          data.openSimulation = open
          that.$Message.success(res.data)
        } else {
          data.openSimulation = !open
        }
        that.$Loading.close()
        this.getData()
      })
    },
    changePage(page) {
      this.pagination.page = page
      this.pagination.pageNum = page
      this.getData()
    },
    openModal(data) {

    }
  }
}
</script>

<style scoped>

</style>
