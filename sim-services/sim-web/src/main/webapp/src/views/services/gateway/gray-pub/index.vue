<template>
  <div class="app-container">
    <el-row :gutter="10">
      <el-col :span="24">
        <div class="box-card">

          <div class="grid-content bg-purple-light">
            <div class="" style="margin-bottom: 10px">
              <SearchBar>
                <el-row :gutter="10">
                  <el-col :span="5">
                    <SearchBarItem :type="'select'"
                                   v-model="searchParam.serviceId"
                                   :label="'服务ID：'"
                                   :data="initData.serviceIds"></SearchBarItem>
                  </el-col>
                  <el-col :span="12">
                    <SearchBarItem :type="'input'"
                                   v-model="searchParam.filterText"
                                   :label="'路由名称：'"
                    ></SearchBarItem>
                  </el-col>
                </el-row>
                <!-- <SearchBarItem :type="'radio'"
                                v-model="searchParam.orgId"
                                :label="'所属机构：'"
                                :data="initData.orgIds"></SearchBarItem>-->
              </SearchBar>
            </div>
            <div class="mb10 ">
              <el-button @click="addRoute" type="primary" size="mini">
                <svg-icon icon-class="add2"></svg-icon>
                新增
              </el-button>
              <el-button @click="refreshRoute" type="success" size="mini">
                <svg-icon icon-class="refresh"></svg-icon>
                刷新路由
              </el-button>
            </div>
            <el-table-plus
              :option-column="optionColumn"
              :load-data="loadData"
              :columns="columns"
              :page-size="10"
              :search-data="searchParam"
              ref="route-table"
              is-compact>
              <template slot="grayRouteName" slot-scope="{row}">
                <el-link type="primary" @click="detail(row)">
                  {{row.grayRouteName}}
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
      <AddGrayRoute ref="add-route" :route-gray-id="currGrayRoute.routeGrayId"
                    :action="action"></AddGrayRoute>
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
  import AddGrayRoute from "./add-gray-route";

  export default {
    name: "index",
    components: {AddGrayRoute, ElTablePlus, SearchBar, SearchBarItem},
    data() {
      return {
        searchParam: {
          status: null,
          types: [],
          filterText: null
        },
        initData: {
          status: [
            {itemCode: '1200', itemText: '未生效'},
            {itemCode: '1000', itemText: '已上线'},
            {itemCode: '1300', itemText: '已下线'},
          ],
          serviceIds: []
        },
        columns: [
          {prop: 'routeGrayId', label: 'ID', width: '70', align: 'center',},
          {prop: 'routeId', label: '关联路由', width: '80', align: 'center',},
          {
            prop: 'grayRouteName',
            label: '名称',
            align: 'center',
            showOverflowTooltip: true
          },
          {
            prop: 'serviceId',
            label: '服务',
            align: 'center',
            showOverflowTooltip: true
          },
          {
            prop: 'path',
            label: '转发规则',
            align: 'center',
            width: '200',
            showOverflowTooltip: true
          },
          {prop: 'mainVersion', label: '正式版本', align: 'center', width: 80},
          /*{prop: 'status', label: '正式流量', align: 'center',width:80},*/
          {prop: 'grayVersion', label: '灰度版本', align: 'center', width: 80},
          /*{prop: 'status', label: '灰度流量', align: 'center',width:80},*/
          {prop: 'type', label: '灰度规则', align: 'center', width: 160},

        ],
        optionColumn: {
          options: [
            {
              type: 'text',
              text: '编辑',
              disabled(row) {
                return ['1200', '1300'].indexOf(row.status) < 0
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
              text: '转正',
              disabled(row) {
                return ['1000'].indexOf(row.status) < 0
              },
              handler: (row) => {
                this.formal(row);
              }
            }, {
              type: 'text',
              text: '复制',
              disabled(row) {
              },
              handler: (row) => {
                alert('复制灰度路由')
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
          width: '240',
        },
        tableSearchData: {},

        title: '新增灰度路由',
        showAddRouteDialog: false,
        currGrayRoute: {},
        action: null
      }
    },
    mounted() {
      this.loadServiceIds();
    },
    methods: {
      loadData(resolve, pageInfo, searchData) {
        this.R.get('/api/gateway/grayRoute', {...pageInfo, ...searchData}).then(res => {
          resolve(res.data);
        })
      },
      refresh() {
        this.$refs['route-table'].refresh();
      },
      loadServiceIds() {
        this.R.get('/api/gateway/service-info', {}).then((res) => {
          this.initData.serviceIds = res.data.map(item => {
            return {itemCode: item, itemValue: item}
          });
        })
      },
      addRoute() {
        this.currGrayRoute = {};
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
        this.currGrayRoute = row;
        this.action = 'edit';
        this.title = '修改灰度路由';
        this.showAddRouteDialog = true;
      },
      detail(row) {
        this.currGrayRoute = row;
        this.action = 'detail';
        this.title = '灰度路由详情';
        this.showAddRouteDialog = true;
      },
      delete(row) {
        this.$confirm('删除后无法恢复,确认删除?', '提示', {
          distinguishCancelAndClose: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.R.delete('/api/gateway/grayRoute/' + row.routeGrayId).then(res => {
            if (res.code === 0) {
              this.$message.success('删除成功');
              this.refresh();
            }
          })
        }).catch(() => {
        });

      },
      online(row) {
        this.R.patchJson('/api/gateway/grayRoute/online/' + row.routeGrayId).then(res => {
          if (res.code === 0) {
            this.$message.success('上线成功');
            this.refresh();
          }
        })
      },
      offline(row) {
        this.R.patchJson('/api/gateway/grayRoute/offline/' + row.routeGrayId).then(res => {
          if (res.code === 0) {
            this.$message.success('下线成功');
            this.refresh();
          }
        })
      },
      formal(row) {
        this.$confirm('转正操作不可逆，确认转为正式服务?', '提示', {
          distinguishCancelAndClose: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.R.patchJson('/api/gateway/grayRoute/formal/' + row.routeGrayId).then(res => {
            if (res.code === 0) {
              this.$message.success('转正成功');
              this.refresh();
            }
          })
        }).catch(() => {
        });

      },
      refreshRoute() {
        this.R.postJson('/api/gateway/grayRoute/refresh').then(res => {
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
