<template>
  <div class="shadow">
    <el-row :gutter="20">
      <el-col :span="15">
        <CodeEditor></CodeEditor>

        <div class="tabs">
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="执行历史" name="first">
              <QueryHistory></QueryHistory>
            </el-tab-pane>
            <el-tab-pane label="查询结果" name="second">
              <QueryResult></QueryResult>
            </el-tab-pane>

          </el-tabs>
        </div>
      </el-col>
      <el-col :span="9">
        <div class="right-tabs">
          <el-tabs v-model="sqlFragmentTab">
            <el-tab-pane label="数据源信息" name="first">
              <DsInfo></DsInfo>
            </el-tab-pane>
            <!--<el-tab-pane label="状态监控" name="2">
              <DsInfo></DsInfo>
            </el-tab-pane>-->
          </el-tabs>
        </div>
        <div class="right-tabs">
          <el-tabs v-model="sqlFragmentTab">
            <el-tab-pane label="SQL片段" name="first">
              <SqlFragment></SqlFragment>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import CodeEditor from "./CodeEditor";
  import QueryHistory from "./query-history";
  import QueryResult from "./query-result";
  import SqlFragment from "./sql-fragment";
  import DsInfo from "./ds-info";

  export default {
    name: "index",
    components: {DsInfo, SqlFragment, QueryResult, QueryHistory, CodeEditor},
    data() {
      return {
        activeName: 'first',
        sqlFragmentTab: 'first'
      }
    }
  }
</script>

<style scoped>
  /deep/ .el-card.box-card.is-never-shadow {
    border: none;
  }

  /deep/ .el-card__header {
    border-left: #20a0ff solid 4px;
    padding: 3px 10px;
    border-bottom: none;
  }

  /deep/ .el-table thead th {
    background: #F5F7FA;
    color: #525252;
    font-weight: normal;
    line-height: normal;
  }
  /deep/ .el-table td{
    border-bottom: 1px solid #ebf2f8
  }

  .tabs {
    margin-left: 10px;
  }

  .right-tabs {
    margin-right: 10px;
  }

  /deep/ .el-tabs__item{
    font-size: 12px;
    line-height: 50px

  }

  /deep/ .el-tabs__nav-wrap::after {
    content: "";
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 1px;
    background-color: #dfe4ed;
    z-index: 1;
  }
</style>
