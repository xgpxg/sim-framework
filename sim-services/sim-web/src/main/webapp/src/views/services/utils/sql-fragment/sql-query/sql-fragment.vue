<template>
  <div>
    <el-table size="mini" border height="210">
      <el-table-column>
        <el-table-column
          prop="id"
          label="片段名称"
          width="80"
          align="center">
        </el-table-column>
        <el-table-column
          prop="id"
          label="SQL"
          align="center">
        </el-table-column>
        <el-table-column
          prop="name"
          label="操作"
          width="100"
          align="center">
        </el-table-column>
        <template  slot="header">
          <el-input v-model="searchParam.filterText" size="mini" style="width: 85%;margin-right: 3px"
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
    name: "sql-fragment",
    data(){
      return{
        pageInfo:{

        },
        searchParam:{},
      }
    }
  }
</script>

<style scoped>

</style>
