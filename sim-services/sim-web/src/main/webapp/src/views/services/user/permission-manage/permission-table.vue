<template>
  <div>
    <div class="" style="">
      <Search v-model.sync="searchParam"
              :options="searchOptions"
              size="mini"
              show-add-button
              :inline="false"
              @search="search"
              @add="addPermission"
      ></Search>
    </div>

    <el-table-plus
      ref="permission-table"
      :option-column="optionColumn"
      :load-data="loadData"
      :columns="columns"
      :page-size="10"
      height="calc(80vh - 197px)"
      is-compact
      :search-data="searchParam"
    >
      <template slot="purviewType" slot-scope="{row}">
        <span v-if="row.purviewType==='1'">菜单权限</span>
        <span v-if="row.purviewType==='2'">接口权限</span>
        <span v-if="row.purviewType==='3'">页面元素</span>
      </template>
    </el-table-plus>

    <el-dialog
      v-if="isShowAddDialog"
      :title="'新增/修改权限'"
      :visible.sync="isShowAddDialog"
      :close-on-click-modal="false"
      width="35%"
    >
      <AddPermissionDialog
        ref="add-permission-dialog"
        :permission-id="currPermissionId"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="onAddCancel">取 消</el-button>
        <el-button type="primary" @click="onAddOk">保 存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import ElTablePlus from '../../../../components/Table/el-table-plus'
import AddPermissionDialog from './add-permission-dialog'
import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
import SearchBarItem
from '../../../../components/common/SearchBar/search-bar-item'
import Search from "../../../../components/Search/search";

export default {
  name: 'PermissionTable',
  components: {Search, ElTablePlus, AddPermissionDialog, SearchBar, SearchBarItem },
  props: {
    parentId: { type: Number }
  },
  data() {
    return {
      searchOptions:[
        {
          type: 'radio-button',
          prop: 'purviewType',
          label: '权限类型',
          items: [
            { itemCode: '', itemText: '全部' },
            { itemCode: '1', itemText: '菜单权限' },
            { itemCode: '2', itemText: '接口权限' },
            { itemCode: '3', itemText: '页面元素' }
          ]
        },{
          type: 'input',
          prop: 'purviewName',
          label: '权限名称',
          placeholder:'请输入权限名称模糊查询'
        },
      ],
      searchParam: {
        purviewType: null,
        purviewName: null,
        path:null,
      },
      columns: [
        { prop: 'purviewId', label: 'ID', width: '70', align: 'center' },
        { prop: 'purviewName', label: '名称', width: '180', align: 'center' },
        { prop: 'purviewType', label: '类型', width: '100', align: 'center' },
        {
          prop: 'purviewCode',
          label: '编码',
          width: '180',
          align: 'center',
          isSlot: true
        },
        { prop: 'url', label: '访问地址', width: '250', align: 'center' },
        { prop: 'icon', label: '图标', width: '80', align: 'center' },
        { prop: 'component', label: '组件', width: '300', align: 'center' },
        { prop: 'status', label: '状态', width: '80', align: 'center' },
        { prop: 'hidden', label: '是否隐藏', width: '80', align: 'center' }
      ],
      optionColumn: {
        options: [
          {
            type: 'text',
            text: '编辑',
            handler: (row) => {
              this.updatePermission(row)
            }
          }, {
            type: 'text',
            text: '删除',
            handler: (row) => {
              this.deletePermission(row)
            }
          }
        ],
        fixed: 'right',
        width: '120'
      },
      currPermissionId: null,
      isShowAddDialog: false
    }
  },
  computed: {
    pid() {
      return this.parentId
    }
  },
  watch: {
    pid:{
      handler(newVal) {
        this.R.get('/api/auth/permission/' + newVal, {}).then(res => {
          this.searchParam.path = res.data.path;
          this.search();
        });
      },
    }
  },
  mounted() {

  },
  methods: {
    loadData(resolve, pageInfo, searchData) {
      this.R.get('/api/auth/permission', { ...pageInfo, ...searchData }).then(res => {
        resolve(res.data)
      })
    },
    search(){
      this.$refs['permission-table'].refresh();
    },
    addPermission() {
      this.currPermissionId = null
      this.isShowAddDialog = true
      this.$refs['permission-table'].refresh()
    },
    updatePermission(row) {
      this.currPermissionId = row.purviewId
      this.isShowAddDialog = true
      this.$refs['permission-table'].refresh()
    },
    deletePermission(row) {
      this.$confirm('确定删除权限？', '提示').then(() => {
        this.R.delete('/api/auth/permission/' + row.purviewId).then(res => {
          if (res.code === 0) {
            this.$message.success('删除成功')
            this.$refs['permission-table'].refresh()
          }
        })
      })
    },
    onAddOk() {
      // 调用子组件保存 保存成功关闭弹框
      this.$refs['add-permission-dialog'].submit((isOk) => {
        if (isOk) {
          this.isShowAddDialog = false
        }
      })
    },
    onAddCancel() {
      this.isShowAddDialog = false
    }
  }
}
</script>

<style scoped>

</style>
