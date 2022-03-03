<template>
  <div class="app-container">
    <el-row :gutter="10">
      <el-col :span="24">
        <div class="box-card">
          <Search v-model.sync="searchParam"
                  :options="searchOptions"
                  size="small"
                  show-add-button
                  :inline="true"
                  @search="refresh"
                  @add="addService"
          >
          </Search>
          <el-table-plus
            :option-column="optionColumn"
            :load-data="loadData"
            :columns="columns"
            :page-size="10"
            :search-data="searchParam"
            ref="service-table"
            is-compact>
          </el-table-plus>
        </div>
      </el-col>
    </el-row>

    <el-dialog :title="title"
               v-if="showAddServiceDialog"
               :visible.sync="showAddServiceDialog"
               :close-on-click-modal="false"
               top="2%">
      <add-service ref="add-service"></add-service>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddServiceDialog = false" size="mini">取 消
        </el-button>
        <el-button type="primary" @click="save" size="mini">确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import ElTablePlus from "../../../../components/Table/el-table-plus";
  import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
  import SearchBarItem
    from "../../../../components/common/SearchBar/search-bar-item";
  import AddService from "./add-service";
  import Search from "../../../../components/Search/search";

  export default {
    name: "index",
    components: {Search, AddService, ElTablePlus, SearchBar, SearchBarItem},
    data() {
      return {
        searchParam: {
          status: null,
          types: [],
          filterText: null
        },
        searchOptions: [
          {
            type: 'input',
            prop: 'filterText',
            label: '模糊查询：',
            placeholder: '路由名称/路径/服务ID'
          },
        ],
        initData: {
          status: [
            {itemCode: '1200', itemText: '未生效'},
            {itemCode: '1000', itemText: '已上线'},
            {itemCode: '1300', itemText: '已下线'},
          ]
        },
        columns: [
          {prop: 'serviceId', label: 'ID', width: '80', align: 'center',},
          {prop: 'name', label: '服务名称', align: 'center',},
          {prop: 'type', label: '服务类型', width: '150', align: 'center',},
          {prop: 'url', label: '服务地址', align: 'center'},
          {prop: 'status', label: '状态', align: 'center', width: '100'},
          {prop: 'createDate', label: '创建时间', align: 'center', width: '160'},
        ],
        optionColumn: {
          options: [
            {
              type: 'text',
              text: '编辑',
              disabled(row) {
              },
              handler: (row) => {
                this.edit(row);
              }
            }, {
              type: 'text',
              text: '上线',
              disabled(row) {
                return ['1200'].indexOf(row.status) < 0
              },
              handler: (row) => {
                this.online(row);
              }
            }, {
              type: 'text',
              text: '下线',
              disabled(row) {
                return ['1000'].indexOf(row.status) < 0
              },
              handler: (row) => {
                this.offline(row);
              }
            }, {
              type: 'text',
              text: '删除',
              disabled(row) {
                return ['1200', '1300'].indexOf(row.status) < 0
              },
              handler: (row) => {
                this.delete(row)
              }
            }
          ],
          width: '180',
        },
        tableSearchData: {},

        title: '新增服务',
        showAddServiceDialog: false,
        currService: {},
        action: null
      }
    },
    methods: {
      loadData(resolve, pageInfo, searchData) {
        this.R.get('/api/gateway/service-manager', {...pageInfo, ...searchData}).then(res => {
          resolve(res.data);
        })
      },
      refresh() {
        this.$refs['service-table'].refresh();
      },
      addService() {
        this.currService = {};
        this.action = null;
        this.title = '新增服务';
        this.showAddServiceDialog = true;
      },
      save() {
        this.$refs['add-service'].save((success) => {
          if (success) {
            this.$message.success('保存成功');
            this.showAddServiceDialog = false;
            this.refresh();
          }
        })
      },
      edit(row) {
        this.currService = row;
        this.action = 'edit';
        this.title = '修改服务';
        this.showAddServiceDialog = true;
      },
      detail(row) {
        this.currService = row;
        this.action = 'detail';
        this.title = '服务详情';
        this.showAddServiceDialog = true;
      },
      delete(row) {
        this.$confirm('删除后无法恢复,确认删除?', '提示', {
          distinguishCancelAndClose: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.R.delete('/api/gateway/service-manager/' + row.routeId).then(res => {
            if (res.code === 0) {
              this.$message.success('删除成功');
              this.refresh();
            }
          })
        }).catch(() => {
        });

      },
      online(row) {
        this.R.patchJson('/api/gateway/service-manager/online/' + row.routeId).then(res => {
          if (res.code === 0) {
            this.$message.success('上线成功');
            this.refresh();
          }
        })
      },
      offline(row) {
        this.R.patchJson('/api/gateway/service-manager/offline/' + row.routeId).then(res => {
          if (res.code === 0) {
            this.$message.success('下线成功');
            this.refresh();
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
