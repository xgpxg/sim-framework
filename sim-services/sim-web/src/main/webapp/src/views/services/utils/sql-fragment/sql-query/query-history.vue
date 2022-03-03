<template>
  <div>
    <el-table size="mini" border height="210" :data="historyList">
      <el-table-column>
        <el-table-column
          prop="createDate"
          label="开始时间"
          align="center"
          width="150">
        </el-table-column>
        <el-table-column
          prop="content"
          label="SQL"
          align="center">
        </el-table-column>
        <el-table-column
          prop="name"
          label="结果"
          width="80"
          align="center">
        </el-table-column>
        <el-table-column
          prop="useTime"
          label="耗时"
          width="80"
          align="center">
        </el-table-column>
        <el-table-column
          prop="name"
          label="操作"
          width="100"
          align="center">
          <template slot-scope="scope">
            再次执行
          </template>
        </el-table-column>
        <template slot="header">
          <el-input v-model="searchParam.filterText" size="mini"
                    style="width: 92%;margin-right: 3px"
                    placeholder="请输入内容"></el-input>
          <el-button size="mini">搜索</el-button>
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
  </div>
</template>

<script>
  export default {
    name: "query-history",
    data() {
      return {
        //执行历史表数据
        historyList: [
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},
          {createDate:'2021-04-30 18:50:41',content:'select * from user',useTime:'52'},

        ],
        searchParam: {
          filterText: ''
        },
        pageInfo: {
          total:20
        },
      }
    }
  }
</script>

<style scoped>

</style>
