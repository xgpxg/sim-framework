<template>
  <Card>
    <Search v-model="testData"
            :options="options"
            size="small"
            @search="search"
            @reset="reset"
            @add="add"
    ></Search>
    {{data}}
  </Card>
</template>

<script>
  import Card from "../../components/Card/Card";
  import Search from "../../components/Search/search";

  export default {
    name: "search",
    components: {Card, Search},
    data() {
      return {
        data: {},
        options: [
          {
            type: 'radio-button',
            prop: 'aaa',
            label: '单选：',
            code: 'USER_TYPE',
            items: [{itemCode: '1', itemText: '哈哈哈'}, {
              itemCode: '2',
              itemText: '哈哈哈2'
            }],
            change: () => {
              this.search();
            }
          },
          {
            type: 'checkbox-button',
            prop: 'bbb',
            label: '多选：',
            code: 'USER_STATUS',
            items: [{itemCode: '1', itemText: '哈哈哈'}, {
              itemCode: '2',
              itemText: '哈哈哈2'
            }],
            initValue: [],
            change: () => {
              this.search();
            }
          },
          {
            type: 'select',
            prop: 'ccc',
            label: '下拉选择：',
            items: [{itemCode: '1', itemText: '哈哈哈'}, {
              itemCode: '2',
              itemText: '哈哈哈2'
            }]
          }, {
            type: 'input',
            prop: 'ddd',
            label: '搜索：',
            placeholder: '输入关键字搜索',
            initValue: '213'
          },
        ],
      }
    },
    methods: {
      search() {
        this.$message({
          type: 'success',
          message: this.data
        });
      },
      reset(value) {
        this.$message({
          type: 'success',
          message: value
        });
      },
      add(value) {
        this.$message({
          type: 'success',
          message: value
        });
      }
    }
  }
</script>

<style scoped>

</style>
