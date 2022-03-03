<template>
  <div class="">
    <VueTerminal
      :intro="intro"
      console-sign=">"
      :allow-arbitrary="true"
      height="600px"
      @command="onCliCommand"
    ></VueTerminal>
    <!--
        <vue-terminal :task-list="taskList" :command-list="commandList" :showHeader="true" :greeting="'redis命令行工具'" :title="'redis@localhost:6379'"/>
    -->
  </div>
</template>

<script>
import VueTerminal from '@/components/VueTerminal/VueTerminal'

/*import VueTerminal from 'vue-terminal';*/

export default {
  name: "command-line",
  components: {VueTerminal},
  props:{
    host:String,
    port:Number
  },
  data() {
    return {
      editableTabsValue: '2',
      editableTabs: [{
        title: 'Tab 1',
        name: '1',
        content: 'Tab 1 content'
      }, {
        title: 'Tab 2',
        name: '2',
        content: ''
      }],
      tabIndex: 2,

      taskList: {
        // your tasks
      },
      commandList: commandList,
      intro: "Welcome to redis cli"
    }
  },
  mounted() {

  },
  methods: {
    addTab(targetName) {
      let newTabName = ++this.tabIndex + '';
      this.editableTabs.push({
        title: 'New Tab',
        name: newTabName,
        content: 'New Tab content'
      });
      this.editableTabsValue = newTabName;
    },
    removeTab(targetName) {
      let tabs = this.editableTabs;
      let activeName = this.editableTabsValue;
      if (activeName === targetName) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            let nextTab = tabs[index + 1] || tabs[index - 1];
            if (nextTab) {
              activeName = nextTab.name;
            }
          }
        });
      }

      this.editableTabsValue = activeName;
      this.editableTabs = tabs.filter(tab => tab.name !== targetName);
    },
    onCliCommand(data, resolve, reject) {
      // typed command is available in data.text
      // don't forget to resolve or reject the Promise
      let that = this;
      that.R.postJson('/api/cache-service/command', {command: data.text}).then(res => {
        var result = res.data;
        if (result.error) {
          reject(result.error);
        } else {
          resolve(result);
        }
      });
    },
    ping() {
      let that = this;
      that.R.postJson('/api/cache-service/command', {command: 'ping'}).then(res => {

      });
    }
  }
}
</script>

<style scoped>
.page {
  padding: 20px;
}
</style>
