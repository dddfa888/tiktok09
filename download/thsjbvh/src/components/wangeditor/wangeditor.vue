<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { message } from 'ant-design-vue'
const baseURL = import.meta.env.VITE_APP_BASE_URL

import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { reactive, onMounted, watch, onBeforeUnmount, shallowRef, ref, shallowReactive } from 'vue'

const props = defineProps({
  label: { type: String, default: '' },
  value: { type: String, default: '' }
})
const emits = defineEmits(['update:value'])

const mode = 'default'
// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()
// 内容 HTML
const valueHtml = ref('')

const toolbarConfig = shallowReactive({})
const editorConfig = shallowReactive({
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      server: `${baseURL}/summer/attachment/upload`,
      fieldName: 'file', // form-data fieldName ，默认值 'file'
      maxFileSize: 2 * 1024 * 1024, // 单个文件的最大体积限制，默认为 2M
      allowedFileTypes: ['image/jpeg', 'image/jpg', 'image/png'], // 选择文件时的类型限制，默认为 ['image/*']
      timeout: 60 * 1000, // 超时时间
      // 上传之前触发
      onBeforeUpload(file) {
        console.log(file)
        const imgType = ['image/jpeg', 'image/jpg', 'image/png']
        const lt2M = file.size / 1024 / 1024 < 2
        if (!imgType.includes(file.type)) {
          message.error('请上传JPG、PNG格式的图片')
          return false
        }
        if (!lt2M) {
          message.error('图片大小不能超过2M')
          return false
        }
        return file
      },
      // 上传进度的回调函数
      onProgress(progress) {
        // progress 是 0-100 的数字
        console.log('progress', progress)
      },
      // 单个文件上传成功之后
      onSuccess(file, res) {
        console.log(`${file.name} 上传成功`, res)
      },
      // 单个文件上传失败
      onFailed(file, res) {
        console.log(`${file.name} 上传失败`, res)
      },
      // 上传错误，或者触发 timeout 超时
      onError(file, err, res) {
        console.log(`${file.name} 上传出错`, err, res)
      },
      // 自定义插入图片
      customInsert(res, insertFn) {
        console.log(res)
        //  insertFn(url, alt, href)
      }
    }
  }
})

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = editor => {
  editorRef.value = editor // 记录 editor 实例，重要！
}
const handleChange = editor => {
  emits('update:value', editor.getHtml())
}

watch(
  () => props.value,
  value => {
    if (!value) valueHtml.value = ''
    else valueHtml.value = value
  }
)
</script>
<template>
  <a-card :title="label ? label : false" size="small" :bodyStyle="{ padding: 0 }">
    <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />
    <Editor
      style="height: 320px; overflow-y: hidden"
      v-model="valueHtml"
      :defaultConfig="editorConfig"
      :mode="mode"
      @onCreated="handleCreated"
      @onChange="handleChange"
    />
  </a-card>
</template>
