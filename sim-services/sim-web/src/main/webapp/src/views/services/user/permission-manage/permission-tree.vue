<template>
  <el-tree
    :data="treeData"
    node-key="purviewId"
    :expand-on-click-node="false"
    @node-click="nodeClick"
  >
    <span slot-scope="{ node, data }" class="custom-tree-node">
      <el-tooltip open-delay="200" class="item" effect="light" :content="data.purviewCode" placement="right-start">
        <span>
          <svg-icon
            v-if="data.purviewType==='1'"
            icon-class="menu"
            class="icon tree-icon-1"
          />
          <svg-icon
            v-if="data.purviewType==='2'"
            icon-class="interface"
            class="icon"
          />
          {{ data.purviewName }}</span>
      </el-tooltip>
      <span />
    </span>
  </el-tree>
</template>

<script>
export default {
  name: 'PermissionTree',
  data() {
    /* const data = [{
        id: 1,
        label: '一级 1',
        icon: 'permission2',
        children: [{
          id: 4,
          label: '二级 1-1',
          children: [{
            id: 9,
            label: '三级 1-1-1'
          }, {
            id: 10,
            label: '三级 1-1-2'
          }]
        }]
      }, {
        id: 2,
        label: '一级 2',
        children: [{
          id: 5,
          label: '二级 2-1'
        }, {
          id: 6,
          label: '二级 2-2'
        }]
      }, {
        id: 3,
        label: '一级 3',
        children: [{
          id: 7,
          label: '二级 3-1'
        }, {
          id: 8,
          label: '二级 3-2'
        }]
      }];*/
    return {
      treeData: []

    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      this.initPermissionTree()
    },
    initPermissionTree() {
      const that = this
      const param = this.U.copy(this.searchParam)
      this.R.get('/api/auth/permission/tree', param).then(res => {
        that.treeData = res.data
        console.log(res.data)
        that.loading = false
        that.$forceUpdate()
      })
    },
    nodeClick(data, node) {
      this.$emit('node-click', data)
    },
    append(data) {
      this.$emit('node-append', data)
    },
    remove(node, data) {
      const parent = node.parent
      const children = parent.data.children || parent.data
      const index = children.findIndex(d => d.id === data.id)
      children.splice(index, 1)
      this.$emit('node-remove', data)
    },
    refresh() {
      this.init()
    }
  }
}
</script>

<style scoped>
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
  }

  .tree-icon-1 {
  }

  /deep/ .el-tree-node.is-current > .el-tree-node__content {
    background-color: #449bfe !important;
    color: white;
  }

  /deep/ .el-checkbox__input.is-checked+.el-checkbox__label {
    color: black;
  }
</style>
