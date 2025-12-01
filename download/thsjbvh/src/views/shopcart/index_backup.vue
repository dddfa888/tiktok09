
<script setup>
import { ApiGetGoods, ApiPartySubmit } from '@/api/api.js'
import { ref, reactive, h, onMounted, computed } from 'vue'
import { DeleteOutlined } from '@ant-design/icons-vue'
import GoodsItem from './components/goodsItem.vue'

const fallbackImg =
  'data:image/png;base64,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'
const spinning = ref(false)
// 查询数据
const findForm = reactive({ goodId: '', updateStatus: 0 })
// 分页数据
const pagin = reactive({ pageNum: 0, pageSize: 10 })
const lists = ref([])
const listtotal = ref(0)

const formdata = reactive({
  partyId: 1,
  useraddresId: 1,
  items: [],
  remark: ''
})

const shopcart = ref([]) // 购物车
// 添加商品到购物车
const handleAddGood = item => {
  // 1、判断购物车数组中是否存在相同的商品id,如果有就+1,如果没有就push进去
  // 2、创建一个临时对象存选中商品信息和默认数量为1
  const has = shopcart.value.some(good => good.goodsId === item.goodsId)
  if (has) {
    const idx = shopcart.value.findIndex(good => good.goodsId === item.goodsId)
    shopcart.value[idx].count++
  } else {
    const good = {
      goodsId: item.goodsId,
      imgUrl: item.imgUrl1,
      systemPrice: item.systemPrice,
      cnTitle: item.cnTitle,
      count: 1
    }
    shopcart.value.push(good)
  }
}

// 删除商品
const handleRemove = gid => {
  shopcart.value = shopcart.value.filter(good => good.goodsId !== gid)
}

// 计算总价
const totalPrice = computed(() => {
  return shopcart.value.reduce((pre, cur) => {
    return pre + cur.count * cur.systemPrice
  }, 0)
})

// 本页全选功能
const handleAllSelected = () => {
  const all = lists.value.map(item => ({
    goodsId: item.goodsId,
    imgUrl: item.imgUrl || '',
    systemPrice: item.systemPrice || '',
    goodsName: item.goodsName || '',
    count: 1
  }))
  shopcart.value = [...all]
}

const modalOpen = ref(false) // 确认下单模态框
const confirmLoad = ref(false) // 确认下单按钮loading
// 模态框确认按钮
const handleModalOk = async () => {
  if (confirmLoad.value) {
    return
  }
  confirmLoad.value = true
  try {
    const dataRes = await ApiPartySubmit()
    console.log(dataRes)
  } catch (error) {
    console.log(error)
  } finally {
    confirmLoad.value = false
  }
}

// 模态框取消按钮
const handleModalCancel = () => {}

// 获取商品列表
const getLists = async () => {
  if (spinning.value) {
    return
  }
  spinning.value = true
  try {
    const dataRes = await ApiGetGoods({ ...findForm, ...pagin })
    if (dataRes.success === 200) {
      const { records, total, current } = dataRes.data
      lists.value = records ?? []
      listtotal.value = total || 0
      Object.assign(pagin, { pageNum: current })
    }
  } catch (error) {
    console.log(error)
  } finally {
    spinning.value = false
  }
}

onMounted(() => {
  getLists()
})
</script>
<template>
  <div class="app-container">
    <a-row :wrap="false" :gutter="12">
      <a-col flex="70%">
        <a-flex gap="12" vertical>
          <h4>查询条件</h4>
          <a-row :gutter="10">
            <a-col :span="6">
              <a-form-item class="mr-b0">
                <a-input placeholder="请输入关键字" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item class="mr-b0">
                <a-input placeholder="请输入卖家id" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item class="mr-b0">
                <a-cascader placeholder="请选择分类"></a-cascader>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item class="mr-b0">
                <a-cascader placeholder="请选择分类"></a-cascader>
              </a-form-item>
            </a-col>
          </a-row>
          <a-space>
            <a-button type="primary" :loading="spinning" @click="handleAllSelected">本页全选</a-button>
          </a-space>
          <div class="main">
            <a-row :gutter="[12, 12]">
              <a-col flex="192px" v-for="item in lists" :key="item.id">
                <GoodsItem :item="item" @add-good="handleAddGood" />
              </a-col>
            </a-row>
          </div>
          <div class="flex justify-center" style="width: 100%; margin-top: 12px">
            <a-pagination
              v-model:current="pagin.pageNum"
              v-model:page-size="pagin.pageSize"
              showQuickJumper
              showSizeChanger
              :pageSizeOptions="['10', '20', '30', '50']"
              :total="listtotal"
              :show-total="total => `Total ${total} items`"
              @change="
                page => {
                  pagin.pageNum = page
                  getLists()
                }
              "
              @showSizeChange="
                (current, size) => {
                  pagin.pageSize = size
                  pagin.pageNum = 1
                  getLists()
                }
              "
            />
          </div>
        </a-flex>
      </a-col>
      <a-col flex="30%">auto with no-wrap</a-col>
    </a-row>
  </div>
</template>
<style scoped>
.main {
  flex: 1;
}
</style>
