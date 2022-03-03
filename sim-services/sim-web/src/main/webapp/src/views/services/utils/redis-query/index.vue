<template>
  <div class="app-container">

    <el-row>
      <el-col span="8">
        <el-row>
          <el-col span="24">
            <connect-list></connect-list>
          </el-col>
        </el-row>
      </el-col>
      <el-col span="16">
        <ConnectTab></ConnectTab>


      </el-col>
    </el-row>
  </div>

</template>

<script>
  import NewConnection from "./new-connection";
  import ConnectList from "./connection-list";
  import ConnectTab from "./connect-tab";

  export default {
    name: "index",
    components: {ConnectTab, ConnectList, NewConnection},
    data() {
      return {

      }
    },
    watch: {
      activeName(val) {
        this.$router.push(`${this.$route.path}?tab=${val}`)
      }
    },
    created() {
      // init the default selected tab
      const tab = this.$route.query.tab
      if (tab) {
        this.activeName = tab
      }
    },
    methods: {
      showCreatedTimes() {
        this.createdTimes = this.createdTimes + 1
      }
    }
  }
</script>

<style scoped>

</style>
