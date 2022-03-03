<template>
  <router-view></router-view>
</template>

<script>
  import CodeEditor from "@/components/CodeEditor"
  import SqlList from "./sql-list";

  export default {
    name: "index",
    components: {SqlList, CodeEditor},
    data() {
      return {

      }
    }
  }
</script>

<style scoped>

</style>
