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
                  @add="addLimit"
          >
          </Search>
          <el-table-plus
            :option-column="optionColumn"
            :load-data="loadData"
            :columns="columns"
            :page-size="10"
            :search-data="searchParam"
            ref="rate-limit-table"
            is-compact>
            <template slot="routeName" slot-scope="{row}">
              <el-link type="primary" @click="detail(row)">{{row.routeName}}
              </el-link>
            </template>
          </el-table-plus>
        </div>
      </el-col>
    </el-row>

    <el-dialog :title="title"
               v-if="showAddRouteDialog"
               :visible.sync="showAddRouteDialog"
               :close-on-click-modal="false"
               top="2%">
      <AddLimit ref="add-limit" :route-id="currRoute.routeId"
                :action="action"></AddLimit>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddRouteDialog = false" size="mini">取 消
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
  import AddLimit from "./add-limit";
  import Search from "../../../../components/Search/search";

  export default {
    name: "index",
    components: {Search, AddLimit, ElTablePlus, SearchBar, SearchBarItem},
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
          {prop: 'routeId', label: '路由ID', width: '80', align: 'center',},
          {prop: 'routeName', label: '路由名称', width: '150', align: 'center',},
          {prop: 'path', label: '流控规则', align: 'center', width: '200'},
          {prop: 'serviceId', label: '流控规则', align: 'center',},
          {prop: 'status', label: '状态', align: 'center', width: '150'},
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
                return ['1200', '1300'].indexOf(row.status) < 0
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
              text: '限流',
              disabled(row) {
              },
              handler: (row) => {

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
          width: '220',
        },
        tableSearchData: {},

        title: '新增规则',
        showAddRouteDialog: false,
        currRoute: {},
        action: null
      }
    },
    methods: {
      loadData(resolve, pageInfo, searchData) {
        this.R.get('/api/gateway/route', {...pageInfo, ...searchData}).then(res => {
          resolve(res.data);
        })
      },
      refresh() {
        this.$refs['limit-table'].refresh();
      },
      addLimit() {
        this.currRoute = {};
        this.action = null;
        this.showAddRouteDialog = true;
      },
      save() {
        this.$refs['add-limit'].save((success) => {
          if (success) {
            this.$message.success('保存成功');
            this.showAddRouteDialog = false;
            this.refresh();
          }
        })
      },
      edit(row) {
        this.currRoute = row;
        this.action = 'edit';
        this.title = '修改路由';
        this.showAddRouteDialog = true;
      },
      detail(row) {
        this.currRoute = row;
        this.action = 'detail';
        this.title = '路由详情';
        this.showAddRouteDialog = true;
      },
      delete(row) {
        this.$confirm('删除后无法恢复,确认删除?', '提示', {
          distinguishCancelAndClose: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.R.delete('/api/gateway/route/' + row.routeId).then(res => {
            if (res.code === 0) {
              this.$message.success('删除成功');
              this.refresh();
            }
          })
        }).catch(() => {
        });

      },
      online(row) {
        this.R.patchJson('/api/gateway/route/online/' + row.routeId).then(res => {
          if (res.code === 0) {
            this.$message.success('上线成功');
            this.refresh();
          }
        })
      },
      offline(row) {
        this.R.patchJson('/api/gateway/route/offline/' + row.routeId).then(res => {
          if (res.code === 0) {
            this.$message.success('下线成功');
            this.refresh();
          }
        })
      },
      refreshRoute() {
        this.R.postJson('/api/gateway/route/refresh').then(res => {
          if (res.code === 0) {
            this.$message.success('刷新成功，路由信息已同步');
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
