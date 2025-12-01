<template>
  <div class="chat-mask" @click.self="close">
    <div class="chat-box">
      <!-- 顶部 -->
      <div class="chat-header">
        <span>在线客服</span>
        <span class="chat-close" @click="close">×</span>
      </div>

      <!-- 聊天内容 -->
      <div class="chat-content" ref="content">
        <div
          v-for="(msg, i) in messages"
          :key="i"
          :class="['msg-row', msg.from]"
        >
          <img
            class="msg-avatar"
            :src="msg.from === 'me' ? myAvatar : otherAvatar"
            alt="avatar"
          />
          <div class="msg-bubble">
            <div class="msg-time" v-if="msg.time">{{ msg.time }}</div>
            {{ msg.content }}
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <input
          v-model="input"
          type="text"
          placeholder="请输入内容"
          @keyup.enter="sendText"
        />
        <button @click="sendText">发送</button>
      </div>
    </div>
  </div>
</template>

<script>
import { _getMsg, _sendMsg } from "@/API/im.api";

export default {
  data() {
    return {
      input: "",
      messages: [],
      lastMessageId: "",
      pollTimer: null,
      myAvatar: require("../../../assets/image/4.c2ab5226.png"), // 自己头像
      otherAvatar: require("../../../assets/image/live.790f27c3.png"), // 客服头像
    };
  },
  created() {
    this.fetchMessages();
    this.startPolling();
  },
  beforeDestroy() {
    this.stopPolling();
  },
  methods: {
    startPolling() {
      this.pollTimer = setInterval(() => {
        this.fetchMessages();
      }, 1000);
    },
    stopPolling() {
      clearInterval(this.pollTimer);
      this.pollTimer = null;
    },
    generateUUID() {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
        const r = Math.random() * 16 | 0
        const v = c === 'x' ? r : (r & 0x3 | 0x8)
        return v.toString(16)
      })
    },
    fetchMessages() {
    let id=localStorage.getItem("anonymousId");
    if(!id){
      id=generateUUID()
      localStorage.setItem("anonymousId",id)
    }
      _getMsg({ message_id: this.lastMessageId, show_img: true,anonymousId:id }).then(
        (res) => {
          if (Array.isArray(res) && res.length) {
            const existingIds = new Set(this.messages.map((m) => m.id));
            const newMessages = res
              .filter((msg) => !existingIds.has(msg.id))
              .map((msg) => ({
                from: msg.send_receive === "send" ? "me" : "other",
                type: msg.type,
                content: msg.content,
                fileName: msg.fileName || "",
                time: msg.createtime,
                id: msg.id,
              }));

            if (newMessages.length > 0) {
              this.messages.push(...newMessages);
              this.lastMessageId = newMessages[newMessages.length - 1].id;
              this.$nextTick(this.scrollToBottom);
            }
          }
        }
      );
    },
    sendText() {
      if (!this.input.trim()) return;

      const message = {
        from: "me",
        type: "text",
        content: this.input,
        time: new Date().toLocaleTimeString(),
        id: `local-${Date.now()}`,
      };

      this.messages.push(message);

      _sendMsg({ type: "text", content: this.input })
        .then((res) => {
          console.log("发送成功：", res);
        })
        .catch((err) => {
          console.warn("消息发送失败：", err);
        });

      this.input = "";
      this.$nextTick(this.scrollToBottom);
    },
    scrollToBottom() {
      const el = this.$refs.content;
      if (el) el.scrollTop = el.scrollHeight;
    },
    close() {
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
.chat-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
  z-index: 999;
}
.chat-box {
  width: 400px;
  height: 520px;
  background: #fff;
  display: flex;
  flex-direction: column;
  border-radius: 10px 10px 0 0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.2);
  margin-right: 20px;
  margin-bottom: 20px;
}
.chat-header {
  padding: 12px;
  background: #409eff;
  color: white;
  display: flex;
  justify-content: space-between;
}
.chat-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: #f6f6f6;
}

/* 消息行结构 */
.msg-row {
  display: flex;
  align-items: flex-start;
  margin: 10px 0;
}
.msg-row.me {
  flex-direction: row-reverse;
}
.msg-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin: 0 8px;
}
.msg-bubble {
  max-width: 70%;
  background: #e2e3e5;
  padding: 8px 12px;
  border-radius: 8px;
  word-break: break-word;
}
.msg-row.me .msg-bubble {
  background: #d1ecf1;
}
.msg-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

/* 输入区域 */
.chat-input-area {
  display: flex;
  padding: 8px;
  border-top: 1px solid #eee;
  gap: 6px;
}
.chat-input-area input[type="text"] {
  flex: 1;
  padding: 6px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>
