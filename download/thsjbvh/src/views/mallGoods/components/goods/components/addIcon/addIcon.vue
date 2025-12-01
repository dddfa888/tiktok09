<script setup>
import { reactive, ref, shallowRef, toRefs, watch } from 'vue'
import uploadAttrImg from '@/views/mallGoods/components/goods/components/uploadAttrImg/uploadAttrImg.vue'

const props = defineProps({
  attributes: { type: Array, default: () => [] }
})
const emits = defineEmits('update:attributes')

const attributes = toRefs(props).attributes

const form = reactive({
  attrId: null,
  icon: false
})

const attrValues = ref([])

watch(attributes, nval => {
  Object.assign(form, { attrId: null, icon: false })
})

const handleSelectAttr = atrid => {
  if (!atrid) {
    attrValues.value = []
    return false
  }
  const idx = attributes.value.findIndex(item => item.attrId === atrid)
  attrValues.value = attributes.value[idx].attrValues || []
  form.icon = attributes.value[idx].attrValues.every(item => item.icon)
}

const handleChangeIcon = e => {
  attrValues.value.forEach(item => {
    item.icon = e.target.checked
  })
}
</script>
<template>
  <a-flex gap="small" vertical>
    <a-row align="middle" :gutter="12">
      <a-col>
        <a-select
          v-model:value="form.attrId"
          :options="attributes"
          :field-names="{ label: 'attrName', value: 'attrId' }"
          placeholder="请选择产品规格"
          allowClear
          style="width: 210px"
          @change="handleSelectAttr"
        >
        </a-select>
      </a-col>
      <a-col>
        <a-checkbox :disabled="!form.attrId" v-model:checked="form.icon" @change="handleChangeIcon">规格使用Icon</a-checkbox>
      </a-col>
    </a-row>
    <a-card v-if="attrValues.length" size="small" title="产品规格属性图片">
      <a-flex wrap="wrap">
        <a-form-item v-for="item in attrValues" :key="item.attrValueId">
          <uploadAttrImg v-model:value="item['iconImg']" accept="image/*" :maxCount="1" />
          <div style="text-align: center">{{ item.attrValueName }}</div>
        </a-form-item>
      </a-flex>
    </a-card>
  </a-flex>
</template>
