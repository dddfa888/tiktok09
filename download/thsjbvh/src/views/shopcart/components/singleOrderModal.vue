<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { ApiGetUserAddrList, ApiPartySubmit } from '@/api/api.js'
import pick from 'xe-utils/pick'
import clone from 'xe-utils/clone'
import { message } from 'ant-design-vue'

const props = defineProps({
  open: { type: Boolean, default: false },
  shopcart: { type: Array, default: () => [] },
  totalPrice: { type: Number, default: () => 0 }
})
const emits = defineEmits(['update:open', 'update:shopcart'])

const spinning = ref(false)

const formRef = ref()
const formdata = reactive({
  addressId: null,
  items: [],
  lang: 'en',
  partyId: 'ff80808186e078b70186e0807b090008',
  secret: '',
  type: 'single'
})
const userinfo = reactive({
  contacts: '', // 联系人
  email: '', // 邮件
  address: '', // 地址
  country: '', //国家
  province: '', // 州
  postcode: '', // 邮政编码
  phone: '' // 联系电话
})

const userList = ref([])

const handleModalCancel = () => {
  emits('update:open', false)
  formRef.value.resetFields()
  Object.assign(userinfo, { contacts: '', email: '', address: '', country: '', province: '', postcode: '', phone: '' })
}

const confirmLoad = ref(false) // 确认下单按钮loading
// 模态框确认按钮
const handleModalOk = async () => {
  if (confirmLoad.value) {
    return
  }
  confirmLoad.value = true
  try {
    await formRef.value.validate()
    const items = clone(props.shopcart).map(item => ({ count: item.count, sellerGoodsId: item.sellerGoodsId }))
    const dataRes = await ApiPartySubmit({ ...formdata, items })
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

const getUserList = async () => {
  if (spinning.value) {
    return
  }
  spinning.value = true
  try {
    const { data } = await ApiGetUserAddrList()
    userList.value = data ?? []
  } catch (error) {
    console.log(error)
  } finally {
    spinning.value = false
  }
}

const handleUserChange = (value, option) => {
  // formdata.addressId = value
  Object.assign(userinfo, pick(option, Object.keys(userinfo)))
}

watch(
  () => props.open,
  nval => {
    if (nval) {
      getUserList()
    }
  }
)
</script>
<template>
  <a-modal :open="open" title="确认订单" centered destroyOnClose @cancel="handleModalCancel" width="750px">
    <template #footer>
      <a-flex gap="middle" justify="space-between" align="center">
        <div>
          合计：$ <strong>{{ totalPrice.toFixed(2) }}</strong>
        </div>
        <div>
          <a-button key="back" @click="handleModalCancel">取消</a-button>
          <a-button key="submit" type="primary" :loading="spinning || confirmLoad" @click="handleModalOk">确认支付</a-button>
        </div>
      </a-flex>
    </template>
    <a-row :wrap="false" :gutter="8">
      <a-col flex="auto">
        <a-card size="small" title="商品信息" :bodyStyle="{ padding: '0' }">
          <div style="height: 420px; overflow: auto">
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
        <a-card size="small" title="顾客信息">
          <a-spin :spinning="spinning">
            <a-form ref="formRef" :model="formdata">
              <a-form-item name="addressId" :rules="[{ required: true, message: '请选择顾客' }]" class="mr-b0">
                <a-select
                  v-model:value="formdata.addressId"
                  show-search
                  :options="userList"
                  :field-names="{ label: 'email', value: 'uuid' }"
                  :filter-option="(input, option) => option.email.toLowerCase().indexOf(input.toLowerCase()) >= 0"
                  placeholder="请选择顾客信息"
                  allowClear
                  @change="handleUserChange"
                >
                </a-select>
              </a-form-item>
            </a-form>
            <a-form label-width="80px">
              <a-form-item class="mr-b0" label="联系人">
                <span class="ant-form-text">{{ userinfo.contacts }}</span>
              </a-form-item>
              <a-form-item class="mr-b0" label="邮件">
                <span class="ant-form-text">{{ userinfo.email }}</span>
              </a-form-item>
              <a-form-item class="mr-b0" label="地址">
                <span class="ant-form-text">{{ userinfo.address }}</span>
              </a-form-item>
              <a-form-item class="mr-b0" label="国家">
                <span class="ant-form-text">{{ userinfo.country }}</span>
              </a-form-item>
              <a-form-item class="mr-b0" label="州">
                <span class="ant-form-text">{{ userinfo.province }}</span>
              </a-form-item>
              <a-form-item class="mr-b0" label="邮政编码">
                <span class="ant-form-text">{{ userinfo.postcode }}</span>
              </a-form-item>
              <a-form-item class="mr-b0" label="联系电话">
                <span class="ant-form-text">{{ userinfo.phone }}</span>
              </a-form-item>
            </a-form>
          </a-spin>
        </a-card>
      </a-col>
    </a-row>
  </a-modal>
</template>
