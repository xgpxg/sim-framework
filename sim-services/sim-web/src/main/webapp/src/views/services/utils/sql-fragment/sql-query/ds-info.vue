<template>
    <div>
      <el-table size="mini" border height="285" :data="tableList">
        <el-table-column type="expand">
          表信息：数据库、表名、字段信息
        </el-table-column>
        <el-table-column
          prop="tableName"
          label="表"
          align="center"
          >
        </el-table-column>
      </el-table>
    </div>
</template>

<script>
    export default {
        name: "ds-info",
      data(){
          return{
            tableList:[{tableName:'aaa'}]
          }
      }
    }
</script>

<style scoped>

</style>
