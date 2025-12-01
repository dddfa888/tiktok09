<template>
  <div class="page-main-content">
    <fx-header :fixed="true" style="width: 100%; height: 50px">
      <template #title>
        {{ $t('通知') }}
      </template>
    </fx-header>
    <div class="notice-list">
      <div class="van-card" v-for="item in noticeList" :key="item.id">
        <div>{{ $t(item.title) }}</div>
        <div>{{ item.createTime }}</div>
        <div v-if="item.tipCode === 'singe'">
          {{ formatI18nContent(item.content) }}
        </div>
        <div v-else-if="item.tipCode === 'none'">
          {{ $t(item.content) }}
        </div>
        <div v-else-if="item.tipCode === 'customize'">
          {{ item.content }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, nextTick, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { _getUnreadNotice, _getNoticeList } from '@/service/user.api.js'

export default defineComponent({
  name: 'MessageList',
  setup() {
    const { t } = useI18n()
    const noticeList = ref([])

    const formatI18nContent = (content) => {
      try {
        const parsed = JSON.parse(content)
        // this.$t("customer_service_time", {
        //         start: res.data.start,
        //         end: res.data.end,
        //       })
        return t(parsed.i18nCode, { value: parsed.value })
      } catch (e) {
        console.warn('解析失败:', content, e)
        return content
      }
    }

    _getNoticeList().then((res) => {
      console.log('_getNoticeList===>', res)
      noticeList.value = res
    })

    return {
      noticeList,
      formatI18nContent,
      t
    }
  }
})
</script>

<style lang="scss" scoped>
.van-nav-bar__content {
  width: 100%;
  height: 50px;
}

.van-card {
  width: 100%;
  height: 80px;
}
</style>
