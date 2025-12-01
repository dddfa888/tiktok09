<script setup>
import { ApiAddAttrCategory } from '@/api/api.js'
import { ref } from 'vue'
import { message } from 'ant-design-vue'

const props = defineProps({
  attributeItem: { type: Object, default: () => {} },
  id: { type: [String, Number] },
  lang: { type: String, default: 'en' }
})
const emits = defineEmits(['update:attributeItem'])

const name = ref('')
const loading = ref(false)
const handleAdd = async () => {
  if (!name.value) {
    message.error('请输入规格属性')
    return
  }
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    const params = { lang: props.lang, attrId: props.attributeItem.attrId, name: name.value }
    const dataRes = await ApiAddAttrCategory(params)
    if (dataRes.code == 0) {
      let item = { attrValueId: dataRes.data.id, attrValueName: params.name, iconImg: null, icon: false, checked: false }
      emits('update:attributeItem', { ...props.attributeItem, attrValues: [...props.attributeItem.attrValues, item] })
      name.value = ''
    } else {
      message.error(dataRes.msg)
    }
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
}
</script>
<template>
  <a-input-group compact>
    <a-input v-model:value="name" placeholder="请输入规格属性" style="width: calc(100% - 88px)" />
    <a-button :disabled="loading" type="primary" @click="handleAdd">添加属性</a-button>
  </a-input-group>
</template>
