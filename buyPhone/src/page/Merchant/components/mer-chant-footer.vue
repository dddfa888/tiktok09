<template>
  <div class="mer-chant-footer">
    <div class="footer-wrap">
      <div class="bottom-wrap">
        <div class="logo-wrap">
          <img :src="logo" />
        </div>
        <div class="serviece-wrap">
          <ul>
            <li class="title">{{ $t("我的帐户") }}</li>
            <li>
              <a href="JavaScript:;" @click="openUrl(1)">{{ $t("登录") }}</a>
            </li>
            <li>
              <a href="JavaScript:;" @click="openUrl(2)">
                {{
                $t("收藏商品")
                }}
              </a>
            </li>
            <li>
              <a href="JavaScript:;" @click="openUrl(3)">
                {{
                $t("购买记录")
                }}
              </a>
            </li>
            <li>
              <a href="JavaScript:;" @click="openUrl(4)">{{ $t("帐户") }}</a>
            </li>
          </ul>
          <ul class="two-footer">
            <li class="title">{{ $t("卖家专区") }}</li>
            <li>
              <a href="JavaScript:;" @click="openUrl(5)">{{ $t("做卖家") }}</a>
            </li>
            <li>
              <a href="#" @click="openUrl(6)">{{ $t("卖家面板登录") }}</a>
            </li>
          </ul>
        </div>
      </div>

      <div class="copyright">
        <p>{{ $t("版权所有© 2019-2022 SHOP PO, LLC. 版权所有") }}</p>
        <p>
          {{
          $t(
          "使用本网站，即表示您同意其服务条款（禁止将本网站上的任何信息用于商业用途）及其隐私政策。 当您使用我们的一个链接进行购买时，我们可能会赚取佣金。"
          )
          }}
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { Toast } from "vant";
import { isLogin } from "@/utils/utis";
export default {
  name: "mer-chant-footer",
  data() {
    return {
      logo: require(`@/assets/${process.env.VUE_APP_ITEM_NAME}/logo.png`)
    };
  },
  components: {
    [Toast.name]: Toast
  },
  methods: {
    openUrl(index) {
      switch (index) {
        case 1:
          if (!isLogin()) {
            this.$router.push("/logo");
          } else {
            this.$router.push("/me");
          }
          break;
        case 2:
          if (!isLogin()) {
            this.$router.push("/logo");
          } else {
            this.$router.push("/collectGoods");
          }
          break;
        case 3:
          if (!isLogin()) {
            this.$router.push("/logo");
          } else {
            this.$router.push("/order");
          }
          break;
        case 4:
          if (!isLogin()) {
            this.$router.push("/logo");
          } else {
            this.$router.push("/me");
          }
          break;
        case 5:
          this.$router.push("/merchantSettled");
        case 6:
          const base_url = process.env.VUE_APP_IS_APP
            ? this.$store.state.multiItem[process.env.VUE_APP_ITEM_NAME].app_url
            : location.origin;
          location.href = base_url + "/ww";
          break;
        default:
          break;
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.mer-chant-footer {
  min-width: 1200px;
  background: #212121;
  font-size: 14px;

  .footer-wrap {
    width: 1200px;
    margin: 0 auto;

    .logo-wrap {
      height: 70px;

      img {
        height: 35px;
      }
    }
  }

  .bottom-wrap {
    display: flex;
    border-bottom: 1px solid #3f3f3f;
    align-items: center;

    .address-input {
      input {
        width: 288px;
        height: 43px;
        background: #ffffff;
        border-radius: 4px;
        border: none;
        padding: 0 20px;
      }
    }

    .subscribe-but {
      width: 153px;
      height: 43px;
      border: 1px solid var(--main-color);
      border-radius: 4px;
      color: var(--main-color);
      text-align: center;
      line-height: 43px;
      margin-top: 20px;
      cursor: pointer;
    }
  }

  .serviece-wrap {
    color: #ffffff;
    padding-top: 40px;
    padding-bottom: 40px;

    ul {
      display: flex;
      margin-left: 100px;
      align-items: center;

      li {
        font-size: 12px;
        margin-right: 50px;

        a {
          color: #ffffff;
        }
      }

      .title {
        font-size: 16px;
        color: var(--main-color);
        font-weight: bold;
      }
    }

    .two-footer {
      margin-top: 30px;
    }
  }

  .copyright {
    color: #6b6b6b;
    font-size: 12px;
    padding: 20px 0;
  }
}

@media screen and (max-width: 500px) {
  .mer-chant-footer {
    min-width: 100% !important;

    .footer-wrap {
      width: 100% !important;
    }

    .bottom-wrap {
      display: block !important;
      margin-left: 15px;
      padding-bottom: 20px;
    }

    .serviece-wrap {
      padding: 0 !important;
      display: block !important;
      margin-left: 0px !important;

      ul {
        margin-left: 0px;
        margin-top: 30px;
        display: block !important;

        li {
          margin-top: 20px;
        }
      }
    }

    .copyright {
      padding: 15px;
    }

    .logo-wrap {
      padding-top: 15px;
      height: 50px !important;
    }
  }
}
</style>
