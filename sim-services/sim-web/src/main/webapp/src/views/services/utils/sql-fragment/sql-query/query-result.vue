<template>
    <div>
      <!--执行结果-->
      <el-table size="mini" border :data="queryResult" height="210">
        <el-table-column
          v-for="(item,index) in headers"
          :key="index"
          :label="item"
          :prop="id"
          align="center"
        >
          <template slot-scope="scope">
            {{queryResult[scope.$index][item]}}
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
        name: "query-result",
      data(){
          return{
            pageInfo:{

            },
            searchParam:{},
            //sql查询结果
            queryResult: [
              {id: "123", name: 'wxg'}
            ],

            //sql查询结果表头
            headers: ['id', 'name'],
          }
      }
    }
</script>

<style scoped>

</style>
