<script setup>
import { ref, watch } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { newImageUpload } from '@/api/api.js'
import { message, Form } from 'ant-design-vue'

const props = defineProps({
  value: { type: [String, Array] },
  disabled: { type: Boolean, default: false }, // 是否禁用
  accept: { type: String, default: 'image/*' }, // 接受上传的文件类型
  listType: { type: String, default: 'picture-card' }, // 上传列表的内建样式，支持三种基本样式 text, picture 和 picture-card
  maxCount: { type: Number, default: 5 }, // 上传图片数量 默认5张
  multiple: { type: Boolean, default: false } // 是否支持多选文件，ie10+ 支持。开启后按住 ctrl 可选择多个文件。
})
const emits = defineEmits(['update:value'])

const formItemContext = Form.useInjectFormItemContext()
const triggerChange = changeValue => {
  let len = changeValue.length
  if (len === 0) {
    emits('update:value', '')
    formItemContext.onFieldChange()
  } else {
    emits('update:value', changeValue[0]['response'])
    formItemContext.onFieldChange()
  }
}

const fileList = ref([])

const beforeUpload = file => {
  return new Promise(resolve => {
    const isImg = file.type == 'image/jpeg' || file.type == 'image/jpg' || file.type == 'image/png'
    const isLt3M = file.size / 1024 / 1024 < 3
    if (!isImg) {
      message.error('请上传JPG或PNG格式')
      return false
    }
    if (!isLt3M) {
      message.error('图片大小不超过3M')
      return false
    }
    resolve(true)
    return true
  })
}
const customRequest = ({ file, onProgress, onSuccess, onError }) => {
  try {
    let params = new FormData()
    params.append('moduleName', 'goods')
    params.append('file', file)
    newImageUpload(params, {
      onUploadProgress: ({ total, loaded }) => {
        let percent = (loaded / total) * 100
        onProgress({ percent })
      }
    })
      .then(res => {
        if (res.code == '0') {
          onSuccess(res.data, file)
          triggerChange(fileList.value)
        } else {
          onError(res.msg)
        }
      })
      .catch(error => {
        console.log(error)
        onError(error)
      })
  } catch (error) {
    console.log(error)
  }
}
const handleRemove = file => {
  fileList.value = []
  triggerChange(fileList.value)
}

const setFileList = image => {
  if (typeof image == 'string') {
    const fileListItem = {
      uid: '-1',
      name: 'image',
      status: 'done',
      url: image
    }
    fileList.value = [fileListItem]
  }
}
watch(
  () => props.value,
  image => {
    if (image) {
      if (image) {
        setFileList(image)
      }
    }
  },
  { deep: true }
)
</script>
<template>
  <a-upload
    :disabled="disabled"
    v-model:file-list="fileList"
    :accept="accept"
    :list-type="listType"
    :max-count="maxCount"
    :before-upload="beforeUpload"
    :custom-request="customRequest"
    @remove="handleRemove"
  >
    <div v-if="fileList.length < maxCount">
      <PlusOutlined />
      <div style="margin-top: 8px">上传图片</div>
    </div>
  </a-upload>
</template>
