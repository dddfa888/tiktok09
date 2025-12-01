<template>
  <!-- 站内信 -->
  <div
    style="
      display: flex;
      justify-content: center;
      justify-content: space-around;
      width: 70px;
    "
  >
    <el-dropdown
      :hide-on-click="false"
      class="message-dropdown"
      trigger="click"
      @visible-change="visibleChange"
    >
      <div class="bell active horn-wrapper">
        <div class="icon-container">
          <img
            :src="require('@/assets/images/nav/Horn.png')"
            alt=""
            class="w-22 h-22"
          />
          <div v-if="getUnread > 0" class="tip">
            {{ getUnread > 99 ? 99 : getUnread }}
          </div>
        </div>
        <audio ref="notifyAudio" style="display: none">
          <source :src="require('@/assets/notify.mp3')" type="audio/mpeg" />
        </audio>
      </div>
      <el-dropdown-menu slot="dropdown" placement="bottom-start">
        <div
          ref="projectDropdown"
          class="project-dropdown"
          @scroll.stop="handleScroll"
        >
          <div ref="dropdownMenu">
            <el-dropdown-item v-for="(item, index) in NoticeList" :key="index">
              <div class="message">
                <div
                  class="message-item"
                  :class="['ar'].includes(currentLanguage) ? 'rtl' : ''"
                >
                  <div
                    :class="
                      item.status === 2
                        ? 'message-title'
                        : 'message-title active'
                    "
                  >
                    {{ $t(item.title) }}
                  </div>
                  <div class="message-time">{{ item.createTime }}</div>
                  <div class="message-content" v-if="item.tipCode === 'singe'">
                    {{ formatI18nContent(item.content) }}
                  </div>
                  <div class="message-content" v-if="item.tipCode === 'none'">
                    {{ $t(item.content) }}
                  </div>
                  <div
                    class="message-content"
                    v-if="item.tipCode === 'customize'"
                  >
                    {{ item.content }}
                  </div>
                </div>
              </div>
            </el-dropdown-item>
            <el-dropdown-item @click.stop="getMessage">
              <div
                v-show="lock"
                style="width: 100%; text-align: center; font-size: 14px"
              >
                {{ $t("加载中...") }}
              </div>
            </el-dropdown-item>
            <div
              v-if="NoticeList.length === 0"
              style="
                display: flex;
                justify-content: center;
                align-items: center;
                height: 400px;
                color: #aaaaaa;
                flex-direction: column;
              "
            >
              <div>
                <i
                  class="el-icon-document"
                  style="font-size: 46px; margin-bottom: 24px"
                ></i>
              </div>
              <div>
                {{ $t("暂无记录") }}
              </div>
            </div>
          </div>
        </div>
      </el-dropdown-menu>
    </el-dropdown>
    <!-- xxxxxxx -->
    <el-dropdown
      :hide-on-click="false"
      class="message-dropdown"
      trigger="click"
      @visible-change="visibleChange"
    >
      <div class="bell active">
        <img
          :src="require('@/assets/images/nav/bell.png')"
          alt=""
          class="w-22 h-22"
        />
        <div v-if="messageNumber > 0" class="tip">
          {{ messageNumber > 99 ? 99 : messageNumber }}
        </div>
        <audio ref="notifyAudio" style="display: none">
          <source :src="require('@/assets/notify.mp3')" type="audio/mpeg" />
        </audio>
      </div>
      <el-dropdown-menu slot="dropdown" placement="bottom-start">
        <div
          ref="projectDropdown"
          class="project-dropdown"
          @scroll.stop="handleScroll"
        >
          <div ref="dropdownMenu">
            <el-dropdown-item v-for="(item, index) in messageList" :key="index">
              <div class="message">
                <div
                  class="message-item"
                  :class="['ar'].includes(currentLanguage) ? 'rtl' : ''"
                >
                  <div
                    :class="
                      item.status === 2
                        ? 'message-title'
                        : 'message-title active'
                    "
                  >
                    {{ item.title }}
                  </div>
                  <div class="message-content">{{ item.content }}</div>
                  <div class="message-time">{{ item.sendTime }}</div>
                </div>
              </div>
            </el-dropdown-item>
            <el-dropdown-item @click.stop="getMessage">
              <div
                v-show="lock"
                style="width: 100%; text-align: center; font-size: 14px"
              >
                {{ $t("加载中...") }}
              </div>
            </el-dropdown-item>
            <div
              v-if="messageList.length === 0"
              style="
                display: flex;
                justify-content: center;
                align-items: center;
                height: 400px;
                color: #aaaaaa;
                flex-direction: column;
              "
            >
              <div>
                <i
                  class="el-icon-document"
                  style="font-size: 46px; margin-bottom: 24px"
                ></i>
              </div>
              <div>
                {{ $t("暂无记录") }}
              </div>
            </div>
          </div>
        </div>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
import {
  getMessage,
  readMessage,
  _getUnreadNotice,
  _getNoticeList,
} from "@/api/im.api";
import { mapActions, mapGetters } from "vuex";
import notifyMp3 from "@/assets/dingdong.mp3";
import i18n from "@/lang";
export default {
  name: "index",
  data() {
    return {
      userClick: false,
      tip: 99,
      messageList: [],
      unreadNoticeTimer: null,
      lock: false,
      page: {
        pageSize: 10,
        pageNum: 1,
        totalElements: -1,
      },
      notifyAudio: null,
      getUnread: null,
      NoticeList: [],
      notifyMp3,
    };
  },
  computed: {
    ...mapGetters(["currentLanguage", "languages", "lang"]),
    messageNumber() {
      return this.$store.getters.messageNumber;
    },
  },
  created() {
    this.startMassageInterval();
    this.getUnprocessedOrderFunction();
    this.notifyAudio = new Audio(this.notifyMp3);
    this.getUnreadNotice(); // ✅ 这行可以保留也可以删
    this.getNoticeList();

    // ✅ 添加轮询调用
    this.startUnreadNoticePolling();
  },
  mounted() {
    let self = this,
      notifyAudio = this.$refs["notifyAudio"];
    document.addEventListener("click", audio);

    function audio() {
      self.userClick = true;
      notifyAudio && (notifyAudio.muted = false);
      document.removeEventListener("click", audio);
    }

    this.$watch("messageNumber", (newVal, oldVal) => {
      if (newVal - 0 > oldVal - 0) {
        //有新消息
        this.userClick && notifyAudio && notifyAudio.play();
      }
    });
  },
  methods: {
    ...mapActions("chat", [
      "startMassageInterval",
      "getMessageNumber",
      "getUnprocessedOrder",
    ]),
    formatI18nContent(content) {
      try {
        let parsed = JSON.parse(content);
        return this.$t(parsed.i18nCode, { value: parsed.value });
      } catch (e) {
        console.warn("解析失败:", content, e);
        return content;
      }
    },
    visibleChange(val) {
      if (!val) {
        this.readMessage();
      } else {
        this.page.pageNum = 1;
        this.getMessage();
      }
    },
    startUnreadNoticePolling() {
      // 先调用一次
      this.getUnreadNotice();

      // 设置定时器，每 60 秒调用一次
      this.unreadNoticeTimer = setInterval(() => {
        this.getUnreadNotice();
      }, 5000); // 60 秒轮询一次
    },
    handleScroll() {
      if (
        this.$refs.projectDropdown.scrollTop >=
        this.$refs.dropdownMenu.clientHeight - 400
      ) {
        this.getMessage();
      }
    },
    getUnreadNotice() {
      _getUnreadNotice().then((res) => {
        this.getUnread = res.data;
      });
    },
    getNoticeList() {
      _getNoticeList().then((res) => {
        console.log("_getNoticeList===>", res.data);
        this.NoticeList = res.data;
      });
    },
    getMessage() {
      if (this.lock) {
        return;
      }
      if (this.page.totalElements === this.messageList.length) {
        return;
      }
      this.lock = !this.lock;
      let options = {
        ...this.page,
        type: 3,
        status: 0,
        module: 0,
      };
      getMessage(options).then((res) => {
        this.lock = !this.lock;
        this.page.pageNum++;
        this.page.totalElements = res.data.totalElements;
        if (this.page.pageNum > 1) {
          this.messageList.push(...this.getElementsI18n(res.data.elements));
        } else {
          this.messageList = this.getElementsI18n(res.data.elements);
        }
      });
    },
    getElementsI18n(list) {
      list.forEach((item) => {
        // i18n支持通配符
        let varInfo = JSON.parse(item.varInfo);
        let varObj = {};
        varInfo.forEach((item, index) => {
          varObj[item.code] = item.value;
        });
        switch (item.title) {
          case "One order finished":
            item.content = this.$t(item.title + " content", varObj);
            break;
          case "Freeze seller because of violation":
            item.content = this.$t(item.title + " content", varObj);
            break;
          case "New Order":
            item.content = this.$t(item.content);
            break;
          case "Order overtime":
            item.content = this.$t(item.content);
            break;
          case "Buyer Consult from Customer Service":
            item.content = this.$t(item.content);
            break;
          case "Your Seller-Credit updated":
            item.content = this.$t(item.title + " content", varObj);
            break;
          case "UnFreeze seller":
            item.content = this.$t(item.title + " content", varObj);
            break;
          case "Withdraw Success Notify":
            item.content = this.$t(item.title + " content", varObj);
            break;
          case "Recharge Pass Notify":
            item.content = this.$t(item.title + " content", varObj);
            break;
          case "Store certification passed":
            item.content = this.$t(item.title + " content", varObj);
            break;
          case "Store authentication failed":
            item.content = this.$t(item.title + " content", varObj);
            break;
          case "Order purchase overtime":
            item.content = this.$t(item.title + " content", varObj);
            break;
        }
        item.title = this.$t(item.title);
      });
      return list;
    },
    readMessage() {
      const batchSize = 10; // 批量提交的大小
      let ids = [];

      this.messageList.forEach((item, index) => {
        if (item.status === 1) {
          ids.push(item.id);

          if (ids.length === batchSize) {
            this.submitReadMessage(ids); // 提交读取消息的方法
            ids = [];
          }
        }
      });

      if (ids.length > 0) {
        this.submitReadMessage(ids); // 提交剩余的未处理的消息
      }

      this.getMessageNumber();
    },

    submitReadMessage(ids) {
      readMessage({ ids: ids.join(",") }).then((res) => {
        this.messageList.forEach((item, index) => {
          if (item.status === 1 && ids.includes(item.id)) {
            item.status = 2;
          }
        });
        this.messageList = [...this.messageList];
      });
    },

    // 轮询查询是否有新的订单
    getUnprocessedOrderFunction() {
      let self = this;
      self.getUnprocessedOrder();
      setInterval(() => {
        self.getUnprocessedOrder();
      }, 60000);
    },
  },
  beforeDestroy() {
    if (this.unreadNoticeTimer) {
      clearInterval(this.unreadNoticeTimer);
    }
  },
};
</script>

<style lang="scss" scoped>
.el-dropdown-menu {
  .el-dropdown-menu__item {
    .message {
      width: 100%;

      .message-item {
        padding: 10px 0;

        .message-title {
          font-size: 14px;
          color: #000000;
          display: flex;
          justify-content: flex-start;
          align-items: center;

          &.active {
            text-indent: 10px;
            display: inline;

            &::before {
              margin-right: 6px;
              display: inline-block;
              width: 10px;
              height: 10px;
              content: " ";
              background-color: #f56c6c;
              border-radius: 10px;
            }
          }
        }

        .message-content {
          font-size: 12px;
          color: #333333;
          line-height: 14px;
        }
      }
    }
  }
}

.bell {
  position: relative;
  right: 6px;
  cursor: pointer;

  .tip {
    display: block;
    width: 16px;
    height: 16px;
    content: " ";
    background-color: #f56c6c;
    border-radius: 100%;
    line-height: 16px;
    font-size: 12px;
    color: #ffffff;
    position: absolute;
    text-align: center;
    top: -5px;
    right: -5px;
    // 缩小字体
    transform: scale(0.9);
  }
}

.horn-wrapper {
  position: relative;

  .icon-container {
    position: relative;
    width: 22px;
    height: 22px;

    img {
      width: 100%;
      height: 100%;
    }

    .tip {
      position: absolute;
      top: -4px;
      right: -4px;
      width: 16px;
      height: 16px;
      background-color: #f56c6c;
      border-radius: 50%;
      font-size: 12px;
      color: #fff;
      text-align: center;
      line-height: 16px;
    }
  }
}

.project-dropdown {
  //设置高度才能显示出滚动条 !important
  height: 400px;
  width: 300px;
  overflow-y: auto;
  overflow-x: hidden;
}

.project-dropdown::-webkit-scrollbar {
  width: 5px;
  height: 5px;
  background-color: #f5f5f5;
}

.project-dropdown::-webkit-scrollbar-track {
  //-webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
  border-radius: 10px;
  background-color: #f5f5f5;
}

.spinner {
  width: 100%;
  flex: 1;
}
</style>
