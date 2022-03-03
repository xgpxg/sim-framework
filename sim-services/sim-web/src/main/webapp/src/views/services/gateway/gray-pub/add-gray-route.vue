<template>
  <div>
    <div class="title">路由配置</div>
    <el-form ref="form" :model="form" :rules="rules" label-position="left"
             label-width="auto" size="mini" :disabled="action==='detail'">
      <el-form-item label="关联路由" prop="routeId">
        <el-select v-model="form.routeId"
                   filterable
                   remote
                   :remote-method="remoteMethod"
                   @focus="remoteMethod"
                   placeholder="请输入路由名称/服务ID/路径搜索"
                   style="width: 100%"
        >
          <el-option
            v-for="item in routes"
            :key="item.routeId"
            :value="item.routeId"
            :label="item.routeNameDesc"
            :disabled="!item.serviceId">
            <div @click="routeChange(item)">
              {{item.routeNameDesc}}
            </div>
          </el-option>
          <div style="float: right;margin-right:10px;padding-bottom: 10px">
            <el-pagination
              layout=" prev, pager, next,total"
              @current-change="currentChange"
              :current-page="pageInfo.pageNum"
              :page-size="pageInfo.pageSize"
              :total="pageInfo.total">
            </el-pagination>
          </div>
        </el-select>
      </el-form-item>
      <el-form-item label="灰度路由名" prop="grayRouteName">
        <el-input v-model="form.grayRouteName" maxlength="20"
                  show-word-limit placeholder="请填写路由名称"></el-input>
      </el-form-item>
      <el-form-item label="路由规则" prop="path">
        <el-input :disabled="true" v-model="form.path"
                  placeholder="请选择关联路由"></el-input>
      </el-form-item>
      <el-form-item label="路由分组" prop="url">
        <el-select :disabled="true" v-model="form.groupId"
                   placeholder="请选择关联路由">
          <el-option
            v-for="item in initData.groups"
            :key="item.groupId"
            :label="item.groupName"
            :value="item.groupId">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="服务ID" prop="serviceId">
        <el-row>
          <el-col :span="18">
            <el-input :disabled="true" v-model="form.serviceId"
                      placeholder="请选择关联路由"></el-input>
          </el-col>
          <el-col :span="4">
            <el-button size="small" v-if="!routeId" class="ml10">检测服务
            </el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="服务实例" prop="serviceInstances">
        <el-select v-model="form.serviceInstances"
                   placeholder="请选择灰度服务实例"
                   multiple
                   style="width: 100%">
          {{serviceInstances}}
          <el-option
            v-for="item in serviceInstances"
            :key="item['instanceId']"
            :label="item['instanceId']"
            :value="item['instanceId']">
          </el-option>
        </el-select>
      </el-form-item>

      <div class="title">规则配置</div>
      <el-form-item label="灰度规则" prop="type">
        <el-select v-model="form.type" placeholder="请选择">
          <el-option label="按百分比" value="WEIGHT_RANDOM"></el-option>
          <el-option label="Header匹配" value="HEADER"></el-option>
          <el-option label="参数匹配" value="PARAM"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="灰度百分比" prop="weight"
                    v-if="form.type==='WEIGHT_RANDOM'">
        <el-input-number v-model="form.weight" @change="handleChange" :min="0"
                         :max="100" label="灰度流量所占百分比(0-100)"></el-input-number>
      </el-form-item>
      <el-form-item label="主版本号" prop="mainVersion">
        <el-input v-model="form.mainVersion" placeholder="版本号,如1.0"></el-input>
      </el-form-item>
      <el-form-item label="灰度版本号" prop="grayVersion">
        <el-input v-model="form.grayVersion" placeholder="版本号,如2.0"></el-input>
      </el-form-item>

    </el-form>
  </div>
</template>

<script>
  export default {
    name: "add-gray-route",
    props: {
      routeGrayId: {type: Number},
      action: {type: String}
    },
    data() {
      return {
        initData: {
          groups: [
            {groupId: -1, groupName: '默认分组'}
          ]
        },
        /**
         * 路由选择下拉列表
         */
        routes: [],
        /**
         * 服务实例下拉列表
         */
        serviceInstances: [],
        pageInfo: {
          pageNum: 1,
          pageSize: 10
        },
        searchQuery: null,
        form: {
          routeId: null,
          grayRouteName: null,
          path: null,
          serviceId: null,
          serviceInstances: [],
          groupId: null,
          type: null,
          mainVersion: null,
          grayVersion: null,
          weight: 0

        },
        rules: {
          routeName: {required: true, message: '请填写路由名称'},
          groupId: {required: true, message: '请选择分组'},
          mainVersion: {required: true, message: '请填写主版本号'},
          grayVersion: {required: true, message: '请填写灰度版本号'},
          weight: {required: true, message: '请填灰度比例'},
          routeId: {required: true, message: '请选择关联路由'},
          type: {required: true, message: '请选择灰度规则'},
          serviceInstances: {required: true, message: '请选择灰度服务实例'},
        }
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        if (this.routeGrayId) {
          this.loadDetail();
        }
      },
      loadDetail() {
        this.R.get('/api/gateway/grayRoute/' + this.routeGrayId, {}).then(res => {
          if (res.code === 0) {
            this.form = res.data;
            debugger;
          }
        })
      },
      save(callback) {
        this.$refs['form'].validate((valid) => {
          if (valid) {
            let reqParam = this.U.copy(this.form);
            if (!this.routeGrayId) {
              this.R.postJson('/api/gateway/grayRoute', reqParam).then(res => {
                if (res.code === 0) {
                  callback(true)
                }
              })
            } else {
              this.R.patchJson('/api/gateway/grayRoute', reqParam).then(res => {
                if (res.code === 0) {
                  callback(true)
                }
              })
            }
          }
        })
      },
      remoteMethod(query) {
        this.searchQuery = query;
        let reqParam = {...this.pageInfo, filterText: this.searchQuery};
        this.R.get('/api/gateway/route', reqParam).then((res) => {
          let temp = res.data.list;
          temp.forEach(item => item.routeNameDesc = item.routeName + '(服务ID:' + item.serviceId + ',匹配路径:' + item.path + ')');
          this.routes = temp;
          this.pageInfo.total = res.data.total;
        })
      },
      currentChange(currPage) {
        this.pageInfo.pageNum = currPage;
        let reqParam = {...this.pageInfo, filterText: this.searchQuery};
        debugger;
        this.R.get('/api/gateway/route', reqParam).then((res) => {
          this.routes = res.data.list;
          this.pageInfo.total = res.data.total;
        })
      },
      routeChange(val) {
        this.form.grayRouteName = '灰度路由_' + val.routeName;
        this.form.path = val.path;
        this.form.serviceId = val.serviceId;
        this.form.groupId = val.groupId;
        this.form.mainVersion = val.version;

        this.loadServiceInstances();
      },
      loadServiceInstances() {
        this.R.get('/api/gateway/service-info/inst/'+this.form.serviceId, {}).then((res) => {
          this.serviceInstances = res.data.instances;
        })
      }
    }
  }
</script>

<style scoped>
  /deep/ .el-input.is-disabled input {
    color: #4c4c4c;
  }

  /deep/ .el-input.is-disabled .el-input__suffix {
    display: none;
  }

  /deep/ .el-checkbox.is-disabled .el-checkbox__label {
    color: #4c4c4c;
  }
</style>
