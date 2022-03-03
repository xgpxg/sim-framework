<template>
  <div class="shadow">

    <div class="toolbar">
      <el-button-group>
        <el-button type="" icon="el-icon-video-play" size=""
                   class="btn" color="green" style="color: green"
                   title="运行所选择的"></el-button>
        <el-button type="" icon="el-icon-magic-stick" size=""
                   class="btn" style="color: orange" title="美化SQL"></el-button>

      </el-button-group>
      <el-divider direction="vertical"></el-divider>
      <el-button type="" icon="el-icon-refresh-left" size=""
                 class="btn" style="color: grey" title="撤销"
                 @click="undo"></el-button>
      <el-button type="" icon="el-icon-refresh-right" size=""
                 class="btn" style="color: grey" title="回退"
                 @click="redo"></el-button>
      <el-button type="" icon="el-icon-delete" size=""
                 class="btn" style="color: grey" title="清空"
                 @click="clean"></el-button>
      <el-divider direction="vertical"></el-divider>
      <el-button type="" icon="el-icon-c-scale-to-original" size=""
                 class="btn" style="color: grey"
                 title="选择数据库"></el-button>

    </div>
    <div class="editor">
      <textarea ref="codeEditor" v-model="code"></textarea>
      {{selection}}
    </div>
  </div>
</template>

<script>
  import CodeMirror from "codemirror/lib/codemirror";

  import "codemirror/theme/ambiance.css";
  import "codemirror/lib/codemirror.css";
  import "codemirror/addon/hint/show-hint.css";

  import 'codemirror/theme/idea.css'
  import 'codemirror/mode/javascript/javascript.js'
  import 'codemirror/mode/css/css.js'
  import 'codemirror/mode/xml/xml.js'
  import 'codemirror/mode/swift/swift.js'
  import 'codemirror/mode/vue/vue.js'

  require("codemirror/addon/edit/matchbrackets");
  require("codemirror/addon/selection/active-line");
  require("codemirror/mode/sql/sql");
  require("codemirror/addon/hint/show-hint");
  require("codemirror/addon/hint/sql-hint");

  export default {
    name: "codeMirror",
    props: {
      width: {
        type: String,
        default: '100%'
      },
      height: {
        type: String,
        default: '200px'
      },
      code: {
        type: String,
        default: ''
      }
    },
    data() {
      return {
        //编辑器实例
        editor: null,
        //选择的内容
        selection: null
      }
    },
    mounted() {
      if (!this.editor) {
        this.init();
      }
    },
    watch: {},
    methods: {
      init() {

        let that = this;
        let mime = 'text/x-mysql';
        this.editor = CodeMirror.fromTextArea(this.$refs.codeEditor, {
          mode: mime,
          indentWithTabs: true,
          smartIndent: true,
          lineNumbers: true,
          matchBrackets: true,
          theme: 'idea',
          hintOptions: {
            completeSingle: false,
            tables: {}
          }
        });

        this.editor.on('cursorActivity', function () {
          that.editor.showHint();
          //设置选中
          that.selection = that.editor.getSelection();
        });
        //内容改变触发
        this.editor.on('update', function () {
          that.$emit('update:code', that.editor.getValue());
        });
      },
      /**
       * 运行所选中的
       */
      runSelection() {

      },
      /**
       * 撤销
       */
      undo() {
        this.editor.undo();
      },
      /**
       * 回退
       */
      redo() {
        this.editor.redo();
      },
      /**
       * 清空
       */
      clean() {
        this.editor.setValue('');
      },
      /**
       * 美化sql
       */
      beautiful() {

      }
    }
  }
</script>

<style scoped>
  /deep/ .shadow {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1) !important;
  }

  /deep/ .toolbar {
    border: #f0f0f0 solid 1px;
  }

  .btn {
    border: none;
  }

  /deep/ .editor {
    margin-top: 0;
    border-left: #f0f0f0 solid 1px;
    border-bottom: #f0f0f0 solid 1px;
    border-right: #f0f0f0 solid 1px;

  }
</style>
