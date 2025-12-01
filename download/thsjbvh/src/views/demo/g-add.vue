<script setup>
import { computed, onMounted, ref, reactive, shallowRef, shallowReactive, watch, watchEffect } from 'vue'
import uploadAttrImg from '@/views/mallGoods/components/goods/components/uploadAttrImg/uploadAttrImg.vue'

const attrbutes = shallowRef([
  {
    attrId: 'B0B3DPK62S_color',
    attrName: '颜色',
    attrValues: [
      {
        attrValueId: 'B0B3DPK62S_color_0',
        attrValueName: '龙凤',
        iconImg: null,
        icon: false
      },
      {
        attrValueId: 'B0B3DPK62S_color_1',
        attrValueName: '呈祥',
        iconImg: null,
        icon: false
      }
    ]
  },
  {
    attrId: 'B0B3DPK62S_size',
    attrName: '大小',
    attrValues: [
      {
        attrValueId: 'B0B3DPK62S_size_0',
        attrValueName: '9.1 英寸 x 787 英寸',
        iconImg: null,
        icon: false
      },
      {
        attrValueId: 'B0B3DPK62S_size_1',
        attrValueName: '9.1 英寸 x 1969 英寸',
        iconImg: null,
        icon: false
      },
      {
        attrValueId: 'B0B3DPK62S_size_2',
        attrValueName: '6.7 英寸 x 787 英寸',
        iconImg: null,
        icon: false
      },
      {
        attrValueId: 'B0B3DPK62S_size_3',
        attrValueName: '6.7 英寸 x 1969 英寸',
        iconImg: null,
        icon: false
      }
    ]
  }
])

function processAttributesData(attributes) {
  return attributes.map(item => ({
    ...item,
    attrValues: item.attrValues.map(attrItem => reactive({ ...attrItem, checked: false }))
  }))
}
const attributesData = computed(() => {
  return processAttributesData(attrbutes.value)
})

const skus = ref([])

const generateSkus = () => {
  const selectedAttrValues = attributesData.value.map(attr => {
    return attr.attrValues
      .filter(value => value.checked)
      .map(attrValue => {
        return {
          attrId: attr.attrId,
          attrName: attr.attrName,
          ...attrValue
        }
      })
  })
  // 计算所有可能的SKU组合
  const skuCombinations = getCombinations(selectedAttrValues)
  // 将组合转换为SKU对象并赋值给skus
  skus.value = skuCombinations.map(combination => ({
    skuId: generateSkuId(combination),
    attrs: combination,
    price: 0,
    coverImg: null,
    img: null
  }))
}
// 获取所有可能的组合
const getCombinations = items => {
  let result = [[]]
  for (const item of items) {
    result = result.flatMap(cmb => item.map(val => [...cmb, val]))
    if (!result.length) break
  }
  return result
}

const generateSkuId = combination => {
  return combination.map(item => item.attrValueId).join('_')
}

const handleRemove = skuid => {
  // 查找待删除SKU在skus中的索引
  const idx = skus.value.findIndex(sku => sku.skuId === skuid)
  // 删除SKU
  skus.value = [...skus.value.slice(0, idx), ...skus.value.slice(idx + 1)]
}

const form = shallowReactive({
  img: 'https://tiktokmalli-shop-online.s3.ap-east-1.amazonaws.com/goods/2024-03-25/aa239114-78ee-4138-88be-f8966db426be.png'
})

// 在组件挂载后或每次选中状态改变时调用此方法
onMounted(() => generateSkus())

// 监听 attrValues 中的 checked 属性变化并更新 SKU 列表
watchEffect(() => {
  generateSkus()
})
</script>
<template>
  <div style="width: 640px">
    <a-flex gap="small" vertical>
      <a-card v-for="item in attributesData" :key="item.attrId" :title="item.attrName" size="small">
        <a-checkbox v-for="attr in item.attrValues" :key="attr.attrValueId" v-model:checked="attr.checked">{{ attr.attrValueName }}</a-checkbox>
      </a-card>

      <table class="tableContainer">
        <thead>
          <tr>
            <th v-for="attribute in Object.keys(attributesData)" :key="attribute">{{ attributesData[attribute].attrName }}</th>
            <th>价格</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, idx) in skus" :key="idx">
            <td v-for="attribute in Object.keys(attributesData)" :key="attribute">{{ item.attrs[attribute].attrValueName }}</td>
            <td><a-input-number v-model:value="item.price" :min="0" /></td>
            <td>
              <a-space>
                <a-button type="primary">编辑</a-button>
                <a-button type="primary" @click="handleRemove(item.skuId)">删除</a-button>
              </a-space>
            </td>
          </tr>
        </tbody>
      </table>

      <a-form ref="formRef" :model="form">
        <a-form-item label="图片" name="img">
          <uploadAttrImg v-model:value="form.img" />
        </a-form-item>
      </a-form>
      {{form}}
    </a-flex>
  </div>
</template>
<style scoped>
.tableContainer tr > th {
  padding: 8px 12px;
  border: 1px solid #e8e8e8;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 500;
  text-align: center;
  background: #fafafa;
}
.tableContainer tr > td {
  border: 1px solid #e8e8e8;
  padding: 6px 8px;
}
</style>
