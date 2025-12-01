<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import pick from 'xe-utils/pick'
import clone from 'xe-utils/clone'
import { message } from 'ant-design-vue'
import { ApiPartySubmitBatch } from '@/api/api.js'

const props = defineProps({
  open: { type: Boolean, default: false },
  shopcart: { type: Array, default: [] },
  totalPrice: { type: Number, default: () => 0 }
})
const emits = defineEmits(['update:open', 'update:shopcart'])

const desc = [
  '1.批量下单:将从系统演示账号的收货人进行下单，下单前请保证有足够的演示账号，否则会导致一个账号重复对一个商品进行下单。',
  '2.最大下单:一分钟仅最人支持1个订单。',
  '3.时间范围:下单最小时间6小时，最大时间30天;'
]

const spinning = ref(false)
const confirmLoad = ref(false)

const handleModalCancel = () => {
  emits('update:open', false)
}

const formRef = ref()
const formdata = reactive({
  amount: 0, // 订单金额
  amountCount: 0, // 订单总金额
  count: 0, // 订单数量
  items: [],
  lang: 'en',
  maxAmount: 0, // 订单最大金额
  secret: '',
  startEnd: '', // 下单结束时间
  startTime: '', // 下单开始时间
  type: 'batch'
})

const handleModalOk = async () => {
  if (confirmLoad.value) {
    return
  }
  confirmLoad.value = true
  try {
    await formRef.value.validate()
    const items = clone(props.shopcart).map(item => ({ count: item.count, sellerGoodsId: item.sellerGoodsId }))
    const dataRes = await ApiPartySubmitBatch({ ...formdata, items })
    message[dataRes.success === 200 ? 'success' : 'error'](dataRes.success === 200 ? dataRes.returnMsg : dataRes.errorCode + ':' + dataRes.errorMsg)
    if (dataRes.success === 200) {
      handleModalCancel()
      emits('update:shopcart', [])
    }
  } catch (error) {
    console.log(error)
  } finally {
    confirmLoad.value = false
  }
}

watch(
  () => props.open,
  nval => {
    if (nval) {
      formdata.amount = props.shopcart.reduce((min, curr) => (min.systemPrice < curr.systemPrice ? min.systemPrice : curr.systemPrice), 0)
      formdata.amountCount = parseFloat(props.totalPrice.toFixed(2))
    }
  },
  { immediate: true }
)
</script>
<template>
  <a-modal :open="open" title="确认订单" centered destroyOnClose @cancel="handleModalCancel" width="750px">
    <template #footer>
      <a-button key="back" @click="handleModalCancel">取消</a-button>
      <a-button key="submit" type="primary" :loading="spinning || confirmLoad" @click="handleModalOk">确认支付</a-button>
    </template>
    <a-row :wrap="false" :gutter="8">
      <a-col flex="auto">
        <a-card size="small" title="商品信息" :bodyStyle="{ padding: '0' }">
          <div style="height: 240px; overflow: auto">
            <a-list :data-source="shopcart" item-layout="horizontal">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #avatar>
                      <a-avatar shape="square" :size="40" :src="item.imgUrl" />
                    </template>
                    <template #title>
                      <div class="goods-name">{{ item.goodName }}</div>
                    </template>
                    <template #description>
                      <a-flex gap="middle" justify="space-between" align="center">
                        <div>$ {{ item.systemPrice }}</div>
                        <div>x {{ item.count }}</div>
                      </a-flex>
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </div>
        </a-card>
      </a-col>
      <a-col flex="280px">
        <a-card size="small" title="下单备注">
          <ul style="list-style: none; padding-left: 0">
            <li v-for="(item, idx) in desc" :key="idx" class="mr-b5">{{ item }}</li>
          </ul>
        </a-card>
      </a-col>
    </a-row>
    <a-card class="mr-t15">
      <a-form ref="formRef" :model="formdata">
        <a-form-item required label="下单时间范围" class="mr-b0">
          <a-row :gutter="12">
            <a-col :span="12">
              <a-form-item name="startTime" :rules="[{ required: true, message: '请选择开始时间' }]">
                <a-date-picker
                  show-time
                  v-model:value="formdata.startTime"
                  placeholder="开始时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  valueFormat="YYYY-MM-DD HH:mm:ss"
                  class="w100pre"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item name="startEnd" :rules="[{ required: true, message: '请选择结束时间' }]">
                <a-date-picker
                  show-time
                  v-model:value="formdata.startEnd"
                  placeholder="结束时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  valueFormat="YYYY-MM-DD HH:mm:ss"
                  class="w100pre"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item label="订单数量" name="count" :rules="[{ required: true, message: '请输入订单数量' }]">
          <a-input-number v-model:value="formdata.count" :min="0" class="w100pre">
            <template #addonAfter>最大订单数量0</template>
          </a-input-number>
        </a-form-item>
        <a-form-item label="单笔订单金额" class="mr-b0">
          <a-row :gutter="12">
            <a-col :span="12">
              <a-form-item name="amount">
                <a-input-number disabled v-model:value="formdata.amount" :min="0" placeholder="" class="w100pre" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item name="maxAmount" :rules="[{ required: true, message: '请输入最大金额' }]">
                <a-input-number v-model:value="formdata.maxAmount" :min="0" placeholder="最大金额" class="w100pre" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item label="订单总金额" name="amountCount">
          <a-input-number disabled v-model:value="formdata.amountCount" :min="0" placeholder="订单总金额" class="w100pre" />
        </a-form-item>
      </a-form>
    </a-card>
  </a-modal>
</template>
