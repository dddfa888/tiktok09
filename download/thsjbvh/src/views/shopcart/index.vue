<script setup>
import { ApiGetMallCategory, ApiGetMallSellerList, ApiGetMallSellerGoods, ApiPartySubmit } from '@/api/api.js'
import { ref, reactive, h, onMounted, computed } from 'vue'
import { DeleteOutlined } from '@ant-design/icons-vue'
import GoodsItem from './components/goodsItem.vue'
import singleOrderModal from './components/singleOrderModal.vue'
import bulkOrderModal from './components/bulkOrderModal.vue'

const fallbackImg =
  'data:image/png;base64,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'
const spinning = ref(false)
// 查询数据
const findForm = reactive({
  lang: 'en',
  goodName: '',
  sellerId1: '',
  sellerId2: null,
  categoryId: [null]
})
// 分页数据
const pagin = reactive({ pageNum: 1, pageSize: 10 })
const lists = ref([])
const listtotal = ref(0)

// 查询条件字段处理
const ctFindFields = () => {
  const parameter = {
    ...findForm,
    sellerId: findForm.sellerId1 || findForm.sellerId2 || ''
  }
  if (Array.isArray(findForm.categoryId) && findForm.categoryId.every(cat => cat !== null)) {
    parameter.categoryId = findForm.categoryId
  } else {
    parameter.categoryId = undefined
  }
  delete parameter.sellerId1
  delete parameter.sellerId2
  return parameter
}

const handleFind = () => {
  pagin.pageNum = 1
  getLists(ctFindFields())
}

const category = ref([])
const mallSeller = ref([])

const shopcart = ref([]) // 购物车
// 添加商品到购物车
const handleAddGood = item => {
  // 1、判断购物车数组中是否存在相同的商品id,如果有就+1,如果没有就push进去
  // 2、创建一个临时对象存选中商品信息和默认数量为1
  const has = shopcart.value.some(good => good.sellerGoodsId === item.sellerGoodsId)
  if (has) {
    const idx = shopcart.value.findIndex(good => good.sellerGoodsId === item.sellerGoodsId)
    shopcart.value[idx].count++
  } else {
    shopcart.value.push({ ...item, count: 1 })
  }
}

// 删除商品
const handleRemove = gid => {
  shopcart.value = shopcart.value.filter(good => good.goodsId !== gid)
}

// 计算总价
const totalPrice = computed(() => {
  return shopcart.value.reduce((pre, cur) => pre + cur.count * cur.systemPrice, 0)
})

// 本页全选功能
const handleAllSelected = () => {
  const all = lists.value.map(item => ({
    ...item,
    count: 1
  }))
  shopcart.value = [...all]
}

const bulkOrderModalOpen = ref(false)
const singleOrderModalOpen = ref(false)

// 获取商品分类
const getCategory = async () => {
  try {
    const { data } = await ApiGetMallCategory()
    if (data && data.length) {
      const copiedData = data.slice()
      copiedData.unshift({ name: '全部', uuid: null, children: [] })
      copiedData.forEach(element => {
        if (element.children && element.children.length) {
          element.children.unshift({ name: '无二级分类', uuid: '0', children: [] })
        }
      })
      category.value = copiedData
    } else {
      category.value = []
    }
  } catch (error) {
    console.log(error)
  }
}
// 获取商城卖家
const getMallSeller = async () => {
  try {
    const { data } = await ApiGetMallSellerList()
    mallSeller.value = data ?? []
  } catch (error) {
    console.log(error)
  }
}
// 获取商品列表
const getLists = async (params = {}) => {
  if (spinning.value) {
    return
  }
  spinning.value = true
  try {
    const dataRes = await ApiGetMallSellerGoods(params)
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
  getMallSeller()
  getCategory()
  getLists(ctFindFields())
})
</script>
<template>
  <div class="goods">
    <div class="goods-left">
      <div class="title-name">查询条件</div>
      <a-flex gap="middle" vertical>
        <a-flex gap="middle" vertical>
          <a-form :model="findForm">
            <a-row :gutter="10">
              <a-col :span="6">
                <a-form-item name="goodName">
                  <a-input v-model:value="findForm.goodName" placeholder="请输入关键字" allowClear />
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item name="sellerId1">
                  <a-input v-model:value="findForm.sellerId1" placeholder="卖家id" allowClear />
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item name="sellerId2">
                  <a-select
                    v-model:value="findForm.sellerId2"
                    :options="mallSeller"
                    :field-names="{ label: 'name', value: 'uuid' }"
                    placeholder="选择卖家"
                    allowClear
                  />
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item name="categoryId">
                  <a-cascader
                    v-model:value="findForm.categoryId"
                    placeholder="请选择商品分类"
                    allowClear
                    :options="category"
                    :field-names="{ label: 'name', value: 'uuid', children: 'children' }"
                    changeOnSelect
                  />
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item class="mr-b0">
                  <a-button type="primary" @click="handleFind">查询</a-button>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
          <a-space>
            <a-button :disabled="!lists.length" type="primary" :loading="spinning" @click="handleAllSelected">本页全选</a-button>
          </a-space>
        </a-flex>
        <div class="goods-main">
          <a-spin :spinning="spinning">
            <div class="main">
              <GoodsItem v-for="item in lists" :key="item.id" :item="item" @add-good="handleAddGood" />
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
          </a-spin>
        </div>
      </a-flex>
    </div>
    <div class="goods-right">
      <div class="title-name">已选择商品</div>
      <div class="shop-cart-list">
        <template v-if="shopcart.length > 0">
          <div v-for="item in shopcart" :key="item.goodsId" class="shop-cart-item">
            <div class="img">
              <a-image :src="item.imgUrl || ''" :alt="item.cnTitle" :fallback="fallbackImg" width="100%" height="100%" />
            </div>
            <div class="content">
              <div class="name">{{ item.cnTitle }}</div>
              <div class="attrsValuesVos"></div>
              <a-space style="margin-top: 20px">
                <div class="opt-num">
                  <a-input-number v-model:value="item.count" :min="1"></a-input-number>
                </div>
                <span style="">x</span>
                <div class="opt-total">${{ item.systemPrice }}</div>
              </a-space>
            </div>
            <div class="del">
              <a-button type="primary" danger shape="circle" :icon="h(DeleteOutlined)" @click="handleRemove(item.goodsId)" />
            </div>
          </div>
        </template>
        <template v-else></template>
      </div>
      <div class="goods-total">
        <div class="price">
          <div class="all">合计：$ {{ totalPrice.toFixed(2) }}</div>
          <div class="btn">
            <a-button type="primary" :disabled="shopcart.length === 0" @click="() => (bulkOrderModalOpen = true)">批量下单</a-button>
            <a-button type="primary" :disabled="shopcart.length === 0" @click="() => (singleOrderModalOpen = true)" style="margin-left: 8px"
              >单笔下单</a-button
            >
          </div>
        </div>
      </div>
    </div>
    <!--  -->
    <singleOrderModal v-model:open="singleOrderModalOpen" v-model:shopcart="shopcart" :totalPrice="totalPrice" />
    <!--  -->
    <bulkOrderModal v-model:open="bulkOrderModalOpen" v-model:shopcart="shopcart" :totalPrice="totalPrice" />
  </div>
</template>
<style scoped>
.goods {
  height: calc(100vh - 40px);
  /* width: calc(100% - 40px); */
  display: flex;
  padding: 20px;
}

.goods-left {
  width: 70%;
  height: 100%;
}

.goods-main {
  width: 100%;
  height: calc(100% - 100px);
}

.goods-main .main {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  overflow: auto;
  height: calc(100% - 80px);
}

.goods-right {
  width: 30%;
  height: 100%;
  border-left: 1px solid #ccc;
}

.title-name {
  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 19px;
  color: #000;
  margin-bottom: 10px;
}

.goods-right .title-name {
  font-style: normal;
  font-weight: 700;
  font-size: 16px;
  line-height: 19px;
  padding-left: 20px;
  color: #000;
}

.goods-right .shop-cart-list {
  width: 100%;
  height: calc(100% - 200px);
  overflow: auto;
  border-bottom: 1px solid #ccc;
}

.goods-right .goods-total {
  padding: 20px;
}

.goods-right .shop-cart-item {
  display: flex;
  /* width: calc(100% - 40px); */
  align-items: center;
  border-bottom: 1px solid #ccc;
  padding: 20px;
}

.goods-right .shop-cart-item:last-child {
  border-bottom: 0;
}

.goods-right .shop-cart-item .img {
  width: 90px;
  height: 90px;
}

.goods-right .shop-cart-item .img img {
  object-fit: cover;
  -o-object-fit: cover;
}

.goods-right .shop-cart-item .content {
  /* width: calc(100% - 152px); */
  padding: 0 14px;
}

.goods-right .shop-cart-item .content .name {
  font-style: normal;
  font-weight: 400;
  font-size: 16px;
  line-height: 19px;
  color: #000;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.goods-right .shop-cart-item .content .attrsValuesVos {
  line-height: 36px;
  font-size: 14px;
  color: #666;
}

.goods-right .shop-cart-item .content .opt {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.goods-right .shop-cart-item .del {
  width: 36px;
}

.goods-right .goods-total {
  padding: 20px;
}

.goods-right .goods-total .price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 40px;
}

.goods-right .goods-total .price .all {
  font-style: normal;
  font-weight: 400;
  font-size: 14px;
  line-height: 16px;
  color: #000;
}

.confirm-order .goods-name {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
