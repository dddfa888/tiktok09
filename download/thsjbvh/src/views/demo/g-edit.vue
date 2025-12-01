<script setup>
import { ref, reactive } from 'vue'
import { formdata, desc2 } from './datas/attribute'
import { pick, omit } from 'xe-utils'

const convertSkusToAttributes = skus => {
  const attributes = []
  skus.forEach(sku => {
    sku.attrs.forEach(attr => {
      const existingAttribute = attributes.find(a => a.attrId === attr.attrId)
      if (existingAttribute) {
        // 如果已经存在相同的属性ID，则向该属性的attrValues中添加新的值
        const existingAttrValue = existingAttribute.attrValues.find(av => av.attrValueId === attr.attrValueId)
        if (!existingAttrValue) {
          existingAttribute.attrValues.push({
            
            attrValueId: attr.attrValueId,
            attrValueName: attr.attrValueName,
            iconImg: attr.iconImg,
            icon: attr.icon,
            checked: true
          })
        }
      } else {
        // 如果不存在该属性ID，则创建一个新的属性对象
        attributes.push({
          attrId: attr.attrId,
          attrName: attr.attrName,
          attrValues: [
            {
              attrValueId: attr.attrValueId,
              attrValueName: attr.attrValueName,
              iconImg: attr.iconImg,
              icon: attr.icon,
              checked: true
            }
          ]
        })
      }
    })
  })
  return attributes
}
console.log(desc2.data.goodSkuAttrDto.skus)
console.log(convertSkusToAttributes(desc2.data.goodSkuAttrDto.skus))
</script>
<template>
  <a-card>
    <form ref="formRef">
      <a-form-item></a-form-item>
    </form>
  </a-card>
</template>
