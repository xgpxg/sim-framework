<template>
  <el-tree
    :data="data"
    node-key="id"
    default-expand-all
    :expand-on-click-node="false">
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span class="tree-font"> <svg-icon :icon-class="data.icon" class="icon"/> {{ node.label }}</span>
        <span class="">
          <el-button
            type="text"
            size="mini"
            @click="() => append(data)">

            <el-dropdown trigger="click">
              <span class="el-dropdown-link">
                ···
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>重命名</el-dropdown-item>
                <el-dropdown-item>移动到回收站</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-button>
          <el-button
            type="text"
            size="mini"
            @click="() => remove(node, data)">
            <el-dropdown trigger="click">
              <span class="el-dropdown-link">
                +
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>新建文件夹</el-dropdown-item>
                <el-dropdown-item>新建文档</el-dropdown-item>

              </el-dropdown-menu>
            </el-dropdown>
          </el-button>
        </span>
      </span>

  </el-tree>
</template>

<script>
  export default {
    name: "catalog-tree",
    data() {
      const data = [{
        id: 1,
        label: '所有文档',
        icon: 'home',
        children: [{
          id: 4,
          label: '二级 1-1',
          icon: 'dir2',
          children: [{
            id: 9,
            label: '三级 1-1-1',
            icon: 'dir2',
          }, {
            id: 10,
            label: '三级 1-1-2',
            icon: 'dir2',
          }, {
            id: 10,
            label: '三级 1-1-2',
            icon: 'dir2',
          }, {
            id: 10,
            label: '三级 1-1-2',
            icon: 'dir2',
          }, {
            id: 10,
            label: '三级 1-1-2',
            icon: 'dir2',
          }, {
            id: 10,
            label: '三级 1-1-2',
            icon: 'dir2',
          }, {
            id: 10,
            label: '三级 1-1-2',
            icon: 'dir2',
          }]
        }]
      }];
      return {
        data: JSON.parse(JSON.stringify(data))
      }
    },
    methods: {
      append(data) {
        const newChild = {id: id++, label: 'testtest', children: []};
        if (!data.children) {
          this.$set(data, 'children', []);
        }
        data.children.push(newChild);
      },

      remove(node, data) {
        const parent = node.parent;
        const children = parent.data.children || parent.data;
        const index = children.findIndex(d => d.id === data.id);
        children.splice(index, 1);
      },
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

  .tree-font{
    font-size: 1.0rem;
    margin: 2px 0 2px 0;

  }

  .el-dropdown-link{
    color: darkgrey;
    font-weight: 500;
  }
</style>
