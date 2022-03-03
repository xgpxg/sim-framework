<template>
  <div>
    <el-form ref="form" :model="form" :rules="rules" label-position="left"
             label-width="auto" size="small" :disabled="action==='detail'">
      <el-form-item label="服务名称" prop="name">
        <el-input v-model="form.name" maxlength="20"
                  show-word-limit placeholder="请填写服务名称"></el-input>
      </el-form-item>
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
      <el-form-item label="服务地址" prop="url">
        <el-input v-model="form.url" placeholder="请填写服务地址"></el-input>
      </el-form-item>
      <el-form-item label="访问地址" prop="url">
        <span>{{openUrl}}</span>
      </el-form-item>
    </el-form>

    {{form}}
  </div>
</template>

<script>
  export default {
    name: "add-service",
    data() {
      return {
        form: {
          name: null,
          routeId: null,
          url: '',
          //默认1000 接口服务
          type: '1000'
        },
        /**
         * 路由选择下拉列表
         */
        routes: [],
        pageInfo: {
          pageNum: 1,
          pageSize: 10
        },
      }
    },
    computed: {
      openUrl() {
        return 'http://127.0.0.1:7000'  + '/service/' + this.form.url;
      }
    },
    methods:{
      remoteMethod(query) {
        this.searchQuery = query;
        let reqParam = {...this.pageInfo, filterText: this.searchQuery};
        this.R.get('/api/gateway/route', reqParam).then((res) => {
          let temp = res.data.list;
          temp.forEach(item => item.routeNameDesc = item.routeName + '(' + item.path + ')');
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
    }
  }
</script>

<style scoped>

</style>
