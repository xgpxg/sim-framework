<template>
  <!--<div class="dashboard-container">
    <component :is="currentRole" />
  </div>-->
  <div style="margin: 10% 30% 0 30%">
    <a href="https://gitee.com/xgpxg/sim-framework" target="_blank">
      <img src="https://gitee.com/xgpxg/sim-framework/widgets/widget_card.svg?colors=393222,ebdfc1,fffae5,d8ca9f,393222,a28b40">
    </a>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import adminDashboard from './admin'
import editorDashboard from './editor'

export default {
  name: 'Dashboard',
  components: { adminDashboard, editorDashboard },
  data() {
    return {
      currentRole: 'adminDashboard'
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ])
  },
  created() {
    if (!this.roles.includes('admin')) {
      this.currentRole = 'editorDashboard'
    }
  }
}
</script>

<style>
  .osc_pro_color {color: #393222 !important;}
  .osc_panel_color {background-color: #ebdfc1 !important;}
  .osc_background_color {background-color: #fffae5 !important;}
  .osc_border_color {border-color: #d8ca9f !important;}
  .osc_desc_color {color: #393222 !important;}
  .osc_link_color * {color: #a28b40 !important;}
</style>
