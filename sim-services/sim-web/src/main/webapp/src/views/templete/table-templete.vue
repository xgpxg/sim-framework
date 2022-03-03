<template>
  <el-row :gutter="10">
    <el-col :span="24">
      <div class="box-card">

        <div class="grid-content bg-purple-light">
          <div class="" style="margin-bottom: 10px">
            <SearchBar>
              <SearchBarItem :type="'radio'"
                             v-model="searchParam.status"
                             :label="'用户类型：'"
                             :data="initData.status"></SearchBarItem>
              <SearchBarItem :type="'radio'"
                             v-model="searchParam.userType"
                             :label="'用户状态：'"
                             :data="initData.userTypes"></SearchBarItem>
            </SearchBar>
          </div>
          <el-row class="mb20">
            <el-col :span="2">
              <el-button type="primary" size="mini"
                         @click="addMerchant">+ 新增
              </el-button>
            </el-col>
            <el-col :span="22">
              <el-input
                v-model="searchParam.filterText"
                size="mini"
                placeholder="用户ID/用户名"
              >
                <el-button slot="append" size="mini" type="success" @click="search">查询</el-button>
              </el-input>
            </el-col>
          </el-row>
          <el-table
            :data="initData.userList"
            style="width: 100%">

            <el-table-column
              prop="userId"
              label="ID"
              width="100"
              align="center">
            </el-table-column>
            <el-table-column
              prop="createDate"
              label="创建时间"
              align="center"
              :show-overflow-tooltip="true">
            </el-table-column>
            </el-table-column>
            <el-table-column
              prop="statusName"
              align="center"
              label="状态"
              width="120">
            </el-table-column>
            <el-table-column
              label="操作"
              align="center"
              width="200">
              <template slot-scope="{row}">

              </template>
            </el-table-column>


          </el-table>

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
</template>

<script>
  export default {
    name: "table-templete",
    data(){
      return{
        pageInfo: {},
        initData: {
          userTypes: [],
          userList: []
        },
        searchParam: {
          filterText: '',
          pageSize: 10
        },
        loading: false,
      }
    }
  }
</script>

<style scoped>

</style>
