<template>
  <div>
    <el-cascader
      v-model="_value"
      :options="selectTreeData"
      :props="props"
      @change="handleChange"></el-cascader>
  </div>
</template>

<script>
  export default {
    name: "permission-select-tree",
    props:{
      value:Number
    },
    data() {
      return {
        selectTreeData: [],
        _value: -1,
        props: {
          value: 'purviewId',
          label: 'purviewName',
          checkStrictly: true,
          emitPath: false
        }
      };
    },
    computed:{
      _value(){
        return this.value
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        this.initPermissionTree();
      },
      initPermissionTree() {
        let that = this;
        let param = this.U.copy(this.searchParam);
        this.R.get('/api/auth/permission/tree', param).then(res => {
          that.selectTreeData = [{
            purviewName: '根目录',
            purviewId: -1,
            children: res.data
          }];
          that.loading = false;
          that.$forceUpdate()
        })
      },
      handleChange(value) {
        this.$emit('on-select',value)
      }
    }
  }
</script>

<style scoped>

</style>
