<script setup>
import pick from 'xe-utils/pick'
import { ref, reactive, computed, watch } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { ApiAddPrize, ApiPrizeInfo, ApiUpdPrize, ApiUploadFile } from '@/api/api.js'
import uploadImg from '@/components/uploadImg/uploadImg.vue'

defineOptions({ name: 'modalForm' })

const props = defineProps({
  open: { type: Boolean, default: () => false },
  id: { type: [String, Number], default: () => null }
})
const emits = defineEmits(['update:open', 'resetList'])
const title = computed(() => (props.id ? '编辑奖品' : '新增奖品'))

const loading = ref(false)
const confirmLoading = ref(false)
const prizeType = [
  { label: '实物', value: 1 },
  { label: '彩金', value: 2 },
  { label: '谢谢惠顾', value: 3 }
]
const langOptions = [
  { label: '英文', value: 'en' },
  { label: '中文', value: 'cn' }
]
const formRef = ref()
const formdata = reactive({
  lang: 'en',
  prizeNameEn: '', // 奖品名称en
  prizeNameCn: '', // 奖品名称cn
  image: '', // 奖品图片
  // prizeAmount: 0, // 奖品价值
  defaultPrize: 0, // 奖品价值
  prizeType: null, // 彩金类型
  remark: '' // 备注
  //
  // activityId: '',
  // defaultPrize: 0,
  // deleted: 0,
  // leftQuantity: 0,
  // maxQuantity: 0,
  // odds: 0,
  // poolId: '',
  // prizeNameEn: '',
  // status: 0
})
const fileList = ref([])

const handleCancel = () => {
  emits('update:open', false)
  formRef.value.resetFields()
}

const handleSubmit = async () => {
  if (confirmLoading.value) {
    return
  }
  confirmLoading.value = true
  try {
    await formRef.value.validate()
    let dataRes = null
    if (!props.id) {
      dataRes = await ApiAddPrize({ ...formdata })
    } else {
      dataRes = await ApiUpdPrize({ uuid: props.id, ...formdata })
    }
    message[dataRes.success === 200 ? 'success' : 'error'](dataRes.success === 200 ? dataRes.returnMsg : dataRes.errorMsg + ':' + dataRes.errorCode)
    if (dataRes.success === 200) {
      handleCancel()
      emits('resetList')
    }
  } catch (error) {
    console.log(error)
  } finally {
    confirmLoading.value = false
  }
}

const getInfo = async () => {
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    const dataRes = await ApiPrizeInfo(props.id)
    if (dataRes.success === 200) {
      Object.assign(formdata, pick(dataRes.data, Object.keys(formdata)))
    }
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
}

watch(
  [() => props.open, () => props.id],
  nval => {
    if (nval[0] && nval[1]) {
      getInfo()
    }
  },
  { deep: true }
)
</script>
<template>
  <a-modal
    :open="open"
    :title="title"
    centered
    :confirm-loading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    okText="提交"
    cancelText="取消"
    destroyOnClose
  >
    <a-form ref="formRef" :model="formdata" label-width="90px" layout="vertical">
      <a-form-item label="语言" name="lang">
        <a-radio-group
          v-model:value="formdata.lang"
          :options="langOptions"
          @change="
            () => {
              formdata.prizeNameCn = ''
              formdata.prizeNameEn = ''
            }
          "
        ></a-radio-group>
      </a-form-item>
      <template v-if="formdata.lang === 'en' || !formdata.lang">
        <a-form-item label="奖品名称" name="prizeNameEn" :rules="[{ required: true, message: '请输入奖品名称' }]">
          <a-input v-model:value="formdata.prizeNameEn" placeholder="请输入奖品英文名称" />
        </a-form-item>
      </template>
      <template v-else>
        <a-form-item label="奖品名称" name="prizeNameCn" :rules="[{ required: true, message: '请输入奖品名称' }]">
          <a-input v-model:value="formdata.prizeNameCn" placeholder="请输入奖品中文名称" />
        </a-form-item>
      </template>
      <a-form-item label="奖品图片 (尺寸建议200x200)" name="image" :rules="[{ required: true, message: '请上传奖品图片' }]">
        <uploadImg v-model:value="formdata.image" accept="image/*" :maxCount="1" />
      </a-form-item>
      <a-form-item label="奖品价值" name="defaultPrize" :rules="[{ required: true, message: '请输入奖品价值' }]">
        <a-input-number v-model:value="formdata.defaultPrize" :min="0" placeholder="请输入奖品价值" class="w100pre" />
      </a-form-item>
      <a-form-item label="奖品类型" name="prizeType" :rules="[{ required: true, message: '请选择奖品类型' }]">
        <a-select v-model:value="formdata.prizeType" :options="prizeType" placeholder="请选择奖品类型"></a-select>
      </a-form-item>
      <a-form-item label="备注" name="remark">
        <a-textarea v-model:value="formdata.remark" placeholder="请输入备注" :auto-size="{ minRows: 2, maxRows: 5 }"></a-textarea>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
