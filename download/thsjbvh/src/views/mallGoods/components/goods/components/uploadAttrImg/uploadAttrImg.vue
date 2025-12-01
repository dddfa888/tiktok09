<script setup>
import { ref, watch } from 'vue'
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue'
import { message, Form } from 'ant-design-vue'
import { newImageUpload } from '@/api/api'

const props = defineProps({
  value: { type: [String, Array] },
  title: { type: String, default: () => '上传图片' },
  maxLen: { type: Number, default: () => 1 }
})
const emits = defineEmits(['update:value'])
const formItemContext = Form.useInjectFormItemContext()

const triggerChange = changeValue => {
  if (typeof props.value === 'string' || (typeof props.value === 'object' && props.value === null)) {
    emits('update:value', changeValue.length ? changeValue[0].response : '')
    formItemContext.onFieldChange()
  } else if (Array.isArray(props.value)) {
    let imgs = changeValue.map(item => item.response)
    emits('update:value', imgs)
    formItemContext.onFieldChange()
  } else {
    console.error('value类型错误')
  }
}

const loading = ref(false)
const fileList = ref([])
const previewImage = ref('')
const previewVisible = ref(false)

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
  if (loading.value) {
    return false
  }
  loading.value = true
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
        console.log(res)
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
  } finally {
    loading.value = false
  }
}
const handlePreview = async file => {
  previewImage.value = file.response || file.url
  previewVisible.value = true
}
const handleCancel = () => {
  previewVisible.value = false
}
const handleRemove = file => {
  const index = fileList.value.indexOf(file)
  const newFileList = fileList.value.slice()
  newFileList.splice(index, 1)
  fileList.value = newFileList
  triggerChange(fileList.value)
  return Promise.resolve(false)
}

const setImg = () => {
  if (!props.value) {
    return
  }
  if (typeof props.value === 'string' || (typeof props.value === 'object' && props.value === null)) {
    fileList.value = [{ uid: '-1', name: 'image', status: 'done', url: props.value }]
  } else if (Array.isArray(props.value)) {
    fileList.value = props.value.map((item, idx) => ({ uid: `img${idx}`, name: 'image', status: 'done', url: item }))
  } else {
    console.error('请传入正确的数据')
  }
}

watch(
  () => props.value,
  nval => {
    setImg()
  },
  { deep: true, immediate: true }
)
</script>
<template>
  <div>
    <a-upload
      v-model:file-list="fileList"
      name="avatar"
      list-type="picture-card"
      class="avatar-uploader"
      :before-upload="beforeUpload"
      :customRequest="customRequest"
      @preview="handlePreview"
      @remove="handleRemove"
    >
      <div v-if="fileList.length < maxLen">
        <plus-outlined />
        <div style="margin-top: 8px">{{ title }}</div>
      </div>
    </a-upload>
    <a-modal :open="previewVisible" title="预览图片" :footer="null" @cancel="handleCancel">
      <img alt="example" style="width: 100%" :src="previewImage" />
    </a-modal>
  </div>
</template>
