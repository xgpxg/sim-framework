<template>
  <div>
    <el-table-plus
      :data.sync="data"
      :page-size="10"
      :current-page.sync="currPage"
      data-url="/api/auth/userManage/permissions"
      :columns="columns"
      :selection="true"
      @select="select"
      :optionColumn="optionColumn"
      :max-height="'500px'"
      :auto-row-span-key="['createDate','purviewCode']"
      :enable-auto-rowspan="true"
    >
    </el-table-plus>
  </div>

</template>

<script>
  import ElTablePlus from "../../../components/Table/el-table-plus";

  export default {
    name: "setting",
    components: {ElTablePlus},
    data() {
      return {
        data: {},
        columns: [
          {
            label: '权限ID', prop: 'purviewId', align: 'center'
          },
          {
            label: '权限编码', prop: 'purviewCode'
          },
          {
            label: '权限名称', prop: 'purviewName', autoRowspan: true
          },

        ],
        currPage: 1,
        optionColumn: {
          options: [
            {
              type: 'text',
              text: '新增',
              handler: (row) => {
                alert('点击了新增')
              }
            }, {
              type: 'text',
              text: '删除',
              handler: (row) => {
                alert('点击了删除')
              }
            }
          ],
          width: '100px',
        }
      }
    },
    methods: {
      loadData(resolver) {
        let reqParam = {
          //key为后台封装的对应的当前页key
          pageNum: this.currPage,
        };
        debugger;
        this.R.get('/api/auth/userManage/permissions', reqParam).then(res => {
          debugger;
          resolver(res.data)
        })
      },
      pageChange(page) {
        alert(page)
      },
      select(selection, row) {
        alert(JSON.stringify(selection))
      }
    }
  }
</script>

<style scoped>

</style>
