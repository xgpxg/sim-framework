<template>
  <div>
    <el-row>
      <el-col :span="6">
        <el-form ref="form" :model="form" label-width="80px" size="large"
                 :label-position="'left'">
          <el-form-item label="文档">
            <el-input v-model="form.name" placeholder="搜索文档" suffix-icon="el-icon-search"></el-input>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="18">
        <el-row type="flex" justify="end">
          <el-col :span="3" justify="end">
            <el-dropdown>
              <el-button type="success" class="pull-right" size="small">
                + 新建<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>新建文件夹</el-dropdown-item>
                <el-dropdown-item>新建文档</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-breadcrumb separator-class="el-icon-arrow-right" class="catalog-bar">
      <el-breadcrumb-item :to="{ path: '/' }">
        <svg-icon :icon-class="'home'" class="icon"/>
        所有文档
      </el-breadcrumb-item>
      <el-breadcrumb-item>
        <svg-icon :icon-class="'dir2'" class="icon"/>
        项目文档
      </el-breadcrumb-item>
      <el-breadcrumb-item>
        <svg-icon :icon-class="'dir2'" class="icon"/>
        测试
      </el-breadcrumb-item>
    </el-breadcrumb>


    <el-table
      :data="tableData"
      style="width: 100%">
      <el-table-column
        prop="docName"
        label="名称"
      >
        <template slot-scope="scope">
          <span style="margin-right: 10px"><svg-icon :icon-class="'dir2'"
                                                     class="icon"/> {{ scope.row.docName }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="docTypeName"
        label="类型"
        width="80">
      </el-table-column>
      <el-table-column
        prop="updateDate"
        label="更新时间"
        width="180">
      </el-table-column>
      <el-table-column
        prop="createUserName"
        label="创建人"
        width="80">
      </el-table-column>
      <el-table-column
        prop="option"
        width="50"
        label="">
        <template slot-scope="scope">
          <el-dropdown trigger="click">
            <el-button type="text" size="mini">
              ···
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>重命名</el-dropdown-item>
              <el-dropdown-item>移动</el-dropdown-item>
              <el-dropdown-item>删除</el-dropdown-item>
              <el-dropdown-item>权限</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
  export default {
    name: "catalog-main",
    data() {
      return {
        form: {},
        tableData: [{
          docName: 'test',
          docTypeName: '文件夹',
          updateDate: '2021-03-05 22:01:15',
          createUserName: '管理员'
        },
          {
            docName: 'test2',
            docTypeName: '文件夹',
            updateDate: '2021-03-05 22:01:15',
            createUserName: '管理员'
          }],
      }
    }
  }
</script>

<style scoped>
  .pull-right {
    text-align: right;
  }

  /deep/ .el-form-item__label {
    font-size: 1.3rem;

  }

  .catalog-bar {
    margin: 10px 0 10px 0;
    font-size: 1rem;
  }
</style>
