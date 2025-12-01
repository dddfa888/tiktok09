<script setup>
import { reactive, ref, shallowReactive } from 'vue'
import { ApiCgstatusMemberComment } from '@/api/api.js'
import { message } from 'ant-design-vue'
const props = defineProps({
  open: { type: Boolean, default: false },
  info: { type: Object, default: {} }
})
const emits = defineEmits(['update:open', 'update:info'])

const form = reactive({
  status: 0
})
const loading = ref(false)

const handleChangeStatus = async value => {
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    const dataRes = await ApiCgstatusMemberComment(value == 0 ? 'open' : 'hide', { id: props.info.id })
    message[dataRes.code == 0 ? 'success' : 'error'](dataRes.msg)
    emits('update:info', { ...props.info, status: value })
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  emits('update:open', false)
}
const handleSubmit = async () => {}
</script>
<template>
  <a-modal :open="open" title="用户评价详情" centered destroyOnClose @cancel="handleCancel">
    <template #footer>
      <a-button key="cancel" @click="handleCancel"> 取消 </a-button>
      <a-button key="submit" type="primary" @click="handleCancel"> 确定 </a-button>
    </template>
    <a-flex gap="small" vertical>
      <a-row :wrap="false" :gutter="12">
        <a-col flex="auto">
          <a-descriptions title="店铺信息" :column="1">
            <a-descriptions-item label="店铺名称">{{ info.sellerName }}</a-descriptions-item>
            <a-descriptions-item label="订单标号">{{ info.orderId }}</a-descriptions-item>
            <a-descriptions-item label="商品名称">{{ info.goodsVo?.name || '' }}</a-descriptions-item>
          </a-descriptions>
        </a-col>
        <a-col flex="80px">
          <a-avatar shape="square" :size="64"> LOGO </a-avatar>
        </a-col>
      </a-row>
      <a-descriptions title="用户评价" :column="1">
        <a-descriptions-item label="">{{ info.userName }}</a-descriptions-item>
      </a-descriptions>
      <a-alert type="info" message="评价内容">
        <template #description>
          <p>
            {{ info.content || '' }}
          </p>
        </template>
      </a-alert>
      <a-form ref="formRef" :model="form">
        <a-form-item label="前端展示" class="mr-b0">
          <a-switch v-model:checked="info.status" :loading="loading" :unCheckedValue="1" :checkedValue="0" @change="handleChangeStatus" />
        </a-form-item>
      </a-form>
    </a-flex>
  </a-modal>
</template>
