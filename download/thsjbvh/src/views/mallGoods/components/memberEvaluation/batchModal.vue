<script setup>
import { ref, reactive, watch } from 'vue'
import { ApiFindSellerList, ApiChangedTime } from '@/api/api.js'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
const props = defineProps({
  open: { type: Boolean, default: false }
})
const emits = defineEmits(['update:open','resetList'])

const sellerOptions = ref([])
const formRef = ref()
const formdata = reactive({
  sellerId: null, // 选择店铺
  fromTime: '', // 开始时间
  toTime: '' // 结束时间
})
const confirmLoading = ref(false)

const handleCancel = () => {
  emits('update:open', false)
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (confirmLoading.value) {
    return false
  }
  try {
    await formRef.value.validate()
    const dataRes = await ApiChangedTime(formdata)
    message[dataRes.code == '0' ? 'success' : 'error'](dataRes.msg)
    if(dataRes.code == 0){
      handleCancel()
      emits('resetList')
    }
  } catch (error) {
    console.log(error)
  }
}

const getList = async () => {
  try {
    const { data } = await ApiFindSellerList()
    sellerOptions.value = data ?? []
  } catch (error) {
    console.log(error)
  }
}

watch(
  () => props.open,
  nval => {
    if (nval) {
      getList()
    }
  }
)
</script>
<template>
  <a-modal :open="open" title="一键修改评论时间" centered destroyOnClose @cancel="handleCancel">
    <template #footer>
      <a-button key="back" @click="handleCancel">取消</a-button>
      <a-button key="submit" type="primary" @click="handleSubmit">确定</a-button>
    </template>
    <a-form ref="formRef" layout="vertical" :model="formdata" class="mr-t15">
      <a-form-item label="选择店铺" name="sellerId" :rules="[{ required: true, message: '请选择店铺' }]">
        <a-select
          v-model:value="formdata.sellerId"
          placeholder="请选择店铺"
          :options="sellerOptions"
          :fieldNames="{
            label: 'sellerName',
            value: 'sellerId'
          }"
        />
      </a-form-item>
      <a-form-item label="开始时间" name="fromTime" :rules="[{ required: true, message: '请选择开始时间' }]">
        <a-date-picker
          v-model:value="formdata.fromTime"
          :show-time="{
            defaultValue: dayjs('00:00:00', 'HH:mm:ss')
          }"
          format="YYYY-MM-DD HH:mm:ss"
          valueFormat="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择开始时间"
        />
      </a-form-item>
      <a-form-item label="结束时间" name="toTime" :rules="[{ required: true, message: '请选择结束时间' }]">
        <a-date-picker
          v-model:value="formdata.toTime"
          :show-time="{
            defaultValue: dayjs('00:00:00', 'HH:mm:ss')
          }"
          format="YYYY-MM-DD HH:mm:ss"
          valueFormat="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择结束时间"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
