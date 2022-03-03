<template>
  <div>
    <el-form ref="form" :model="form" :rules="rules" label-position="left"
             label-width="auto" size="small" :disabled="action==='detail'">
      <el-form-item label="编码" prop="dicCode">
        <el-input v-model="form.dicCode" maxlength="20"
                  show-word-limit placeholder="请填写编码"></el-input>
      </el-form-item>
      <el-form-item label="名称" prop="dicCodeName">
        <el-input v-model="form.dicCodeName" maxlength="20"
                  show-word-limit placeholder="请填写编码名称"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" maxlength="50"
                  show-word-limit placeholder="请填写编码说明"></el-input>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  export default {
    name: "add-dic-code",
    props: {
      initData: {
        type: Object
      }
    },
    data() {
      return {
        form: {
          dicCode: null,
          dicCodeName: null,
          description: null
        }
      }
    },
    mounted() {
      this.init();
    },
    watch: {
      initData: {
        handler() {
          if (this.initData) {
            this.form = {...this.initData};
          }
        },
        immediate: true
      }
    },
    methods: {
      save() {

      }
    }
  }
</script>

<style scoped>

</style>
