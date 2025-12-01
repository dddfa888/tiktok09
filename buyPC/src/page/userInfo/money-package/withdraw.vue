<template>
  <div class="info_page_container" style="width: 480px">
    <div class="page-title" style="cursor: pointer; margin-bottom: 20px" @click="goBack">
      <i class="el-icon-arrow-left"></i>
      {{ $t("message.home.myPurse") }}/{{ $t("message.home.withdraw") }}
    </div>

    <el-form v-if="itemname !== 'FamilyShop'">
      <el-form-item :label="$t('message.home.提现方式')">
        <el-select style="width: 100%" v-model="rachargeTypeV" @change="changeType">
          <el-option v-for="item in rachargeType" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <el-form ref="withdrawForm" label-position="top" :model="form" :rules="rules" v-loading="httpLoading"
      v-if="rachargeTypeV === 'Virtual'">
      <div>
        <el-form-item :label="$t('message.home.coinBlock')">
          <el-select v-model="coinType" :placeholder="$t('message.home.请选择')" @change="changeCoinType(coinType)">
            <el-option v-for="item in Object.keys(this.groupBlockAddress)" :key="item" :label="item"
              :value="item"></el-option>
          </el-select>
        </el-form-item>
      </div>
      <div class="network-select" v-if="this.form.network">
        <el-form-item :label="$t('message.home.blockchainNetwork' /**区块链网络 */)" prop="network">
          <el-radio-group v-model="form.network">
            <el-radio-button v-for="item in coinInfo" :key="item.blockchain_name" :label="item.blockchain_name"
              :disabled="swithButton"></el-radio-button>
          </el-radio-group>
        </el-form-item>
      </div>

      <el-form-item :label="$t('message.home.withdrawAddress' /**提现地址 */)" class="wallet-address" prop="address">
        <el-input v-model="form.address" :placeholder="$t('message.home.withdrawInputTips' /** 请输入提币地址*/)
          " :disabled="swithButton" />
        <span :class="{
          'copy-btn': true,
          disabled: isDisabled,
        }" @click="copy">
          {{ $t("message.home.copy" /**复制 */) }}
        </span>
      </el-form-item>

      <el-form-item :label="$t('message.home.quantity')" prop="quantity">
        <el-input v-model="form.quantity" maxlength="12" onkeyup="if(isNaN(value))execCommand('undo')"
          onafterpaste="if(isNaN(value))execCommand('undo')" :disabled="money < 1"
          :placeholder="$t('message.home.enterTips')" />
      </el-form-item>
      <el-form-item>
        <div style="color:#56B069">
          {{ $t('message.home.当前余额') }}: {{ numberFormat(money) }} USDT ≈ {{ numberFormat($big(money).div(fee)) }}
          {{ coinType }}
        </div>
      </el-form-item>
      <el-form-item>
        <div class="withdraw-tips flex-between">
          <span>
            {{ $t("message.home.ActualLedger" /**实际账户 */) }}:
            {{ numberFormat(form.arrival) }} {{ coinType }}
          </span>
          <span>
            {{ $t("message.home.handlingFee" /**手续费 */) }}:
            {{ numberFormat(getRealWithdrawFee) }}%
          </span>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button style="width: 100%" type="primary" @click="withdraw">
          {{ $t("message.home.btnSure" /**确认 */) }}
        </el-button>
      </el-form-item>
    </el-form>
    <el-form ref="withdrawBankForm" :model="formBank"
      v-if="rachargeTypeV === 'bank' && itemname !== 'FamilyShop' && itemname !== 'SM-wholesale shop'"
      :rules="bankRules">
      <el-form-item :label="$t('message.home.actualName')" prop="bankUserName">
        <el-input v-model="formBank.bankUserName" @keypress.native="onKeyPress"
          :placeholder="$t('message.home.yourName')" />
      </el-form-item>
      <el-form-item :label="$t('message.home.开户行')" prop="bankName">
        <el-input v-model="formBank.bankName" @keypress.native="onKeyPress"
          :placeholder="$t('message.home.请输入开户行名称')" />
      </el-form-item>
      <el-form-item :label="$t('message.home.银行卡号')" prop="bankCardNo">
        <el-input v-model="formBank.bankCardNo" :placeholder="$t('message.home.请输入银行卡号')"
          onkeyup="value=value.replace(/[^\w\.\/]/ig,'')" />
      </el-form-item>
      <el-form-item :label="$t('message.home.quantity')" prop="bankQuantity">
        <el-input v-model="formBank.bankQuantity" :placeholder="$t('message.home.enterTips')"
          onkeyup="if(isNaN(value))execCommand('undo')" onafterpaste="if(isNaN(value))execCommand('undo')" />
      </el-form-item>
      <!-- <el-input > -->

      <!-- <el-input name=txt1 onchange="if(/\D/.test(this.value)){alert('只能输入数字');this.value='';}"> -->
      <el-form-item>
        <div class="withdraw-tips flex-between">
          <span>
            {{ $t("message.home.ActualLedger" /**实际账户 */) }}:
            {{ numberFormat(formBank.arrival) }}
          </span>
          <span>
            {{ $t("message.home.handlingFee" /**手续费 */) }}:
            {{ numberFormat(getRealWithdrawFee) }}%
          </span>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button style="width: 100%" type="primary" @click="Bankwithdraw">
          {{ $t("message.home.btnSure" /**确认 */) }}
        </el-button>
      </el-form-item>
    </el-form>
    <!-- 支付宝提现表单 -->
    <el-form ref="withdrawZfbForm" :model="formZfb"
      v-if="rachargeTypeV === 'zfb' && itemname !== 'FamilyShop' && itemname !== 'SM-wholesale shop'" :rules="zfbRules">
      <el-form-item :label="$t('message.home.actualName')" prop="bankUserName">
        <el-input v-model="formZfb.userName" @keypress.native="onKeyPress" :placeholder="$t('message.home.yourName')" />
      </el-form-item>
      <el-form-item :label="$t('message.home.支付宝账号')" prop="bankCardNo">
        <el-input v-model="formZfb.bankCardNo" :placeholder="$t('message.home.pleaseEnterAccount')" />
      </el-form-item>
      <!-- 金额 -->
      <el-form-item :label="$t('message.home.quantity')" prop="quantity">
        <el-input v-model="formZfb.quantity" maxlength="12" onkeyup="if(isNaN(value))execCommand('undo')"
          onafterpaste="if(isNaN(value))execCommand('undo')" :disabled="money < 1"
          :placeholder="$t('message.home.enterTips')" />
      </el-form-item>
      <el-form-item>
        <div style="color:#56B069">
          {{ $t('message.home.当前余额') }}: {{ numberFormat(money) }} USDT ≈ {{ numberFormat($big(money).div(fee)) }} USDT
        </div>
      </el-form-item>
      <el-form-item>
        <div class="withdraw-tips flex-between">
          <span>
            {{ $t("message.home.ActualLedger" /**实际账户 */) }}:
            {{ numberFormat(formZfb.arrival) }} USDT
          </span>
          <span>
            {{ $t("message.home.handlingFee" /**手续费 */) }}:
            {{ numberFormat(getRealWithdrawFee) }}%
          </span>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button style="width: 100%" type="primary" @click="Zfbwithdraw">
          {{ $t("message.home.btnSure" /**确认 */) }}
        </el-button>
      </el-form-item>
    </el-form>
    <!-- 微信提现表单 -->
    <el-form ref="withdrawWxForm" :model="formWx"
      v-if="rachargeTypeV === 'wx' && itemname !== 'FamilyShop' && itemname !== 'SM-wholesale shop'" :rules="wxRules">
      <!-- <el-form-item :label="$t('message.home.actualName')" prop="bankUserName">
        <el-input
          v-model="formWx.userName"
          @keypress.native="onKeyPress"
          :placeholder="$t('message.home.yourName')"
        />
      </el-form-item> -->
      <el-form-item :label="$t('message.home.微信绑定手机号')" prop="bankCardNo">
        <el-input v-model="formWx.bankCardNo" :placeholder="$t('message.home.pleaseEnterAccount')" />
      </el-form-item>


      <el-form-item style="align-items: start;display: flex;flex-direction: column;" :label="$t('message.home.上传微信收款码（需保存收款码到相册)')" prop="qdCode">
        <el-upload class="avatar-uploader" action="#" list-type="picture-card" :limit="1"
          :on-preview="handlePictureCardPreview" :on-remove="handleRemove" :http-request="customUploadRequest">
          <i class="el-icon-plus"></i>
        </el-upload>
        <el-dialog :visible.sync="dialogVisibleWx">
          <img width="100%" :src="dialogImageUrl" alt="">
        </el-dialog>
      </el-form-item>
      <!-- 金额 -->
      <el-form-item :label="$t('message.home.quantity')" prop="quantity">
        <el-input v-model="formWx.quantity" maxlength="12" onkeyup="if(isNaN(value))execCommand('undo')"
          onafterpaste="if(isNaN(value))execCommand('undo')" :disabled="money < 1"
          :placeholder="$t('message.home.enterTips')" />
      </el-form-item>
      <el-form-item>
        <div style="color:#56B069">
          {{ $t('message.home.当前余额') }}: {{ numberFormat(money) }} USDT ≈ {{ numberFormat($big(money).div(fee)) }} USDT
        </div>
      </el-form-item>
      <el-form-item>
        <div class="withdraw-tips flex-between">
          <span>
            {{ $t("message.home.ActualLedger" /**实际账户 */) }}:
            {{ numberFormat(formWx.arrival) }} USDT
          </span>
          <span>
            {{ $t("message.home.handlingFee" /**手续费 */) }}:
            {{ numberFormat(getRealWithdrawFee) }}%
          </span>
          <span>{{ $t("message.home.微信") }} {{ $t("message.home.提现请联系客服") }}</span>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button style="width: 100%" type="primary" @click="Wxwithdraw">
          {{ $t("message.home.btnSure" /**确认 */) }}
        </el-button>
      </el-form-item>
    </el-form>

    <el-dialog :title="$t('message.home.提款地址绑定' /**提现地址 */)" :visible.sync="dialogVisible" width="30%"
      :show-close="false" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="with_container">
        <el-form label-position="top" label-width="80px">
          <el-form-item :label="$t('message.home.coinBlock')">
            <el-select v-model="optionsValue" :placeholder="$t('message.home.请选择')" @change="changeCoin(optionsValue)">
              <el-option v-for="item in options" :key="item.coin" :label="item.coin" :value="item.coin">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('message.home.blockchainNetwork' /**区块链网络 */)">
            <el-select v-model="form.network" :placeholder="$t('message.home.请选择')">
              <el-option v-for="item in coinInfo" :key="item.blockchain_name" :label="item.blockchain_name"
                :value="item.blockchain_name">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('message.home.withdrawAddress')" prop="address">
            <el-input v-model="form.address" :placeholder="$t('message.home.withdrawInputTips' /** 请输入提币地址*/)
              "></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div style="color:#FF3E3E">{{ $t('message.home.仅能绑定一个收款地址!') }}</div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="pushWithdrawAddress">{{ $t('message.home.btnSure') }}</el-button>
      </span>
    </el-dialog>

    <EsPayModal v-model="payModalShow" :payCallback="payCallback" :payLoading="btnLoading" />
    <!-- <EsWidthdrawRecordDialog v-model="withdrawRecordVisible" /> -->
    <EsOnlineServiceView v-model="showOnlieService" />
  </div>
  <!-- </el-dialog> -->
</template>

<script>
import { mapGetters, mapActions } from "vuex";
import EsPayModal from "@/page/settlementPage/payModal";
// import EsWidthdrawRecordDialog from './widthdrawRecordDialog'
import common from "@/util/common";
import { copyTextToClipboard, emptyStr, numberFormat } from "@/util";
import { accTimes, accMinus } from "@/util/math";
import { Widerrufsfrist, BindWithdrawAddress, WithdrawGetFee } from "@/api/withdraw";
import { groupBy } from "lodash";
import axios from "axios";
import URL from "@/config/index";
import { getVeriCodeApi } from "@/api/common";
export default {
  name: "withdraw-dialog",
  components: { EsPayModal },
  props: {
    value: {
      type: Boolean,
      default: false,
    },
    currentCoin: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      dialogImageUrl: '',
      dialogVisibleWx: false,
      // itemname: env.process
      itemname: process.env.VUE_APP_ITEM_NAME,
      form: {
        network: "",
        address: "",
        quantity: "",
        arrival: "",
        coin: "",
      },
      formBank: {
        bankName: '',
        bankUserName: '',
        bankCardNo: '',
        quantity: '',
        bankQuantity: '',
      },
      formZfb: {
        userName: '',
        bankName: '',
        bankUserName: '',
        bankCardNo: '',
        quantity: '',
        arrival: "",
        bankQuantity: '',
      },
      formWx: {
        userName: '',
        // bankName: '',
        bankUserName: '',
        bankCardNo: '',
        quantity: '',
        arrival: "",
        bankQuantity: '',
        qdCode: '',
      },
      options: [

      ],
      rachargeType: [
        
        {
          label: this.$t("message.home.支付宝"),
          value: "zfb",
        },
        {
          label: this.$t("message.home.微信"),
          value: "wx",
        },
        {
          label: this.$t("message.home.银行卡"),
          value: "bank",
        },
        {
          label: this.$t("message.home.加密货币"),
          value: "Virtual",
        },
        
      ],
      rachargeTypeV: "zfb",
      optionsValue: "USDT",
      bankRules: {
        bankName: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.请输入开户行名称")
          ),
        ],
        bankUserName: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.yourName")
          ),
        ],
        bankCardNo: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.请输入银行卡号")
          ),
        ],
        bankQuantity: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.pleaseEnterQuantity")
          ),
          common.ruleUtils.getRule("decimalExceedZero"),
          common.ruleUtils.getRule("validator", "", {
            validator: (rule, value, callback) => {
              const max = this.money ? this.money : 10e8;
              // if (Number(value) > max) {
              //   callback(this.$t("message.home.validatorMaxNumber"));
              // }
              callback();
            },
          }),
        ],
      },
      zfbRules: {
        bankName: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.请输入开户行名称")
          ),
        ],
        userName: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.yourName")
          ),
        ],
        bankCardNo: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.pleaseEnterAccount")
          ),
        ],
        quantity: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.pleaseEnterQuantity")
          ),
          common.ruleUtils.getRule("decimalSix"),
          common.ruleUtils.getRule("validator", "", {
            validator: (rule, value, callback) => {
              const max = (this.$big(this.money).div(this.fee)) ? (this.$big(this.money).div(this.fee)) : 10e8;
              // if (Number(value) > max) {
              //   callback(this.$t("message.home.validatorMaxNumber"));
              // }
              callback();
            },
          }),
        ],
      },
      wxRules: {
        bankName: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.请输入开户行名称")
          ),
        ],
        userName: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.yourName")
          ),
        ],
        bankCardNo: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.pleaseEnterAccount")
          ),
        ],
        qdCode: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.请上传微信收款码")
          ),
        ],
        quantity: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.pleaseEnterQuantity")
          ),
          common.ruleUtils.getRule("decimalSix"),
          common.ruleUtils.getRule("validator", "", {
            validator: (rule, value, callback) => {
              const max = (this.$big(this.money).div(this.fee)) ? (this.$big(this.money).div(this.fee)) : 10e8;
              // if (Number(value) > max) {
              //   callback(this.$t("message.home.validatorMaxNumber"));
              // }
              callback();
            },
          }),
        ],
      },
      rules: {
        address: [
          common.ruleUtils.getRule("cionAddress"),
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.pleaseEnterAddress")
          ),
        ],
        quantity: [
          common.ruleUtils.getRule(
            "required",
            this.$t("message.home.pleaseEnterQuantity")
          ),
          common.ruleUtils.getRule("decimalSix"),
          common.ruleUtils.getRule("validator", "", {
            validator: (rule, value, callback) => {
              const max = (this.$big(this.money).div(this.fee)) ? (this.$big(this.money).div(this.fee)) : 10e8;
              // if (Number(value) > max) {
              //   callback(this.$t("message.home.validatorMaxNumber"));
              // }
              callback();
            },
          }),
        ],
      },
      currentSelectBlock: {},
      coinInfo: [],
      payModalShow: false,
      withdrawRecordVisible: false,
      showOnlieService: false,
      count: 0,
      money: 0,
      dialogVisible: false,
      swithButton: false,
      coinType: 'zfb',
      groupBlockAddress: {},
      fee: 1,
      coinArr: [],
      withdrawFee: 0,
    };
  },
  async mounted() {
    this.money = localStorage.getItem("userBalance");

    await Promise.all([
      this.requestWithdrawFee(),
      this.requestSessionToken(),
      this.requestChannelAddress(),
    ]);
    this.$nextTick(() => {
      console.log("this.rechargeAddress ->", this.rechargeAddress);
      let newArr = []
      let arrId = []
      this.rechargeAddress.forEach((item) => {
        // console.log('item ->', item);
        if (arrId.indexOf(item.coin) === -1) {
          arrId.push(item.coin)
          newArr.push(item)
        }
      })
      this.groupBlockAddress = groupBy(this.rechargeAddress, "coin");
      this.coinArr = Object.keys(this.groupBlockAddress)
      console.log('this.coinArr ->', this.coinArr);
      this.coinArr.includes('USDT') && (this.coinType = 'USDT')
      // console.log('this.options ->', this.options);
      // if

      this.coinType = this.coinArr[0]
      const data = this.rechargeAddress.filter((item) => item.coin === this.coinType);
      if (data && data.length) {
        console.log('data[0] ->', data[0]);
        this.form.network = data[0].blockchain_name;
        this.getFee(data[0].coin)
        this.fee = data[0].fee
      }
      this.coinInfo = [...data];
    });
    this.getwithdraw();
    this.groupAddress()
    // 提现只支持USDT
  },
  watch: {
    "form.network": {
      handler(newValue, oldValue) {
        if (newValue !== oldValue) {
          const data = this.rechargeAddress.find(
            (item) => item.blockchain_name === newValue
          );
          if (data) {
            this.form.address = "";
            this.form.quantity = "";
            this.form.arrival = 0;
            this.currentSelectBlock = { ...data };
          }
        }
      },
      immediate: true,
    },
    "form.quantity": {
      handler(newValue, oldValue) {
        if (newValue !== oldValue) {
          const amount = accTimes(
            newValue,
            accMinus(1, this.withdrawFee ?? 0)
          );
          this.form.arrival = isNaN(amount) ? "" : amount;
        }
      },
    },
    "formBank.bankQuantity": {
      handler(newValue, oldValue) {
        if (newValue !== oldValue) {
          const amount = accTimes(
            newValue,
            accMinus(1, this.withdrawFee ?? 0)
          );
          this.formBank.arrival = isNaN(amount) ? "" : amount;
        }
      },
    },
    "formZfb.quantity": {
      handler(newValue, oldValue) {
        console.log("进来了")
        if (newValue !== oldValue) {
          const amount = accTimes(
            newValue,
            accMinus(1, this.withdrawFee ?? 0)
          );
          this.formZfb.arrival = isNaN(amount) ? "" : amount;
        }
      },
    },
    "formWx.quantity": {
      handler(newValue, oldValue) {
        console.log("进来了")
        if (newValue !== oldValue) {
          const amount = accTimes(
            newValue,
            accMinus(1, this.withdrawFee ?? 0)
          );
          this.formWx.arrival = isNaN(amount) ? "" : amount;
        }
      },
    },
  },
  computed: {
    ...mapGetters("user", ["userBalance"]),
    ...mapGetters("withdraw", [
      "sessionToken",
      "httpLoading",
      "btnLoading",
      // "withdrawFee",
    ]),
    rechargeAddress() {
      return this.$store.state.recharge.rechargeAddress;
    },
    getRealWithdrawFee() {
      console.log('this.withdrawFee ->', this.withdrawFee);
      return this.withdrawFee ? this.withdrawFee * 100 : 0;
    },
    isDisabled() {
      return emptyStr(this.form.address);
    },
  },
  methods: {
    // 自定义上传逻辑
    customUploadRequest({ file, onSuccess, onError }) {
      var t = this;
      const formData = new FormData();
      formData.append("file", file);
      formData.append("moduleName", "123");
      console.log('file.file', file);
      
      getVeriCodeApi(formData).then(res => {
        // this.$message.success(this.$t('message.home.uploadSuccess'));
       
        this.formWx.qdCode = res.data
      }).catch(function (err) {
        console.log(err)
        this.$message.error(this.$t('message.home.uploadFail'));
      })
    },
    // 上传图片
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisibleWx = true;
    },
    async getFee(e) {
      const res = await WithdrawGetFee(e);
      // console.log('res ->', res);
      this.withdrawFee = res.data.withdraw_fee;
      // console.log('this.fee ->', this.fee);
    },
    changeCoinType(e) {
      console.log('e ->', e);
      const data = this.rechargeAddress.filter((item) => item.coin === e);
      if (data && data.length) {
        this.form.network = data[0].blockchain_name;
        this.fee = data[0].fee
        this.getFee(data[0].coin)
      }
      this.coinInfo = [...data]
    },
    groupAddress() {
      // if(this.itemname == 'FamilyShop'){
      // console.log('this.groupAddress ->', Object.keys(this.groupBlockAddress));
      this.coinType = Object.keys(this.groupBlockAddress)[0];
      // }
    },
    async getwithdraw() {
      let res = await Widerrufsfrist();
      if (res.data.openWithdrawAddressBinding) {
        if (
          !res.data.existWithdrawAddress &&
          !res.data.chainName
        ) {
          this.swithButton = true;
          this.dialogVisible = true;
        } else {
          this.form.address = res.data.existWithdrawAddress;
          // this.address = res.data.existWithdrawAddress
          this.coinType = res.data.coinType
          this.form.network = res.data.chainName;
          this.swithButton = true;
        }
      } else {
        this.swithButton = false;
      }
    },

    changeType(v) {

      // this.form.network = ''
      if (v == 'bank' && this.itemname == 'SM-wholesale shop') {
        this.showOnlieService = true
      }
      if (v == 'bank') {

        this.getFee('bank')
        this.coinType = v
        this.formBank.bankCardNo = ''
        this.formBank.bankName = ''
        this.formBank.bankUserName = ''
        this.formBank.quantity = ""
        this.formBank.arrival = ''
      } else if (v == 'zfb') {
        this.coinType = v
        this.formZfb.bankCardNo = ''
        this.formZfb.bankName = ''
        this.formZfb.bankUserName = ''
        this.formZfb.quantity = ""
        this.formZfb.arrival = ''
      } else if (v == 'wx') {
        this.coinType = v
        this.formZfb.bankCardNo = ''
        this.formZfb.bankName = ''
        this.formZfb.bankUserName = ''
        this.formZfb.quantity = ""
        this.formZfb.arrival = ''
      } else {
        this.coinType = 'USDT'
        this.form.address = ''
        this.form.quantity = ""
        this.form.arrival = ''
      }
      this.$nextTick(() => {
    this.$refs.withdrawForm?.clearValidate?.()
    this.$refs.withdrawBankForm?.clearValidate?.()
    this.$refs.withdrawZfbForm?.clearValidate?.()
    this.$refs.withdrawWxForm?.clearValidate?.()
  })
      this.getSesstionToken()
    },
    onKeyPress(event) {
      const allowedCharacters = /^[a-zA-Z0-9]+$/;
      const char = String.fromCharCode(event.keyCode);
      if (!allowedCharacters.test(char)) {
        event.preventDefault();
      }
    },
    async getSesstionToken() {
      await this.requestSessionToken();
    },
    ...mapActions("withdraw", [
      "requestSessionToken",
      "requesWithdraw",
      "requestWithdrawFee",
    ]),
    handleClose() {
      this.dialogVisible = false;
    },
    async pushWithdrawAddress() {
      if (this.addresss == "") {
        this.$message({
          message: this.$t("message.home.请输入完整的支付密码!"),
          type: "warning",
        });
        return;
      } else {
        const res = await BindWithdrawAddress({
          blockchain_name: this.form.network,
          coin: this.optionsValue,
          channel_address: this.form.address,
        });
        if (res.code == 0) {
          this.$message({
            message: this.$t("message.home.绑定成功"),
            type: "success",
          });
          this.dialogVisible = false;
        }
      }
    },
    numberFormat,
    changeCoin(e) {
      const data = this.rechargeAddress.filter((item) => item.coin === e);
      if (data && data.length) {
        this.form.network = data[0].blockchain_name;
      }
      this.coinInfo = [...data];
    },
    goBack() {
      this.$router.push("/userInfo/money-package");
    },
    requestChannelAddress() {
      return this.$store.dispatch("recharge/requestRechargeAddress");
    },
    selectCurrency(val) {
      this.$emit("select-currency", val);
      this.dialogVisible = false;
    },
    copy() {
      if (!emptyStr(this.form.address)) {
        copyTextToClipboard(this.form.address);
        this.$notify({
          title: this.$t("message.home.successTips"),
          message: this.$t("message.home.copySuccess"),
          type: "success",
        });
      }
      // else {
      //     this.$notify({
      //         title: this.$t('message.home.fail' /**成功 */),
      //         message: this.$t('message.home.pleaseEnterAddress' /**提现成功 */),
      //         type: 'error',
      //     })
      // }
    },
    async payCallback(password, successCallBack, failCallBack) {
      this.password = password;
      console.log('12313',this.formWX)
      this.requesWithdraw({
        session_token: this.sessionToken,
        amount: this.form.quantity || this.formBank.bankQuantity || this.formZfb.quantity || this.formWx.quantity,
        from: this.form.address,
        channel: this.coinType,
        bankUserName: this.formBank.bankUserName || this.formZfb.userName || this.formWx.userName,
        bankName: this.formBank.bankName,
        bankCardNo: this.formBank.bankCardNo || this.formZfb.bankCardNo || this.formWx.bankCardNo,
        safeword: this.password,
        qdCode: this.formWx.qdCode,
      })
        .then((res) => {
          // console.log(res);
          this.$notify({
            title: this.$t("message.home.successTips" /**成功 */),
            message: this.$t("message.home.withdrawSuccess" /**提现成功 */),
            type: "success",
          });
          if (this.rachargeTypeV == 'wx') {
            this.$message({
              type: "warning",
              message: this.$t("message.home.提现请联系客服"),
            });
          }
          // this.$emit('input', false)
          this.goBack();
          successCallBack && successCallBack();
        })
        .catch((err) => {
          failCallBack && failCallBack();
          this.payModalShow = false;
        });
    },
    withdraw() {
      this.$refs.withdrawForm.validate(async (valid) => {
        if (valid) {
          this.payModalShow = true;
        }
      });
    },
    Bankwithdraw() {
      this.$refs.withdrawBankForm.validate(async (valid) => {
        console.log('valid ->', valid);
        if (valid) {
          this.payModalShow = true;
        }
      });
    },
    Zfbwithdraw() {
      this.$refs.withdrawZfbForm.validate(async (valid) => {
        console.log('valid ->', valid);
        if (valid) {
          this.payModalShow = true;
        }
      })
    },
    Wxwithdraw() {
      this.$refs.withdrawWxForm.validate(async (valid) => {
        console.log('valid ->', valid);
        if (valid) {
          this.payModalShow = true;
        }
      })
    }
  },
};
</script>

<style lang="scss" scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #ccc;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

html[dir="rtl"] {
  .el-form-item /deep/ .el-form-item__error {
    right: 0;
    left: auto;
  }

  .info_page_container {
    margin-left: 0;
    margin-right: 34px;
  }

  .el-radio-group {
    direction: ltr;
  }

  .el-form-item /deep/ .el-form-item__label {
    float: right;
  }

  .el-form--label-top /deep/ .el-form-item__label {
    float: none !important;
  }
}

.withdraw-dialog {
  .info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 27px;
    font-size: 12px;

    .price {
      color: var(--color-main);
    }
  }

  .withdraw-tips {
    span:first-child {
      font-weight: 400;
      font-size: 12px;
      color: var(--color-subtitle);
    }

    span:last-child {
      font-weight: 400;
      font-size: 12px;
      color: var(--color-main);
    }
  }
}

.wallet-address {
  position: relative;

  .copy-btn {
    position: absolute;
    top: 0;
    bottom: 0;
    right: 15px;
    z-index: 1;
    color: #1d91ff;
    user-select: none;
    display: block;
    cursor: pointer;

    &.disabled {
      color: #aaa;
      cursor: default;
    }
  }
}

.right {
  cursor: pointer;
}
</style>
