<script setup lang="jsx">
import { ref, reactive, computed, watch } from 'vue'
import { ApiAddActivity, ApiActivityInfo, ApiEditActivity } from '@/api/api.js'
import { message } from 'ant-design-vue'
import prizeComponent from './prizeCom.vue'
import pick from 'xe-utils/pick'

defineOptions({ name: 'modalForm' })

const props = defineProps({
  open: { type: Boolean, default: () => false },
  id: { type: [String, Number], default: () => null }
})
const emits = defineEmits(['update:open', 'resetList'])

const spinning = ref(false)
const title = computed(() => (props.id ? '编辑活动' : '新增活动'))
const formRef = ref()
const formdata = reactive({
  titleCn: '', // 活动标题
  type: 'simple_lottery', //活动类型
  startTime: '', //开始时间
  endTime: '', //结束时间
  descriptionCn: '', //活动介绍
  prizeList: [], // 奖项配置
  activityConfig: [
    { code: 'minReceiveMoneyThreshold', title: '活动最小领取彩金', value: '', description: '累计最小领取彩金' },
    { code: 'scoreExchangeLotteryTimeRatio', title: '积分兑换抽奖次数', value: '', description: '积分，兑换一次抽奖' }
  ],
  status: 0 // 状态
})

const handleCancel = () => {
  emits('update:open', false)
  formRef.value.resetFields()
}
const confirmLoading = ref(false)
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    let dataRes = null
    if (!props.id) {
      const params = {
        ...formdata,
        activityConfig: JSON.stringify(formdata.activityConfig)
      }
      dataRes = await ApiAddActivity(params)
    } else {
      const params = {
        uuid: props.id,
        ...formdata,
        activityConfig: JSON.stringify(formdata.activityConfig)
      }
      dataRes = await ApiEditActivity(params)
    }
    message[dataRes.success === 200 ? 'success' : 'error'](dataRes.success === 200 ? dataRes.returnMsg : dataRes.errorMsg + ':' + dataRes.errorCode)
    if (dataRes.success === 200) {
      handleCancel()
      emits('resetList')
    }
  } catch (error) {
    console.log(error)
  }
}

const getInfo = async () => {
  if (spinning.value) {
    return
  }
  spinning.value = true
  try {
    const dataRes = await ApiActivityInfo(props.id)
    if (dataRes.success === 200) {
      setTimeout(() => {
        Object.assign(formdata, pick(dataRes.data, Object.keys(formdata)))
        formdata.activityConfig = JSON.parse(dataRes.data.activityConfig)
        let prizeList = formdata.prizeList ?? []
        let plist = dataRes.data.prizeList ?? []
        plist.forEach(item => {
          let idx = prizeList.findIndex(e => e.uuid === item.uuid)
          if (idx > -1) {
            prizeList[idx] = item
          }
        })
        spinning.value = false
      }, 700)
    } else {
      spinning.value = false
      message.error(dataRes.errorMsg + ':' + dataRes.errorCode)
    }
  } catch (error) {
    console.log(error)
    spinning.value = false
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
  <a-modal :open="open" :title="title" width="680px" destroyOnClose :confirm-loading="confirmLoading" @cancel="handleCancel">
    <template #footer>
      <a-button @click="handleCancel">取消</a-button>
      <!-- <a-button @click="() => formRef.resetFields()">重置</a-button> -->
      <a-button type="primary" @click="handleSubmit">提交保存</a-button>
    </template>
    <a-spin :spinning="spinning">
      <a-form ref="formRef" :model="formdata">
        <a-form-item label="活动标题" name="titleCn" :rules="[{ required: true, message: '请输入活动标题' }]">
          <a-input v-model:value="formdata.titleCn" placeholder="请输入活动标题" />
        </a-form-item>
        <a-form-item label="活动类型" name="type" :rules="[{ required: true, message: '请选择活动类型' }]">
          <a-select disabled v-model:value="formdata.type" placeholder="请选择活动类型">
            <a-select-option value="simple_lottery">抽奖活动</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="活动时间" required>
          <a-row align="middle" :gutter="8" :wrap="false">
            <a-col :span="12">
              <a-form-item name="startTime" :rules="[{ required: true, message: '请选择开始时间' }]" class="mr-b0">
                <a-date-picker
                  v-model:value="formdata.startTime"
                  placeholder="开始时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  valueFormat="YYYY-MM-DD HH:mm:ss"
                  show-time
                  class="w100pre"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item name="endTime" :rules="[{ required: true, message: '请选择结束时间' }]" class="mr-b0">
                <a-date-picker
                  v-model:value="formdata.endTime"
                  placeholder="结束时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  valueFormat="YYYY-MM-DD HH:mm:ss"
                  show-time
                  class="w100pre"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item
          label="活动介绍"
          name="descriptionCn"
          :rules="[
            { required: true, message: '请输入活动介绍' },
            { min: 6, message: '请输入至少6个字' }
          ]"
        >
          <a-textarea v-model:value="formdata.descriptionCn" placeholder="请输入活动介绍" :auto-size="{ minRows: 2, maxRows: 5 }" />
        </a-form-item>
        <a-form-item label="奖项配置" name="prizeList" :rules="[{ required: true, type: 'array', message: '请选择奖项配置' }]">
          <prizeComponent v-model:value="formdata.prizeList" />
        </a-form-item>
        <a-form-item label="活动最小领取彩金" :name="['activityConfig', 0, 'value']" :rules="[{ required: true, message: '请输入活动最小领取彩金' }]">
          <a-input v-model:value="formdata.activityConfig[0].value" placeholder="请输入活动最小领取彩金" />
        </a-form-item>
        <a-form-item label="积分兑换抽奖次数" :name="['activityConfig', 1, 'value']" :rules="[{ required: true, message: '请输入积分兑换抽奖次数' }]">
          <a-input v-model:value="formdata.activityConfig[1].value" placeholder="请输入积分兑换抽奖次数" />
        </a-form-item>
        <a-form-item label="活动状态" name="status" :rules="[{ required: true, message: '请选择活动状态' }]">
          <a-radio-group v-model:value="formdata.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>
