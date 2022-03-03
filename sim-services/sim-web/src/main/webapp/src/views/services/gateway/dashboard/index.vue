<template>
  <dv-full-screen-container
    style="background-color: rgb(24, 26, 33);width:100%;display: flex">
    <!--轮播表-->
    <dv-border-box-11 title="调用统计"
                      style="width:55%;height:40%;padding: 1.5% 0 0 0 ">
      <dv-scroll-board :config="option3"
                       style="width:90%;height:80%;margin: 4% 5% 0 5%"></dv-scroll-board>
    </dv-border-box-11>
    <dv-border-box-11 title="调用统计"
                      style="width:75%;height:40%;padding: 1.5% 0 0 0 ">
      <dv-border-box-10 style="width: 25%;height: 20%">
      <dv-digital-flop :config="option4"
                         style="width:100%;height:100%; "></dv-digital-flop>
      </dv-border-box-10>

      <dv-border-box-10 style="width: 25%;height: 20%">
        <dv-digital-flop :config="option4"
                         style="width:100%;height:100%; "></dv-digital-flop>
      </dv-border-box-10>

    </dv-border-box-11>


    <dv-border-box-11 title="dv-border-box-11"
                      style="width:30%;height:40%;padding: 1.5% 0 0 0 "
                      class="box">
      <dv-capsule-chart :config="option2"
                        style="width:90%;height:90%;padding: 1.5% 0 0 0 "
                        class="dv"
      ></dv-capsule-chart>
    </dv-border-box-11>


  </dv-full-screen-container>

</template>

<script>
  import Charts from '@jiaminghi/charts'

  export default {
    name: "dashboard",
    data() {
      return {
        option1: {
          title: {
            text: '周销售额趋势'
          },
          xAxis: {
            name: '第一周',
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
          },
          yAxis: {
            name: '销售额',
            data: 'value'
          },
          series: [
            {
              data: [1200, 2230, 1900, 2100, 3500, 4200, 3985],
              type: 'line',
              smooth: true
            }
          ]
        },
        option2: {

          data: [{
            name: '南阳',
            value: 167
          },
            {
              name: '周口',
              value: 67
            },
            {
              name: '漯河',
              value: 123
            },
            {
              name: '郑州',
              value: 55
            },
            {
              name: '西峡',
              value: 98
            }],
        },
        option3: {
          header: ['列1', '列2', '列3'],
          data: [
            ['行1列1', '行1列2', '行1列3'],
            ['行2列1', '行2列2', '行2列3'],
            ['行3列1', '行3列2', '行3列3'],
            ['行4列1', '行4列2', '行4列3'],
            ['行5列1', '行5列2', '行5列3'],
            ['行6列1', '行6列2', '行6列3'],
            ['行7列1', '行7列2', '行7列3'],
            ['行8列1', '行8列2', '行8列3'],
            ['行9列1', '行9列2', '行9列3'],
            ['行10列1', '行10列2', '行10列3']
          ],
          index: true,
          columnWidth: [50],
          align: ['center'],
          odderBGC: "rgba(255,255,255, .8)",
          evenRowBGC: "rgba(255,255,255, .3)",
          headerBGC: "rgba(0,0,0, 0.01 )",
        },
        option4: {
          number: [900],
          content: '{nt}个'
        }
      }
    },
    mounted() {
      this.init();
    },
    methods: {
      init() {
        this.initChart();
      },
      initChart() {
        let container = this.$refs['container'];
        debugger;
        let myChart = new Charts(container)
        myChart.setOption(this.option1)
      }
    }
  }
</script>

<style scoped>
  body {
    margin: 0;
    padding: 0;
  }

  .box {
    padding: 1.5% 0 0 0;

  }

  .dv {

    margin: 7% 0 0 5%
  }
</style>
