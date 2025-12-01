<template>
  <div class="my-uploader">
    <div>{{ t('uploadPaymentImage') }}</div>

    <!-- 自定义选择来源按钮 -->
    <van-button
      type="primary"
      size="small"
      @click="chooseSource"
      :disabled="fileList.length >= 1"
    >{{ t('上传图片') }}</van-button>

    <!-- 隐藏的 van-uploader -->
    <van-uploader
      ref="uploader"
      v-model="fileList"
      :max-count="1"
      :multiple="false"
      :after-read="afterRead"
      :deletable="true"
      :disabled="isUploaderDisabled"
      :max-size="10 * 1024 * 1024"
      @oversize="onOversize"
      @delete="onDelete"
      accept="image/*"
      style="display:none"
    />

    <!-- 显示上传成功的图片预览 -->
    <div v-if="uploadImg" style="margin-top: 10px; position: relative; display: inline-block;">
      <img :src="uploadImg" alt="上传图片" style="max-width: 100%; max-height: 200px; display: block;" />
      <van-icon
        name="cross"
        class="delete-icon"
        @click="deleteImage"
        style="position: absolute; top: 4px; right: 4px; font-size: 20px; color: rgb(577,236,236); cursor: pointer;"
      />
    </div>

    <!-- 选择来源的弹窗 -->
    <van-action-sheet
      v-model:show="showSourceSheet"
      :actions="actions"
      :cancel-text="t('取消')"
      @select="onSelectSource"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { Toast } from 'vant'
import { uploadimgExecute } from '@/service/upload.api'
const emit = defineEmits(['update:uploadImg'])
const { t } = useI18n()

const fileList = ref([])
const uploadImg = ref('')
const isUploaderDisabled = ref(false)
const showSourceSheet = ref(false)
watch(uploadImg, (val) => {
  emit('update:uploadImg', val)
})
const actions = [
  { name: t('拍照'), type: 'camera' },
  { name: t('从相册选择'), type: 'album' }
]

const uploader = ref(null)

function chooseSource() {
  showSourceSheet.value = true
}

function onSelectSource(action) {
  showSourceSheet.value = false
  const input = uploader.value?.$el.querySelector('input')
  if (!input) return

  input.accept = 'image/*'
  if (action.type === 'camera') {
    input.setAttribute('capture', 'environment')
  } else {
    input.removeAttribute('capture')
  }
  input.click()
}

function afterRead(file) {
  uploadImg.value = ''
  isUploaderDisabled.value = true

  file.status = 'uploading'
  file.message = t('uploading')

  uploadimgExecute({
    file: file.file,
    moduleName: 'recharge'
  })
    .then((url) => {
      file.status = 'success'
      file.message = t('uploadSuccess')
      uploadImg.value = url
      fileList.value = [file]
      isUploaderDisabled.value = false
    })
    .catch(() => {
      file.message = t('上传失败')
      file.status = 'failed'
      isUploaderDisabled.value = false
    })
}

function onOversize() {
  Toast.fail(t('fileMaxLimit'))
}

function onDelete(file, index) {
  fileList.value.splice(index, 1)
  uploadImg.value = ''
}

function deleteImage() {
  fileList.value = []
  uploadImg.value = ''
}
</script>
