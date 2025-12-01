<script setup>
import { reactive, ref } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { newImageUpload, ApiAddGoodsCommentItem } from '@/api/api.js'
import { message } from 'ant-design-vue'
import { omit } from 'xe-utils/omit'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  id: {
    type: [String, Number],
    default: ''
  }
})
const emits = defineEmits(['update:open', 'resetList'])

const formRef = ref()
const form = reactive({
  fileList: [],
  content: '',
  score: 1,
  status: 1
})
const fileList = ref([])
const desc = ref(['1分', '2分', '3分', '4分', '5分'])

const handleCancel = () => {
  emits('update:open', false)
  formRef.value.resetFields()
}

const previewVisible = ref(false)
const previewImage = ref('')
const handlePreview = async file => {
  previewImage.value = file.response || file.url
  previewVisible.value = true
}
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
        } else {
          onError(res.msg)
          message.error(res.msg)
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

const confirmLoading = ref(false)
const handleSubmit = async () => {
  if (confirmLoading.value) {
    return
  }
  confirmLoading.value = true
  try {
    await formRef.value.validate()

    const imgs = form.fileList.reduce((acc, item, idx) => {
      const imgUrl = item.response || ''
      acc['imgUrl' + (idx + 1)] = imgUrl
      return acc
    }, {})
    // 合并对象，但不覆盖form中的现有键
    const parameter = {
      ...form,
      ...imgs,
      systemGoodId: props.id
    }
    delete parameter.fileList
    const dataRes = await ApiAddGoodsCommentItem(parameter)
    message[dataRes.code == 0 ? 'success' : 'error'](dataRes.code == 0 ? '添加成功' : dataRes.msg)
    if (dataRes.code == 0) {
      emits('update:open', false)
      formRef.value.resetFields()
      emits('resetList')
    }
  } catch (error) {
    console.log(error)
  } finally {
    confirmLoading.value = false
  }
}
</script>
<template>
  <a-modal :open="open" title="新增评论" centered width="580px" @cancel="handleCancel" destroyOnClose>
    <template #footer>
      <a-button key="back" @click="handleCancel">取消</a-button>
      <a-button key="submit" type="primary" :loading="confirmLoading" @click="handleSubmit">确定</a-button>
    </template>
    <a-form ref="formRef" :model="form">
      <a-form-item label="评论图片" name="fileList">
        <a-upload
          v-model:file-list="form.fileList"
          list-type="picture-card"
          :beforeUpload="beforeUpload"
          :customRequest="customRequest"
          @preview="handlePreview"
        >
          <div v-if="form.fileList.length < 9">
            <plus-outlined />
            <div style="margin-top: 8px">上传图片</div>
          </div>
        </a-upload>
        <a-modal :open="previewVisible" title="评论图片" :footer="null" @cancel="() => (previewVisible = false)">
          <img alt="example" style="width: 100%" :src="previewImage" />
        </a-modal>
      </a-form-item>
      <a-form-item label="会员评分" name="score" :rules="[{ required: true, message: '请选择评分分数' }]">
        <a-rate v-model:value="form.score" :tooltips="desc" />
      </a-form-item>
      <a-form-item label="评论内容" name="content" :rules="[{ required: true, message: '请选择评分分数' }]">
        <a-textarea v-model:value="form.content" placeholder="请输入评论内容" :auto-size="{ minRows: 2, maxRows: 5 }"></a-textarea>
      </a-form-item>
      <a-form-item label="禁用评论">
        <a-switch v-model:checked="form.status" :checkedValue="1" :unCheckedValue="0" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
