<template>
  <div class="app-container">
    <el-row :gutter="10">
      <el-col :span="24">
        <div class="box-card">

          <div class="grid-content bg-purple-light">
              <Search v-model.sync="searchParam"
                      :options="searchOptions"
                      size="small"
                      show-add-button
                      :inline="true"
                      @search="refresh"
                      @add="addRoute"
              >
                <template slot="button">
                    <el-button @click="refreshRoute" type="primary" size="mini">
                      <svg-icon icon-class="refresh"></svg-icon>
                      刷新路由
                    </el-button>
                </template>
              </Search>

            <el-table-plus
              :option-column="optionColumn"
              :load-data="loadData"
              :columns="columns"
              :page-size="10"
              :search-data="searchParam"
              ref="route-table"
              is-compact>
              <template slot="routeName" slot-scope="{row}">
                <el-link type="primary" @click="detail(row)">{{row.routeName}}
                </el-link>
              </template>
            </el-table-plus>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog :title="title"
               v-if="showAddRouteDialog"
               :visible.sync="showAddRouteDialog"
               :close-on-click-modal="false"
               top="2%">
      <AddRoute ref="add-route" :route-id="currRoute.routeId"
                :action="action"></AddRoute>
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
  import AddRoute from "./add-route";
  import Search from "../../../../components/Search/search";

  export default {
    name: "index",
    components: {Search, AddRoute, ElTablePlus, SearchBar, SearchBarItem},
    data() {
      return {
        searchParam: {
          status: null,
          types: [],
          filterText: null
        },
        searchOptions: [
          {
            type: 'radio-button',
            prop: 'type',
            label: '路由类型：',
            items: []
          }, {
            type: 'radio-button',
            prop: 'statusCd',
            label: '路由状态：',
            items: [
              {itemCode: '1200', itemText: '未生效'},
              {itemCode: '1000', itemText: '已上线'},
              {itemCode: '1300', itemText: '已下线'},
            ]
          }, {
            type: 'radio-button',
            prop: 'statusCd',
            label: '路由分组：',
            items: [
              {itemCode: '1', itemText: '内部接口'},
              {itemCode: '2', itemText: '对外接口'},
            ]
          },{
            type:'empty'
          }, {
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
          ],
          groups: [
            {itemCode: '1', itemText: '内部接口'},
            {itemCode: '2', itemText: '对外接口'},
          ]
        },
        columns: [
          {prop: 'routeId', label: '路由ID', width: '80', align: 'center',},
          {prop: 'routeName', label: '路由名称', width: '150', align: 'center',},
          {prop: 'path', label: '转发规则', align: 'center', width: '200'},
          {prop: 'serviceId', label: '应用ID', align: 'center',},
          {prop: 'url', label: '应用地址', align: 'center',},
          {prop: 'groupId', label: '路由分组', align: 'center', width: '100',},
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

        title: '新增路由',
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
        this.$refs['route-table'].refresh();
      },
      addRoute() {
        this.currRoute = {};
        this.action = null;
        this.showAddRouteDialog = true;
      },
      save() {
        this.$refs['add-route'].save((success) => {
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
