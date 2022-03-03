<!--短信验证码发送组件-->
<template>
  <el-button size="small" @click="getSmsCode"
             :disabled="disabled && innerDisabled">{{msg}}
  </el-button>
</template>

<script>
  export default {
    name: "SmsCode",
    props: {
      phone: {
        type: String
      },
      disabled: {
        type: Boolean
      }
    },
    data() {
      return {
        innerDisabled: false,
        msg: '获取验证码'
      }
    },
    methods: {
      getSmsCode() {
        let that = this;
        if (!(/^1[3456789]\d{9}$/.test(this.phone))) {
          this.$message({
            type: 'error',
            message: '手机号格式不正确'
          });
          return false;
        }
        let reqParam = {
          phone: that.phone,
          isNeedVerifyCode: false
        };
        that.innerDisabled = true;
        that.R.postJson('/api/commonApi/sendSms', reqParam).then(res => {
          if (res.code === 0) {
            let oldDateObj = new Date();
            let newDateObj = new Date();
            newDateObj.setTime(oldDateObj.getTime() + (2 * 60 * 1000));
            let timer = setInterval(() => {
              let timeInterval = that.U.getTimeInterval(new Date(), newDateObj);
              that.msg = `${timeInterval.minutes}分${timeInterval.seconds}秒后重发`;
              if (timeInterval.allSeconds <= 0) {
                that.msg = '获取校验码';
                that.innerDisabled = false;
                clearInterval(timer);
              }
            }, 1000);
          } else {
            that.innerDisabled = false;
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
