<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { clone, isEmpty, omit } from 'xe-utils'
import skuImgModal from './modal.vue'

const props = defineProps({
  attributes: { type: Array, default: [] },
  originSkus: { type: Array }
})

const skuList = ref([])

defineExpose({ skuList })

const generateSkus = () => {
  skuList.value = clone(props.attributes, true)
  const selectedAttrValues = skuList.value.map(attr => {
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
  if (props.originSkus != null && props.originSkus.length) {
    skuList.value = skuCombinations.map((combination, idx) => {
      const originSkus = props.originSkus[idx]
      return {
        cid: generateSkuId(combination),
        attrs: combination,
        ...originSkus
      }
    })
  } else {
    skuList.value = skuCombinations.map(combination => ({
      cid: generateSkuId(combination),
      attrs: combination,
      price: 0,
      coverImg: null,
      img: null
    }))
  }
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

const visible = ref(false)
const cid = ref(null)
const handleEdit = id => {
  cid.value = id
  visible.value = true
}

const handleRemove = skuid => {
  // // 查找待删除SKU在skus中的索引
  const idx = skuList.value.findIndex(sku => sku.cid === skuid)
  console.log(idx)
  console.log(skuList.value.slice(0, idx))
  // // 删除SKU
  skuList.value = [...skuList.value.slice(0, idx), ...skuList.value.slice(idx + 1)]
}

watch(
  () => props.attributes,
  nval => {
    if (nval.length) {
      generateSkus()
    } else {
      skuList.value = []
    }
  },
  { deep: true }
)
</script>

<template>
  <div>
    <table class="tableContainer">
      <thead>
        <tr>
          <th v-for="attribute in Object.keys(attributes)" :key="attribute">{{ attributes[attribute].attrName }}</th>
          <th>价格</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, idx) in skuList" :key="idx">
          <td v-for="attribute in Object.keys(attributes)" :key="attribute">{{ item.attrs[attribute].attrValueName }}</td>
          <td><a-input-number v-model:value="item.price" :min="0" /></td>
          <td>
            <a-space>
              <a-button type="primary" @click="handleEdit(item.cid)">编辑</a-button>
              <a-button type="primary" @click="handleRemove(item.cid)">删除</a-button>
            </a-space>
          </td>
        </tr>
      </tbody>
    </table>
    <!--  -->
    <skuImgModal v-model:open="visible" v-model:sku="skuList" :cid="cid" />
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
