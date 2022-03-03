<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">


        <SearchBar>
          <SearchBarItem :type="'radio'"
                         v-model="searchParam.status"
                         :label="'类型：'"
                         :data="initData.status"></SearchBarItem>
          <SearchBarItem :type="'radio'"
                         v-model="searchParam.userType"
                         :label="'状态：'"
                         :data="initData.merchantTypes"></SearchBarItem>
        </SearchBar>


        <el-table
          :data="tableData"
          border
          style="width: 100%"
          v-loading="loading">
          <el-table-column>
            <el-table-column
              prop="dsId"
              label="ID"
              width="80"
              align="center"
            >
            </el-table-column>
            <el-table-column
              prop="name"
              label="名称"
              align="center">
            </el-table-column>
            <el-table-column
              prop="type"
              label="类型"
              width="100"
              align="center">
            </el-table-column>

          <!--<el-table-column
            prop="sql"
            width="200"
            label="配置模板">
          </el-table-column>-->
          <el-table-column
            prop="statusName"
            width="100"
            label="状态"
            align="center">
          </el-table-column>
          <el-table-column
            prop="statusName"
            width="100"
            label="数据源状态"
            align="center">
            <template slot-scope="{row}">
              <i class="el-icon-success" style="color: #00af00 ;"> 在线</i>
            </template>
          </el-table-column>
            <el-table-column
              prop="fragmentNum"
              width="120"
              label="关联片段数(个)"
              align="center">
            </el-table-column>
          <el-table-column
            prop="createDate"
            width="200"
            label="创建时间"
            align="center">
          </el-table-column>
          <el-table-column
            label="操作"
            width="110"
            align="center">
            <template slot-scope="scope">
              <el-button-group>
                <el-button type="text" icon="detail" size=""
                           class="btn" color="green" style="color: gray"
                           title="详情">
                  <svg-icon class="drag-handler" icon-class="detail"/>
                </el-button>
                <el-button type="text" icon="pen" size=""
                           class="btn" color="green" style="color: gray"
                           title="修改">
                  <svg-icon icon-class="edit"/>
                </el-button>
                <el-button type="text" icon="el-icon-delete" size=""
                           class="btn" style="color: orange"
                           title="删除"></el-button>
              </el-button-group>
            </template>
          </el-table-column>
          <template slot="header" slot-scope="scope">
            <el-col :span="2">
              <el-button type="success" size="mini"
                         @click="addDs">+ 新增
              </el-button>
            </el-col>
            <el-col :span="20">
              <el-input
                v-model="searchParam.filterText"
                size="mini"
                placeholder="数据源ID/数据源名称"
              />
            </el-col>
            <el-col :span="2">
              <el-button size="mini" @click="search">查询</el-button>
            </el-col>
          </template>
          </el-table-column>
        </el-table>
        <el-row style="margin-top:10px;">
          <el-col>
            <el-pagination
              background
              layout="total, prev, pager, next"
              :total="pageInfo.total"
              :page-size="searchParam.pageSize"
              @current-change="currentChange"
              style="text-align: right">
            </el-pagination>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <el-dialog title="新增数据源" :visible.sync="isShow" width="40%"
               :close-on-click-modal="false" v-if="isShow">
      <add-ds ref="add-ds"></add-ds>

      <div slot="footer" class="dialog-footer">
        <el-button @click="testConnection">测试连接</el-button>
        <el-button @click="isShow = false">取 消</el-button>
        <el-button type="primary" @click="submit">确 定
        </el-button>
      </div>
    </el-dialog>

  </div>

</template>

<script>
  import SearchBar from '../../../../components/common/SearchBar/SearchBar.vue'
  import SearchBarItem
    from "../../../../components/common/SearchBar/search-bar-item";
  import AddDs from "./dialog/add-ds";

  export default {
    name: "ds-manage",
    components: {AddDs, SearchBarItem, SearchBar},
    data() {
      return {
        initData: {},
        searchParam: {
          filterText: '',
          pageSize: 10
        },
        pageInfo: {},
        tableData: [],
        isShow: false,
        loading: false
      }
    },
    watch: {
      searchParam: {
        handler(newVal, oldVal) {
          this.search();
        },
        deep: true
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        this.initDict();
        this.search();
      },
      initDict() {

      },
      search() {
        let param = this.U.copy(this.searchParam);
        this.loading = true;
        this.R.get('/api/db-query/dsm', param).then(res => {
          this.tableData = res.data.list;
          this.pageInfo.total = res.data.total;
          this.loading = false;
        })
      },
      addDs() {
        this.isShow = true;
      },
      submit(done) {
        this.$refs['add-ds'].save();
      },
      testConnection() {
        this.$refs['add-ds'].testConnection();
      }
    }
  }
</script>

<style scoped>
  .btn {
    border: none;
    padding: 3px 15px 3px 0;
  }

  .demo-table-expand {
    font-size: 0;
  }

  .demo-table-expand label {
    width: 90px;
    color: #99a9bf;
  }

  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
  }
</style>
