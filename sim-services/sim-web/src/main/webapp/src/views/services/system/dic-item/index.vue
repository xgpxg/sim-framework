<template>
  <div class="app-container">
    <Search v-model.sync="searchParam"
            :options="searchOptions"
            size="small"
            show-add-button
            :inline="true"
            @search="search"
            @add="add"
    ></Search>
    <el-table-plus
      ref="table"
      :load-data="loadData"
      :page-size="10"
      :current-page.sync="currPage"
      :columns="columns"
      @select="select"
      :optionColumn="optionColumn"
      :max-height="'500px'"
      :auto-row-span-key="['createDate','purviewCode']"
      :enable-auto-rowspan="true"
      :is-compact="true"
      :search-data="searchParam"
      enable-auto-rowspan
      :auto-rowspan-key="['dicCode','dicCodeName']"
      border
    >
    </el-table-plus>


    <el-dialog title="新增/修改字典编码"
               v-if="showAddCodeDialog"
               :visible.sync="showAddCodeDialog"
               :close-on-click-modal="false"
               top="2%"
               width="30%">
      <add-dic-item ref="add-dic-code" :init-data="currRow"></add-dic-item>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddCodeDialog = false" size="mini">取 消
        </el-button>
        <el-button type="primary" @click="save" size="mini">确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import ElTablePlus from "@/components/Table/el-table-plus";
  import SearchBar from '@/components/common/SearchBar/SearchBar.vue'
  import SearchBarItem from "@/components/common/SearchBar/search-bar-item";
  import Search from "@/components/Search/search";
  import AddDicItem from "./add-dic-item";

  export default {
    name: "index",
    components: {AddDicItem, Search, ElTablePlus, SearchBarItem, SearchBar},
    data() {
      return {
        searchOptions: [
          {
            type: 'input',
            prop: 'itemText',
            label: '模糊查询',
            placeholder: '字典编码/名称/字典项编码/字典项名称'
          },
        ],
        columns: [
          {
            label: '字典编码', prop: 'dicCode', align: 'center'
          },
          {
            label: '编码名称', prop: 'dicCodeName', align: 'center'
          },
          {
            label: '字典项编码', prop: 'itemCode', align: 'center'
          },
          {
            label: '字典项名称', prop: 'itemText', align: 'center'
          },
          {
            label: '字典项值', prop: 'itemValue', align: 'center'
          },
          {
            label: '描述', prop: 'description'
          }, {
            label: '创建时间', prop: 'createDate',width:190
          },

        ],
        currPage: 1,
        optionColumn: {
          options: [
            {
              type: 'text',
              text: '编辑',
              handler: (row) => {
                this.edit(row);
              }
            }, {
              type: 'text',
              text: '删除',
              handler: (row) => {
                this.del(row)
              }
            }
          ],
          width: '100px',
        },
        searchParam: {
          dicCode: ''
        },
        currRow: null,
        showAddCodeDialog: false
      }
    },
    methods: {
      loadData(resolve, pageInfo, searchData) {
        debugger;
        this.R.get('/api/system/dicCodeItem/item', {...pageInfo, ...searchData}).then(res => {
          resolve(res.data)
        })
      },
      search(){
        this.$refs['table'].refresh();
      },
      add() {
        this.currRow = null;
        this.showAddCodeDialog = true;
      },
      edit(row) {
        this.currRow = row;
        this.showAddCodeDialog = true;
      },
      del(row) {
        this.currRow = row;
      },
      save() {
        this.$refs['add-dic-code'].save();
      }
    }
  }
</script>

<style scoped>

</style>
