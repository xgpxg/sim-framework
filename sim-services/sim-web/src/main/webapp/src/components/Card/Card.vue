<template>
    <el-card class="box-card" shadow="never" :style="{height:height?height:''}">
        <template slot="header">
            <slot name="header"></slot>
        </template>
        <slot></slot>
    </el-card>
</template>

<script>
  export default {
    name: "Card",
    props: {
      height: {type: String}
    }
  }
</script>

<style scoped>
    .box-card {
        min-height: calc(100vh - 55px);
    }

    /deep/ .el-card {
        border-radius: unset;
    }

    /deep/ .el-card__header {
        padding: 15px 20px;
        font-weight: bold;
    }
</style>