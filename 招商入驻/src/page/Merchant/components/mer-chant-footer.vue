<template>
  <div class="footer">
    <div class="app-container">
      <div class="footer-wrapper flex-start">
        <div class="footer-title flex-start">
          <img
            :src="itemname == 'SIMON' || itemname == 'Azedi' ? require(`@/assets/image/${itemname}/logo1.svg`) : itemname == 'AntMall' ? require(`@/assets/image/${itemname}/logo.svg`) : itemname == 'TikTok' || itemname == 'Shop2u' ? require(`@/assets/image/${itemname}/logo.png`) : itemname == 'Laz' ? require(`@/assets/image/${itemname}/logo1.svg`) : require(`@/assets/image/${itemname}/shoplogo.png`)"
            alt
            :style="itemname == 'SM-wholesale shop' || itemname == 'SIMON' || itemname == 'Alibaba' ? 'height:38px' : itemname === 'Shop2u' ? 'height: 42px' : itemname === 'TikTok-Wholesale' ? 'width:276px; height:66px' : ''"
            @click="goHome"
          />
          <p>{{ $t("footer_getCoupons") }}</p>
          <el-input v-model="email" :placeholder="$t('footer_yourEmail')"></el-input>
          <div class="sub" @click="subScription">{{ $t("footer_subscription") }}</div>
        </div>
        <div class="footer-nav">
          <div class="footer-nav-item" v-for="(item, index) in linkNav" :key="index">
            <span>{{ item.title }}</span>
            <ul>
              <el-tooltip
                v-for="(childItem, childIndex) in item.nav"
                effect="light"
                :placement="childItem.name == $t('footer_contactUs') ? 'left' : ''
                "
                :key="childIndex"
              >
                <div
                  slot="content"
                  v-if="itemname !== 'SM-wholesale shop' && itemname !== 'Argos' && itemname !== 'INT Overstock'"
                >{{ $t("footer_businessEmail") }}: {{ bassEmail }}</div>
                <div slot="content" v-else-if="itemname == 'SM-wholesale shop'">
                  {{ $t("message.home.businessEmail") }}: {{ 'support@smwholesaleshop01.com' }}
                  <br />
                  {{ $t("message.home.businessEmail") }}2: {{ 'support@smwholesaleshop02.com' }}
                  <br />
                  {{ $t("message.home.businessEmail") }}2: {{ 'support@smwholesaleshop03.com' }}
                </div>
                <div slot="content" v-else-if="itemname == 'Argos'">
                  {{ $t("footer_businessEmail") }}: {{ bassEmail }}
                  <br />Line: Argos061
                  <br />WhatsApp: +44 7477 466470
                </div>
                <div slot="content" v-else>
                  {{ $t("footer_businessEmail") }}: {{ bassEmail }}
                  <br />
                  {{ $t("企业电话") }}: +1(840) 800-1088
                </div>
                <li @click="childItem.clickEvent">{{ childItem.name }}</li>
              </el-tooltip>
            </ul>
          </div>
        </div>
      </div>

      <div class="footer-bottom" v-if="itemname !== 'INT Overstock' && itemname !== 'Worten'">
        <p
          style="text-align: center; margin-bottom: 15px"
        >{{ itemname !== 'TikTok' ? $t("footer_footTip", { name: itemname }) : $t("footTipTikTok") }}</p>
        <p :style="itemname == 'TikTok' && 'text-align:center'">
          {{ itemname !== 'TikTok' ? $t("footer_footTips", { name: itemname }) : '©️2023 TikTok Mall. All rights
          reserved.'}}
          {{ itemname == 'GreenMall' ? $t("商城联系电话：") + "+ 44 7441 438185。" : '' }}
        </p>
      </div>
      <div class="footer-bottom" v-else-if="itemname == 'INT Overstock'">
        <p style="text-align: center; margin-bottom: 15px">© Copyright 2024, overstock1.me®, Inc.</p>
        <p style="text-align: center;">799 Coliseum Way Midvale, UT 84047 | 1-840-800-1088</p>
      </div>
      <div class="footer-bottom" v-else-if="itemname == 'Worten'">
        <p style="text-align: center; margin-bottom: 15px">{{ $t("wortenBottom") }}</p>
      </div>
      <!-- <div class="footer-bottom">
        <p style="text-align: center; margin-bottom: 15px">
          {{ $t("footer_footTip", { name: itemname }) }}
        </p>
        <p>
          {{ $t("footer_footTips", { name: itemname }) }}
        </p>
      </div>-->
    </div>
    <Service :isShow="isShow" @show="closeService" />
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { sub, apiGetCustomerService } from "@/API/home.js";
import bn from "@/assets/image/Merchant/bn.png";
import hb from "@/assets/image/Merchant/hb.png";
import oy from "@/assets/image/Merchant/oy.png";
import kk from "@/assets/image/Merchant/kk.png";
import cb from "@/assets/image/Merchant/cb.png";
import hl from "@/assets/image/Merchant/hl.png";
import kc from "@/assets/image/Merchant/kc.png";
import bf from "@/assets/image/Merchant/bf.png";
import bit from "@/assets/image/Merchant/bit.png";
// import max from "@/assets/image/Merchant/max.png";
import mh from "@/assets/image/Merchant/mh-logo.png";
import crypto from "@/assets/image/Merchant/crypto.png";
import maicoin from "@/assets/image/Merchant/maiCoin.png";

import argos from "@/assets/image/Merchant/argos.png";
import hab from "@/assets/image/Merchant/hab.png";
import tu from "@/assets/image/Merchant/tu.png";
import sains from "@/assets/image/Merchant/sains.png";
import nectar from "@/assets/image/Merchant/nectar.png";
import clogo from "@/assets/image/Merchant/clogo.png";
import Service from "@/page/customerService/pcIndex.vue";
import { is_mobile } from "@/utils/utis";
import { Toast, Notify } from "vant";
import amazon from "@/assets/image/amazon.png";
import justeat from "@/assets/image/justeat.png";
import zd from "@/assets/image/zd.png";
import zelle from "@/assets/image/zelle.png";
import bdo from "@/assets/image/bdo.png";
export default {
  name: "EsFooter",
  components: {
    Service,
    [Toast.name]: Toast,
    [Notify.Component.name]: Notify.Component
  },
  data() {
    return {
      onlinePath: "",
      email: "",
      isShow: false,
      itemname:
        process.env.VUE_APP_ITEM_NAME == "Mbuy"
          ? "Argos"
          : process.env.VUE_APP_ITEM_NAME,
      bassEmail: "",
      logoList: [
        {
          url:
            process.env.VUE_APP_ITEM_NAME == "Tongda"
              ? amazon
              : process.env.VUE_APP_ITEM_NAME == "Shop2u"
              ? Shop2u
              : process.env.VUE_APP_ITEM_NAME == "SM-wholesale shop"
              ? justeat
              : argos,
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Shop2u"
              ? ""
              : process.env.VUE_APP_ITEM_NAME == "Tongda"
              ? window.open("https://www.amazon.com/", "_blank")
              : process.env.VUE_APP_ITEM_NAME == "SM-wholesale shop"
              ? window.open("https://www.smsupermalls.com/", "_blank")
              : location.reload();
          }
        },
        {
          url:
            process.env.VUE_APP_ITEM_NAME !== "SM-wholesale shop" ? hab : bdo,
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Shop2u"
              ? ""
              : process.env.VUE_APP_ITEM_NAME !== "SM-wholesale shop"
              ? window.open("https://www.bdo.com/", "_blank")
              : window.open("https://www.habitat.co.uk", "_blank");
          }
        },
        {
          url: tu,
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Shop2u"
              ? ""
              : window.open("https://tuclothing.sainsburys.co.uk/", "_blank");
          }
        },
        {
          url: sains,
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Shop2u"
              ? ""
              : window.open("https://www.sainsburys.co.uk", "_blank");
          }
        },
        {
          url: nectar,
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Shop2u"
              ? ""
              : window.open("https://www.nectar.com ", "_blank");
          }
        },
        {
          url: clogo,
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Shop2u"
              ? ""
              : window.open("https://crypto.com/ ", "_blank");
          }
        },
        {
          url: zd,
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Shop2u"
              ? ""
              : window.open("https://www.sc.com/en/", "_blank");
          }
        },
        {
          url: zelle,
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Shop2u"
              ? ""
              : window.open("https://www.zellepay.com/", "_blank");
          }
        },
        {
          url: mh,
          clickEvent: () => {
            window.open(
              "https://www.moneyhero.com.hk/zh/personal-loan/personal-instalment",
              "_blank"
            );
          }
        }
      ]
    };
  },
  computed: {
    ...mapGetters(["existToken", "isLogin"]),
    payList() {
      return [
        {
          img: bn,
          name: "Binance",
          clickEvent: () => {
            window.open("https://www.binance.com", "_blank");
          }
        },
        {
          img: hb,
          name: "Huobi",
          clickEvent: () => {
            window.open("https://www.huobi.com/en-us/", "_blank");
          }
        },
        {
          img: oy,
          name: "OKX",
          clickEvent: () => {
            window.open("https://www.okx.com", "_blank");
          }
        },
        {
          img: kk,
          name: "KraKen",
          clickEvent: () => {
            window.open("https://www.kraken.com", "_blank");
          }
        },
        {
          img: cb,
          name: "Coinbase",
          clickEvent: () => {
            window.open("https://www.coinbase.com", "_blank");
          }
        },
        {
          img:
            process.env.VUE_APP_ITEM_NAME == "Hive" ||
            process.env.VUE_APP_ITEM_NAME == "TikTok-Wholesale"
              ? bit
              : process.env.VUE_APP_ITEM_NAME == "Argos" ||
                process.env.VUE_APP_ITEM_NAME == "ArgosShop"
              ? crypto
              : hl,
          name:
            process.env.VUE_APP_ITEM_NAME == "Hive"
              ? "Bitoex"
              : process.env.VUE_APP_ITEM_NAME == "Argos" ||
                process.env.VUE_APP_ITEM_NAME == "ArgosShop"
              ? "Crypto"
              : "MetaMask",
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Hive" ||
            process.env.VUE_APP_ITEM_NAME == "TikTok-Wholesale"
              ? window.open("https://www.bitoex.com/", "_blank")
              : process.env.VUE_APP_ITEM_NAME == "Argos" ||
                process.env.VUE_APP_ITEM_NAME == "ArgosShop"
              ? window.open("https://crypto.com/", "_blank")
              : window.open("https://metamask.io/", "_blank");
          }
        },
        {
          img:
            process.env.VUE_APP_ITEM_NAME == "Argos" ||
            process.env.VUE_APP_ITEM_NAME == "ArgosShop"
              ? bit
              : kc,
          name:
            process.env.VUE_APP_ITEM_NAME == "Argos" ||
            process.env.VUE_APP_ITEM_NAME == "ArgosShop"
              ? "Bitopro"
              : "KuCoin",
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Argos" ||
            process.env.VUE_APP_ITEM_NAME == "ArgosShop"
              ? window.open("https://www.bitopro.com", "_blank")
              : window.open("https://www.kucoin.com ", "_blank");
          }
        },
        {
          img:
            process.env.VUE_APP_ITEM_NAME == "Hive" ||
            process.env.VUE_APP_ITEM_NAME == "TikTok-Wholesale"
              ? maicoin
              : process.env.VUE_APP_ITEM_NAME == "Argos" ||
                process.env.VUE_APP_ITEM_NAME == "ArgosShop"
              ? maicoin
              : bf,
          name:
            process.env.VUE_APP_ITEM_NAME == "Hive" ||
            process.env.VUE_APP_ITEM_NAME == "TikTok-Wholesale"
              ? "Bitfinex"
              : process.env.VUE_APP_ITEM_NAME == "Argos" ||
                process.env.VUE_APP_ITEM_NAME == "ArgosShop"
              ? "MaiCoin"
              : "Bitfinex",
          clickEvent: () => {
            process.env.VUE_APP_ITEM_NAME == "Hive" ||
            process.env.VUE_APP_ITEM_NAME == "TikTok-Wholesale"
              ? window.open("https://max.maicoin.com/", "_blank")
              : process.env.VUE_APP_ITEM_NAME == "Argos" ||
                process.env.VUE_APP_ITEM_NAME == "ArgosShop"
              ? window.open("https://max.maicoin.com/", "_blank")
              : window.open("https://www.bitfinex.com", "_blank");
          }
        }
      ];
    },
    linkNav() {
      return [
        {
          title: this.$t("footer_custService"),
          nav: [
            {
              name: this.$t("footer_helpLine"),
              path: "",
              clickEvent: () => {
                this.JumpService();
              }
            },
            {
              name: this.$t("footer_contactUs"),
              path: "",
              clickEvent: () => {}
            },
            {
              name:
                process.env.VUE_APP_ITEM_NAME == "Hive" ||
                process.env.VUE_APP_ITEM_NAME == "TikTok-Wholesale" ||
                process.env.VUE_APP_ITEM_NAME == "Worten"
                  ? ""
                  : process.env.VUE_APP_ITEM_NAME == "INT Overstock"
                  ? this.$t("客户端下载")
                  : process.env.VUE_APP_ITEM_NAME == "Shop2u"
                  ? this.$t("安卓下载")
                  : this.$t("App下载(买家端)"),
              path: "",
              clickEvent: () => {
                if (process.env.VUE_APP_ITEM_NAME == "Shop2u") {
                  window.open(
                    "https://play.google.com/store/apps/details?id=com.commerce.app",
                    "_blank"
                  );
                } else {
                  window.location.href = window.origin + "/app.html";
                }
              }
            },
            {
              name:
                process.env.VUE_APP_ITEM_NAME == "Hive" ||
                process.env.VUE_APP_ITEM_NAME == "TikTok-Wholesale" ||
                process.env.VUE_APP_ITEM_NAME == "INT Overstock"
                  ? ""
                  : process.env.VUE_APP_ITEM_NAME == "Shop2u"
                  ? this.$t("苹果下载")
                  : this.$t("App下载(卖家端)"),
              path: "",
              clickEvent: () => {
                if (process.env.VUE_APP_ITEM_NAME == "Shop2u") {
                  window.open(
                    "https://apps.apple.com/my/app/shop2u/id6448880380",
                    "_blank"
                  );
                } else {
                  window.location.href = window.origin + "/app.html";
                }
              }
            }
          ]
        },
        {
          title: this.$t("footer_returnsAndEx"),
          nav: [
            {
              name: this.$t("footer_privacyPolicy"),
              path: "",
              clickEvent: () => {
                this.JumpTo("/privacyPolicy");
              }
            },
            {
              name: this.$t("footer_return1Policy"),
              path: "",
              clickEvent: () => {
                this.JumpTo("/returnPolicy");
              }
            },
            {
              name: this.$t("footer_delivery"),
              path: "",
              clickEvent: () => {
                this.JumpTo("/delivery");
              }
            },
            {
              name: this.$t("footer_sellerRules"),
              path: "",
              clickEvent: () => {
                this.JumpTo("/shippingPolicy");
              }
            },
            {
              name: this.itemname == "Worten" ? this.$t("支持政策") : "",
              path: "",
              clickEvent: () => {
                this.itemname == "Worten" ? this.JumpTo("/supportPolicy") : "";
              }
            }
          ]
        },
        {
          title: this.$t("footer_user_Center"),
          nav: [
            {
              name: this.$t("footer_userReg"),
              path: "",
              clickEvent: () => window.open(window.origin + "/#/login")
            },
            {
              name: this.$t("footer_orderTrack"),
              path: "",
              clickEvent: () =>
                window.open(window.origin + "/#/userInfo/my-order?index=2")
            },
            {
              name: this.$t("footer_purchageHistory"),
              path: "",
              clickEvent: () =>
                window.open(window.origin + "/#/userInfo/collect-goods?index=3")
            },
            {
              name: this.$t("footer_myPurse"),
              path: "",
              clickEvent: () =>
                window.open(window.origin + "/#/userInfo/money-package?index=1")
            }
          ]
        },
        {
          title: this.$t("footer_aboutUs"),
          nav: [
            {
              name:
                process.env.VUE_APP_ITEM_NAME == "Alibaba"
                  ? this.$t("关于阿里巴巴")
                  : this.$t("footer_aboutUs"),
              path: "",
              clickEvent: () => {
                process.env.VUE_APP_ITEM_NAME == "Alibaba"
                  ? window.open(
                      "https://activities.alibaba.com/alibaba/following-about-alibaba.php?spm=a2700.trademark.0.0.605aUWseUWseXn",
                      "_blank"
                    )
                  : process.env.VUE_APP_ITEM_NAME == "SIMON"
                  ? window.open(
                      "https://business.simon.com/why-simon",
                      "_blank"
                    )
                  : process.env.VUE_APP_ITEM_NAME == "SM-wholesale shop"
                  ? window.open(
                      "https://www.smsupermalls.com/about-us/",
                      "_blank"
                    )
                  : process.env.VUE_APP_ITEM_NAME == "TikTok-Wholesale"
                  ? this.JumpTo("/TikTok-about")
                  : process.env.VUE_APP_ITEM_NAME == "Meta"
                  ? this.JumpTo("/aboutUs")
                  : process.env.VUE_APP_ITEM_NAME == "Hive"
                  ? this.JumpTo("/Hive-about")
                  : process.env.VUE_APP_ITEM_NAME == "FamilyMart"
                  ? window.open("https://familymart.vip/", "_blank")
                  : process.env.VUE_APP_ITEM_NAME == "Shop2u"
                  ? window.open(
                      "https://www.asiaone.com/business/shop2u-platform-officially-entered-southeast-asian-market-will-create-nearly-2-million",
                      "_blank"
                    )
                  : process.env.VUE_APP_ITEM_NAME == "INT Overstock"
                  ? window.open("https://overstock1.me/gw/#/about", "_blank")
                  : window.open("", "_blank");
              }
            },
            {
              name:
                process.env.VUE_APP_ITEM_NAME == "Alibaba"
                  ? this.$t("企业责任")
                  : process.env.VUE_APP_ITEM_NAME == "SIMON"
                  ? this.$t("西蒙基金会")
                  : process.env.VUE_APP_ITEM_NAME == "Shop2u"
                  ? this.$t("企业证明")
                  : process.env.VUE_APP_ITEM_NAME == "INT Overstock"
                  ? this.$t("职业机会")
                  : this.$t("footer_Recruitment"),
              // name: this.$t("footer_Recruitment"),
              path: "",
              clickEvent: () => {
                process.env.VUE_APP_ITEM_NAME == "Alibaba"
                  ? window.open(
                      "https://www.alibabagroup.com/en-US/esg?spm=a2700.trademark.0.0.605aUWseUWseXn",
                      "_blank"
                    )
                  : process.env.VUE_APP_ITEM_NAME == "SIMON"
                  ? window.open("https://syf.org/", "_blank")
                  : process.env.VUE_APP_ITEM_NAME == "SM-wholesale shop"
                  ? window.open("https://www.smprimecareers.com/", "_blank")
                  : process.env.VUE_APP_ITEM_NAME == "Shop2u"
                  ? this.JumpTo("/enterprise-prove")
                  : process.env.VUE_APP_ITEM_NAME == "INT Overstock"
                  ? window.open("https://overstock1.me/gw/#/", "_blank")
                  : window.open("", "_blank");
              }
            },
            {
              name:
                process.env.VUE_APP_ITEM_NAME == "Alibaba"
                  ? this.$t("新闻中心")
                  : process.env.VUE_APP_ITEM_NAME == "SIMON"
                  ? this.$t("零售商营销")
                  : process.env.VUE_APP_ITEM_NAME == "INT Overstock"
                  ? ""
                  : this.$t("footer_news"),
              path: "",
              clickEvent: () => {
                process.env.VUE_APP_ITEM_NAME == "Alibaba"
                  ? window.open(
                      "https://reads.alibaba.com/?spm=a2700.trademark.0.0.605aUWseUWseXn",
                      "_blank"
                    )
                  : process.env.VUE_APP_ITEM_NAME == "SIMON"
                  ? window.open(
                      "https://business.simon.com/retailer-marketing",
                      "_blank"
                    )
                  : process.env.VUE_APP_ITEM_NAME == "SM-wholesale shop"
                  ? window.open("https://smstore.com/news-stories/", "_blank")
                  : process.env.VUE_APP_ITEM_NAME == "Shop2u"
                  ? window.open(
                      "https://finance.yahoo.com/news/british-chambers-commerce-visited-shop2u-170000654.html?fr=sycsrp_catchall",
                      "_blank"
                    )
                  : window.open(
                      "https://www.about.sainsburys.co.uk/news/media-enquiries",
                      "_blank"
                    );
              }
            },
            {
              name:
                process.env.VUE_APP_ITEM_NAME == "Alibaba"
                  ? this.$t("职业机会")
                  : process.env.VUE_APP_ITEM_NAME == "SIMON"
                  ? this.$t("职业生涯")
                  : process.env.VUE_APP_ITEM_NAME == "INT Overstock"
                  ? ""
                  : process.env.VUE_APP_ITEM_NAME == "Shop2u"
                  ? "YouTube"
                  : this.$t("footer_stateMent"),
              path: "",
              clickEvent: () => {
                process.env.VUE_APP_ITEM_NAME == "Alibaba"
                  ? window.open(
                      "https://talent.alibaba.com/en/home?lang=en&spm=a2700.trademark.0.0.605aUWseUWseXn",
                      "_blank"
                    )
                  : process.env.VUE_APP_ITEM_NAME == "SIMON"
                  ? window.open("https://careers.simon.com/", "_blank")
                  : process.env.VUE_APP_ITEM_NAME == "SM-wholesale shop"
                  ? window.open("https://www.smsupermalls.com/", "_blank")
                  : process.env.VUE_APP_ITEM_NAME == "Shop2u"
                  ? window.open("https://www.youtube.com/@shop2u", "_blank")
                  : window.open(
                      "https://www.about.sainsburys.co.uk/sustainability/plan-for-better/our-stories/2017/standing-up-to-modern-slavery",
                      "_blank"
                    );
              }
            }
          ]
        }
      ];
    }
  },
  mounted() {
    // this.$set(
    //   this.linkNav[0].nav[0],
    //   "name",
    //   this.existToken
    //     ? this.$t("footer_ userCenter")
    //     : this.$t("footer_ login" /** 登录*/)
    // );
    // this.$set(
    //   this.linkNav[0].nav[0],
    //   "clickEvent",
    //   this.existToken
    //     ? () => this.$router.push("/userInfo/dashboard")
    //     : () => this.$router.push("/login")
    // );
    this.bassEmail = this.$multiItem[process.env.VUE_APP_ITEM_NAME]?.mail;
    this.bassEmail = this.$multiItem[process.env.VUE_APP_ITEM_NAME]?.mail;
    if (this.itemname == "FamilyMart") {
      this.logoList = this.logoList.slice(1, 6);
    } else if (
      this.itemname == "Hive" ||
      process.env.VUE_APP_ITEM_NAME == "TikTok-Wholesale"
    ) {
      this.logoList = this.logoList.slice(1, 5);
      this.linkNav[3].nav = this.linkNav[3].nav.slice(0, 1);
    } else if (this.itemname == "FamilyShop") {
      this.logoList = this.logoList.slice(0, 6);
    } else if (this.itemname == "SM-wholesale shop") {
      this.logoList = this.logoList
        .slice(0, 2)
        .concat(this.logoList.slice(4, 8));
    } else {
      this.logoList = this.logoList.slice(0, 5);
    }
    console.log("this.linkNav ->", this.linkNav);
    if (this.itemname == "Green Mall" || this.itemname == "Azedi") {
      this.linkNav = this.linkNav.slice(0, 3);
    }
    this.getOnlinePath();
  },
  methods: {
    JumpService() {
      if (this.itemname == "FamilyShop") {
        im_create_iframe_client.open();
        return;
      }
      if (this.onlinePath) {
        window.open(this.onlinePath, "_blank");
      } else if (is_mobile()) {
        this.$router.push("/customerServiceIndex");
      } else {
        this.isShow = true;
      }
    },
    JumpTo(path) {
      this.$router.push(path);
    },
    async getOnlinePath() {
      let res = await apiGetCustomerService({ code: "customer_service_url" });
      this.onlinePath = res.customer_service_url;
    },
    goHome() {
      this.$router.push("/");
    },
    closeService(val) {
      this.isShow = val;
    },
    async subScription() {
      console.log("this.email ->", this.email);
      var reg = /^([a-zA-Z\d][\w-]{2,})@(\w{2,})\.([a-z]{2,})(\.[a-z]{2,})?$/;
      var ret = reg.test(this.email);
      if (ret) {
        const result = await sub({ email: this.email });
        console.log("res123 ->", result);
        Notify({
          type: "success",
          message: this.$t("SUBSCRIBE SUCCESSFULLY!")
        });
      } else {
        Notify({
          color: "#fff",
          message: this.$t("Please fill in the correct email address!")
        });
      }
      // console.log("res ->", res);
    }
  }
};
</script>

<style lang="scss" scoped>
html[dir="rtl"] {
  .footer-nav {
    margin-left: 0;
    margin-right: 120px;
  }

  @media screen and (max-width: 500px) {
    .footer-nav {
      margin-left: 0;
      margin-right: 0;
    }

    .footer-nav-item {
      margin-left: 80px;
      margin-right: 0;
    }
  }
}

.app-container {
  width: 1200px;
  margin: 0 auto;
}

.flex-start {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.footer {
  background-color: #212121;
  padding: 27px 20px;

  &-title {
    // border-bottom: 1px solid #3f3f3f;
    // padding-bottom: 30px;
    // margin-bottom: 27px;

    img {
      height: 65px;
      // height: 44px;
      margin-right: 9px;
      margin-bottom: 10px;
      cursor: pointer;
    }

    h1 {
      color: var(--color-white);
      font-size: 20px;
      font-weight: 600;
      margin: 0;
    }
  }

  &-wrapper {
    align-items: flex-start !important;
    border-bottom: 1px solid #3f3f3f;
    padding: 10px 40px;

    .footer-title {
      display: flex;
      flex-direction: column;
      align-items: inherit;

      p {
        color: var(--color-main);
        font-size: 12px;
      }

      /deep/.el-input__inner {
        width: 287px;
        height: 44px;
        margin: 12px 0 25px 0;
      }

      .sub {
        width: 156px;
        height: 42px;
        color: var(--color-main);
        line-height: 42px;
        font-size: 12px;
        text-align: center;
        border: 1px solid var(--color-main);
        border-radius: 4px;
        cursor: pointer;
      }
    }
  }

  &-nav {
    width: 100%;
    display: flex;
    margin-left: 120px;

    &-item {
      display: flex;
      flex-direction: column;
      flex: 1 1 auto;

      span {
        font-size: 16px;
        font-weight: 700;
        color: var(--color-main);
        margin-bottom: 37px;
      }

      ul {
        display: flex;
        list-style: none;
        flex-direction: column;

        li {
          margin-bottom: 37px;
          font-size: 12px;
          font-weight: 300;
          color: #fff;
          cursor: pointer;

          &:hover {
            color: var(--color-main);
          }
        }
      }
    }
  }

  .sec {
    padding-top: 20px;
    justify-content: space-between;

    .payment {
      .title {
        font-size: 14px;
        color: #fff;
      }

      .payment-methods {
        display: flex;
        width: 348px;
        flex-wrap: wrap;
        justify-content: space-between;

        .pay {
          display: flex;
          flex-direction: column;
          align-items: center;
          margin-right: 35px;
          width: 42px;
          cursor: pointer;

          &:hover span {
            color: var(--color-main);
          }

          img {
            width: 32px;
            height: 32px;
            margin-bottom: 6px;
            margin-top: 15px;
          }

          span {
            font-size: 10px;
            color: #ababab;
          }
        }
      }
    }

    .argos {
      display: flex;

      .left {
        margin-left: 58px;
        margin-right: 68px;
        color: #fff;
        font-size: 12px;

        .title {
          font-size: 20px;
          font-weight: 600;
          margin: 19px 0 10px 0;
          color: #fff !important;
        }

        .dec {
          text-align: justify;
          word-break: break-all;
        }
      }

      .right {
        width: 230px;

        img {
          width: 230px;
        }
      }
    }
  }

  .sec-img {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 84px;
    margin-bottom: 28px;

    img {
      cursor: pointer;
      max-height: 28px;
      margin-right: 37px;
    }
  }

  .just-img {
    img {
      &:nth-child(5) {
        max-height: 58px !important;
      }

      &:nth-child(1) {
        max-height: 18px !important;
        margin-top: 4px;
      }
    }
  }

  .img-sec {
    img {
      &:nth-child(1) {
        width: 77px;
        transform: translate(4px, 7px);
      }
    }
  }

  &-bottom {
    font-size: 12px;
    padding: 22px 20px;
    color: #6b6b6b;
  }
}

@media screen and (max-width: 500px) {
  .footer-wrapper {
    padding: 0 !important;
  }

  .app-container {
    width: 100%;
  }

  .flex-start {
    flex-direction: column;
  }

  .footer-title {
    width: 100%;
  }

  .footer-nav {
    margin-left: 0;
    flex-wrap: wrap;
    justify-content: space-between;

    .footer-nav-item {
      margin-right: 80px;
      flex: none;

      span {
        margin-top: 30px;
        margin-bottom: 20px;
      }

      ul li {
        margin-bottom: 15px;
      }
    }
  }

  .sec {
    .argos {
      flex-direction: column;
      align-items: center;
      margin-left: 0 !important;

      .left {
        margin-left: 0 !important;
        margin-right: 0 !important;
        margin-bottom: 20px;
      }
    }
  }

  .sec-img {
    flex-wrap: wrap;
    margin-top: 20px !important;

    img {
      margin-left: 15px;
      margin-bottom: 15px;
    }
  }
}
</style>
