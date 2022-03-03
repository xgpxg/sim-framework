<template>
  <div>
    <el-form ref="form" :model="form" :rules="rules" label-position="left"
             label-width="auto" size="small" :disabled="action==='detail'">
      <el-form-item label="选择路由" prop="routeId">
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
            :label="item.routeNameDesc">
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
      <el-form-item label="时间窗口" prop="refreshInterval">
        <el-input v-model="form.refreshInterval" placeholder="请填写"></el-input>
      </el-form-item>
      <el-form-item label="最大并发数" prop="limit">
        <el-input v-model="form.limit" placeholder="请填写"></el-input>
      </el-form-item>
      <el-form-item label="最大时间" prop="quota">
        <el-input v-model="form.quota" placeholder="请填写"></el-input>
      </el-form-item>
      <el-form-item label="匹配方式" prop="matchType">
        <el-checkbox-group v-model="form.matchType">
          <el-checkbox label="ORIGIN">ORIGIN（基于用户IP）</el-checkbox>
          <!--<el-checkbox label="USER">USER</el-checkbox>-->
          <el-checkbox label="URL">URL（基于请求URL）</el-checkbox>
         <!-- <el-checkbox label="ROLE" >ROLE</el-checkbox>-->
          <el-checkbox label="HTTPMETHOD" >HTTPMETHOD（基于请求方法）</el-checkbox>
          <!--<el-checkbox label="URL_PATTERN" >URL_PATTERN</el-checkbox>-->
        </el-checkbox-group>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  export default {
    name: "add-limit",
    data() {
      return {
        form: {
          refreshInterval: null,
          limit: null,
          quota: null,
          matchType:[]
        },
        /**
         * 路由选择下拉列表
         */
        routes: [],
        pageInfo: {
          pageNum: 1,
        },
        searchQuery: null,
        rules: {
          routeId: {required: true, message: '请选择路由'},
          refreshInterval: {required: true, message: '请填写时间窗口，单位：秒'},
          limit: {required: true, message: '请填写最大并发数'},
          quota: {required: true, message: '请填写最大时间'},
          matchType: {required: true, message: '请填选择匹配方式'},
        }
      }
    },
    methods: {
      remoteMethod(query) {
        this.searchQuery = query;
        let reqParam = {...this.pageInfo, filterText: this.searchQuery};
        this.R.get('/api/gateway/route', reqParam).then((res) => {
          let temp = res.data.list;
          temp.forEach(item => item.routeNameDesc = item.routeName + '(匹配路径:' + item.path + ')');
          this.routes = temp;
          this.pageInfo.total = res.data.total;
        })
      },
      save(callback){
        this.$refs['form'].validate((valid) => {
          if (valid) {
            let reqParam = this.U.copy(this.form);
            reqParam.matchType = this.form.matchType.join(',');
            this.R.postJson('/api/gateway/rateLimit',reqParam).then(res=>{
              alert(JSON.stringify(res))
            })
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
