<script setup>
import { reactive, ref, watch } from 'vue'
import uploadAttrImg from '@/views/mallGoods/components/goods/components/uploadAttrImg/uploadAttrImg.vue'

const props = defineProps({
  open: { type: Boolean, default: false },
  sku: { type: [Object, Array] },
  cid: { type: [String, Number] }
})
const emits = defineEmits(['update:open'])

const handleCancel = () => {
  emits('update:open', false)
}

const form = reactive({
  img: '',
  coverImg: []
})

const setImg = () => {
  const item = props.sku.find(item => item.cid === props.cid)
  const { img, coverImg } = item
  Object.assign(form, { img, coverImg })
}

watch([() => props.open, () => props.cid], val => {
  if (val[0] && val[1]) {
    setImg()
  }
})
</script>
<template>
  <div>
    <a-modal :open="open" :title="'图片-' + cid" width="520px" centered @cancel="handleCancel" destroyOnClose>
      <template #footer>
        <a-button key="back" @click="handleCancel">取消</a-button>
        <a-button key="submit" type="primary" @click="handleCancel">确认</a-button>
      </template>
      <a-form ref="formRef" :model="form">
        <a-form-item label="封面图片" name="img">
          <uploadAttrImg v-model:value="form.img" :maxLen="1" />
        </a-form-item>
        <a-form-item label="展示列表">
          <uploadAttrImg v-model:value="form.coverImg" :maxLen="10" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
