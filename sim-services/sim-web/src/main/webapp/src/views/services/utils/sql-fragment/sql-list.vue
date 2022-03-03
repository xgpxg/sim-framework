<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-table
          :data="tableData"
          style="width: 100%">
          <el-table-column>
            <el-table-column
              prop="id"
              label="ID"
              width="80"
              align="center">
            </el-table-column>
            <el-table-column
              prop="name"
              label="名称"
              width="200"
              align="center">
            </el-table-column>
            <el-table-column
              prop="name"
              label="分类"
              width="100"
              align="center">
            </el-table-column>
            <el-table-column
              prop="datasource"
              label="数据源"
              width="100"
              align="center">
            </el-table-column>
            <el-table-column
              prop="sql"
              label="SQL"
              align="center">
            </el-table-column>
            <el-table-column
              label="操作"
              width="150"
              align="center">
              <template slot-scope="scope">
                <el-button-group>
                  <el-button type="text" icon="el-icon-video-play" size=""
                             class="btn" color="green" style="color: green"
                             title="执行"></el-button>
                  <el-button type="text" icon="el-icon-delete" size=""
                             class="btn" style="color: orange"
                             title="删除"></el-button>
                  <el-button type="text" icon="el-icon-time" size=""
                             class="btn" style="color: grey"
                             title="历史"></el-button>

                </el-button-group>
              </template>
            </el-table-column>
            <template slot="header" slot-scope="scope">
              <el-col :span="2">
                <el-button type="success" size="mini"
                           @click="$router.push({path:'/util/sql-fragment/sql-query'})">
                  + 新增
                </el-button>
              </el-col>
              <el-col :span="20">
                <el-input
                  v-model="searchParam.filterText"
                  size="mini"
                  placeholder="ID/名称"
                />
              </el-col>
              <el-col :span="2">
                <el-button size="mini" @click="search">查询</el-button>
              </el-col>
            </template>
          </el-table-column>
        </el-table>
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
      </el-col>
    </el-row>
  </div>

</template>

<script>
  export default {
    name: "sql-list",
    data() {
      return {
        initData: {},
        searchParam: {},
        pageInfo: {},
        tableData: [{
          id: '1',
          name: '查询列表',
          sql: 'select * from aaa',
          datasource: 'sim'
        }, {
          id: '2',
          name: '查询详情',
          sql: 'select * from bbbbb',
          datasource: 'sim'
        }]
      }
    }
  }
</script>

<style scoped>
  .btn {
    border: none;
    padding: 3px 18px 3px 0;
  }

</style>
