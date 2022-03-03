<template>
  <Card>
    <!--<SecondMenu></SecondMenu>-->
    <el-container :style="{height: containerHeight}">
      <el-aside width="300px" >
        <catalog-tree></catalog-tree>
      </el-aside>
      <el-main class="el-main">
        <catalog-main></catalog-main>
      </el-main>
    </el-container>

  </Card>
</template>

<script>
  import SecondMenu from "../../../../components/SecondMenu/index";
  import CatalogTree from "./catalog-tree";
  import CatalogMain from "./catalog-main";
  import Card from "../../../../components/Card/Card";

  export default {
    name: "index",
    components: {Card, CatalogMain, CatalogTree, SecondMenu},
    computed:{
      containerHeight(){
        return (window.innerHeight - 100) + 'px';
      }
    }
  }
</script>

<style scoped>
  .app-container {
    padding-left: 0;
    padding-top: 10px;
    height: 100%;
  }

  .el-container {
    height: 100%;
    padding: 0 10px 0 0;
  }

  aside {
    background-color: white;
    border-right: #eaeaea solid 1px;
    padding: 8px 2px;
  }

  main {
    background-color: white;
  }
  /deep/ .el-main{
    padding-left: 40px;
  }
</style>
