<template>
  <el-form
    :model="registerModel"
    :rules="rules"
    ref="registerForm"
    label-position="top"
  >
    <el-form-item :label="$t('message.home.mobilePhone')" prop="username">
      <div class="form-phone">
        <el-input
          v-model.trim="registerModel.username"
          :placeholder="$t('message.home.pleaseEnterPhoneNumer')"
          maxlength="30"
          @keyup.enter.native="registerEvent"
          class="phone_ipt"
        ></el-input>
        <!-- <el-button type="primary" class="send_code" @click="sendCode()" @submit.native.prevent :disabled='codedis'>{{sendtext}}</el-button> -->
        <VueCountryIntl
          v-model="registerModel.areaCode"
          schema="popover"
          :searchInputPlaceholder="$t('message.home.searchCountry')"
          :noDataText="$t('message.home.noDataFound')"
        >
          <div class="area-code flex-between" slot="reference">
            <span>+{{ registerModel.areaCode }}</span>
            <i class="el-icon-caret-bottom"></i>
          </div>
        </VueCountryIntl>
      </div>
    </el-form-item>
    <!-- <el-form-item :label="$t('message.home.验证码')" prop="verifcode">
      <el-input  v-model.trim="registerModel.verifcode"
          :placeholder="$t('message.home.请输入验证码')"
          oninput="value=value.replace(/[^\d]/g,'')"
          maxlength="6">

      </el-input>
    </el-form-item> -->
    <el-form-item :label="$t('message.home.setPassword')" prop="password">
      <el-input
        v-model="registerModel.password"
        :type="showPassword ? 'password' : 'text'"
        :placeholder="$t('message.home.pleaseSetLoginPassword')"
        @keyup.enter.native="registerEvent"
      >
        <template slot="suffix">
          <img
            class="password-icon"
            :src="showPassword ? imageMap.close : imageMap.open"
            @click="showPassword = !showPassword"
          />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item
      :label="$t('message.home.confirmPassword')"
      prop="confirmPassword"
    >
      <el-input
        :type="showConfirm ? 'password' : 'text'"
        v-model="registerModel.confirmPassword"
        :placeholder="$t('message.home.plaseeENterConfirmPassword')"
      >
        <template slot="suffix">
          <img
            class="password-icon"
            :src="showConfirm ? imageMap.close : imageMap.open"
            @click="showConfirm = !showConfirm"
          />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item>
      <p class="tips">
        {{ $t("message.home.loginTips") }}
        <span @click="goLogin">{{ $t("message.home.loginIn") }}</span>
      </p>
      <el-button
        class="sing-in-btn"
        type="primary"
        :loading="loading"
        @click="registerEvent"
      >
        {{ $t("message.home.signUp") }}
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { mapActions, mapMutations } from "vuex";
import { resgisterApi, getVeriCodeApi } from "@/api";
import VueCountryIntl from "vue-country-intl";
import common from "@/util/common";
import "vue-country-intl/lib/vue-country-intl.css";
import { BASE_AREA_CODE } from "@/common";
export default {
  name: "EsMobileForm",
  components: { VueCountryIntl },
  data() {
    return {
      loading: false,
      agentCode:'',
      codedis: false,
      sendtext: this.$t("message.home.发送"),
      registerModel: {
        username: "",
        password: "",
        confirmPassword: "",
        areaCode: BASE_AREA_CODE,
        verifcode: ""
      },
      showPassword: true,
      showConfirm: true,
      imageMap: {
        open: require("@/assets/image/eye-open.png"),
        close: require("@/assets/image/eye-close.png"),
      },
      rules: {
        username: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.pleaseEnterPhoneNumer")
          ),
          common.ruleUtils.getRule("phone"),
        ],
        password: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.pleaseEnterPassword")
          ),
          common.ruleUtils.getRule("password"),
        ],
        confirmPassword: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.plaseeENterConfirmPassword")
          ),
          common.ruleUtils.getRule("validator", "", {
            validator: (rule, value, callback) => {
              if (value !== this.registerModel.password) {
                callback(new Error(this.$t("message.home.twoPawword")));
                return;
              }
              callback();
            },
          }),
        ],
        verifcode: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.请输入验证码")
          ),
        ],
      },
    };
  },
   activated(){
    this.registerModel.username = '';
    this.registerModel.password = '';
    this.registerModel.confirmPassword = '';
    this.$refs.reigsterForm.validateOnRuleChange = false;
  },
  methods: {
    ...mapActions(["getUserInfo"]),
    ...mapMutations({ setToken: "SETTOKEN" }),
    async sendCode (){
    const rule = /^[0-9]+[0-9]*$/
    if(rule.test(this.registerModel.username)){
        // await getVeriCodeApi({target:this.registerModel.username})
            const params = {
                target:this.registerModel.areaCode+ this.registerModel.username
            }
            getVeriCodeApi(params).then(() => {
                  this.$message({
                    message: this.$t("message.home.已发送，请注意查收"),
                    type: "success",
                  });
                  let num = 61
                  let timer = setInterval(() => {
                  num--;
                  this.sendtext = num + 's';
                  if (num == 0) {
                      clearInterval(timer);
                      this.sendtext = this.$t("message.home.发送");
                      this.codedis = false;   
                  }
                  },1000)

                  this.codedis = true;
            }).catch(err => {
                
            })
    }else{
      
      this.$message({
        message: this.$t("message.home.validatorPhone"),
        type: "error",
    })
    }
  },
    registerEvent() {
      this.$refs.registerForm.validate(async (valid) => {
        if (valid) {
          try {
            if (localStorage.getItem('agentCode')){
              this.agentCode = localStorage.getItem('agentCode');
            }
            this.loading = true;
            const reigsterResult = await resgisterApi({
              // ...this.registerModel,
              // verifcode:this.registerModel.verifcode,
              username: this.registerModel.username,
              password: (this.registerModel.password),
              confirmPassword: (this.registerModel.confirmPassword),
              ...{
                type: 1,
                re_password: (this.registerModel.password),
                username:
                  this.registerModel.areaCode +
                  " " +
                  this.registerModel.username,
                 agentCode:''
              },
            });
            if(reigsterResult.code == '1'){
              this.$message({
              message: this.$t("验证码错误"),
              type: "success",
            });
              return
            }
            this.setToken(reigsterResult.data.token);
            await this.getUserInfo(reigsterResult.data);
            this.$refs.registerForm.resetFields();
            this.$message({
              message: this.$t("message.home.registerSuccess"),
              type: "success",
            });
            this.$router.replace("/");
          } finally {
            this.loading = false;
          }
        }
      });
    },
    goLogin() {
      sessionStorage.setItem("path", "/register");
      this.$router.push("/login");
    },
  },
};
</script>
<style lang="scss" scoped>
html[dir="rtl"]{
  .el-form-item /deep/ .el-form-item__error{
    right: 0;
    left: auto;
  }
  .el-input {
      .el-input__suffix {
          left: 5px;
          right: auto;
          margin-left: 10px;
      }
    }
   .el-input--suffix .el-input__inner {
          padding-left: 15px;
          padding-right: 15px;
      } 
      .rigister-content-form .el-input .el-input__inner {
        max-width: 400px;
      } 
   .rigister-content-form .form-phone .vue-country-popover-container {
    left: 12px;
  }
      .rigister-content-form .form-phone .area-code span{
    width: auto;
    padding-left: 0;
  }
  .rigister-content-form .form-phone .area-code {
    width: auto;
  }
  
}
.form-phone {
  display: flex;
    .send_code{
    flex: 1;
    min-width: 112px;
  }
  .phone_ipt {
    margin-right: 5px;
  }
    
  }
.vue-country-item.selected .selected-text{
  display: none;
}
</style>
