<script setup lang="jsx">
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'
import { ref, reactive, onMounted, createVNode } from 'vue'
import { ApiPrize, ApiDelPrize } from '@/api/api.js'
import modalForm from './components/modalForm/index.vue'
import { Modal, message } from 'ant-design-vue'
import { ExclamationCircleOutlined } from '@ant-design/icons-vue'

defineOptions({ name: 'ActivityPrize' })

const fallbackImg =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMIAAADDCAYAAADQvc6UAAABRWlDQ1BJQ0MgUHJvZmlsZQAAKJFjYGASSSwoyGFhYGDIzSspCnJ3UoiIjFJgf8LAwSDCIMogwMCcmFxc4BgQ4ANUwgCjUcG3awyMIPqyLsis7PPOq3QdDFcvjV3jOD1boQVTPQrgSkktTgbSf4A4LbmgqISBgTEFyFYuLykAsTuAbJEioKOA7DkgdjqEvQHEToKwj4DVhAQ5A9k3gGyB5IxEoBmML4BsnSQk8XQkNtReEOBxcfXxUQg1Mjc0dyHgXNJBSWpFCYh2zi+oLMpMzyhRcASGUqqCZ16yno6CkYGRAQMDKMwhqj/fAIcloxgHQqxAjIHBEugw5sUIsSQpBobtQPdLciLEVJYzMPBHMDBsayhILEqEO4DxG0txmrERhM29nYGBddr//5/DGRjYNRkY/l7////39v///y4Dmn+LgeHANwDrkl1AuO+pmgAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAwqADAAQAAAABAAAAwwAAAAD9b/HnAAAHlklEQVR4Ae3dP3PTWBSGcbGzM6GCKqlIBRV0dHRJFarQ0eUT8LH4BnRU0NHR0UEFVdIlFRV7TzRksomPY8uykTk/zewQfKw/9znv4yvJynLv4uLiV2dBoDiBf4qP3/ARuCRABEFAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghgg0Aj8i0JO4OzsrPv69Wv+hi2qPHr0qNvf39+iI97soRIh4f3z58/u7du3SXX7Xt7Z2enevHmzfQe+oSN2apSAPj09TSrb+XKI/f379+08+A0cNRE2ANkupk+ACNPvkSPcAAEibACyXUyfABGm3yNHuAECRNgAZLuYPgEirKlHu7u7XdyytGwHAd8jjNyng4OD7vnz51dbPT8/7z58+NB9+/bt6jU/TI+AGWHEnrx48eJ/EsSmHzx40L18+fLyzxF3ZVMjEyDCiEDjMYZZS5wiPXnyZFbJaxMhQIQRGzHvWR7XCyOCXsOmiDAi1HmPMMQjDpbpEiDCiL358eNHurW/5SnWdIBbXiDCiA38/Pnzrce2YyZ4//59F3ePLNMl4PbpiL2J0L979+7yDtHDhw8vtzzvdGnEXdvUigSIsCLAWavHp/+qM0BcXMd/q25n1vF57TYBp0a3mUzilePj4+7k5KSLb6gt6ydAhPUzXnoPR0dHl79WGTNCfBnn1uvSCJdegQhLI1vvCk+fPu2ePXt2tZOYEV6/fn31dz+shwAR1sP1cqvLntbEN9MxA9xcYjsxS1jWR4AIa2Ibzx0tc44fYX/16lV6NDFLXH+YL32jwiACRBiEbf5KcXoTIsQSpzXx4N28Ja4BQoK7rgXiydbHjx/P25TaQAJEGAguWy0+2Q8PD6/Ki4R8EVl+bzBOnZY95fq9rj9zAkTI2SxdidBHqG9+skdw43borCXO/ZcJdraPWdv22uIEiLA4q7nvvCug8WTqzQveOH26fodo7g6uFe/a17W3+nFBAkRYENRdb1vkkz1CH9cPsVy/jrhr27PqMYvENYNlHAIesRiBYwRy0V+8iXP8+/fvX11Mr7L7ECueb/r48eMqm7FuI2BGWDEG8cm+7G3NEOfmdcTQw4h9/55lhm7DekRYKQPZF2ArbXTAyu4kDYB2YxUzwg0gi/41ztHnfQG26HbGel/crVrm7tNY+/1btkOEAZ2M05r4FB7r9GbAIdxaZYrHdOsgJ/wCEQY0J74TmOKnbxxT9n3FgGGWWsVdowHtjt9Nnvf7yQM2aZU/TIAIAxrw6dOnAWtZZcoEnBpNuTuObWMEiLAx1HY0ZQJEmHJ3HNvGCBBhY6jtaMoEiJB0Z29vL6ls58vxPcO8/zfrdo5qvKO+d3Fx8Wu8zf1dW4p/cPzLly/dtv9Ts/EbcvGAHhHyfBIhZ6NSiIBTo0LNNtScABFyNiqFCBChULMNNSdAhJyNSiECRCjUbEPNCRAhZ6NSiAARCjXbUHMCRMjZqBQiQIRCzTbUnAARcjYqhQgQoVCzDTUnQIScjUohAkQo1GxDzQkQIWejUogAEQo121BzAkTI2agUIkCEQs021JwAEXI2KoUIEKFQsw01J0CEnI1KIQJEKNRsQ80JECFno1KIABEKNdtQcwJEyNmoFCJAhELNNtScABFyNiqFCBChULMNNSdAhJyNSiECRCjUbEPNCRAhZ6NSiAARCjXbUHMCRMjZqBQiQIRCzTbUnAARcjYqhQgQoVCzDTUnQIScjUohAkQo1GxDzQkQIWejUogAEQo121BzAkTI2agUIkCEQs021JwAEXI2KoUIEKFQsw01J0CEnI1KIQJEKNRsQ80JECFno1KIABEKNdtQcwJEyNmoFCJAhELNNtScABFyNiqFCBChULMNNSdAhJyNSiECRCjUbEPNCRAhZ6NSiAARCjXbUHMCRMjZqBQiQIRCzTbUnAARcjYqhQgQoVCzDTUnQIScjUohAkQo1GxDzQkQIWejUogAEQo121BzAkTI2agUIkCEQs021JwAEXI2KoUIEKFQsw01J0CEnI1KIQJEKNRsQ80JECFno1KIABEKNdtQcwJEyNmoFCJAhELNNtScABFyNiqFCBChULMNNSdAhJyNSiEC/wGgKKC4YMA4TAAAAABJRU5ErkJggg=='
const formRef = ref()
const findForm = reactive({ prizeNameCn: '', prizeType: null, startDate: '', endDate: '' })
const prizeType = [
  { label: '实物', value: 1 },
  { label: '彩金', value: 2 },
  { label: '谢谢惠顾', value: 3 }
]

const loading = ref(false)
const dataSource = ref([])
const columns = ref([
  { title: '奖品ID', dataIndex: 'uuid' },
  { title: '奖品名称', dataIndex: 'prizeNameCn' },
  {
    title: '图片',
    dataIndex: 'image',
    customRender: ({ text, record }) => {
      return <a-image src={record.image || ''} fallback={fallbackImg} width={45} height={45} />
    }
  },
  {
    title: '奖品类型',
    dataIndex: 'prizeType',
    customRender: ({ text, record }) => {
      let txt = prizeType.find(item => item.value === record.prizeType)?.label || ''
      return txt
    }
  },
  { title: '价值', dataIndex: 'prizeAmount' },
  { title: '备注', dataIndex: 'remark' },
  { title: '操作人', dataIndex: 'createBy' },
  { title: '操作时间', dataIndex: 'createTime' },
  {
    title: '操作',
    key: 'operation',
    customRender: ({ text, record }) => {
      return (
        <a-space>
          <a-button
            type="primary"
            size="small"
            onClick={() => {
              open.value = true
              currid.value = record.uuid
            }}
          >
            编辑
          </a-button>
          <a-button type="primary" size="small" onClick={() => handleDel(record)}>
            删除
          </a-button>
        </a-space>
      )
    }
  }
])
const pagination = reactive({
  position: ['bottomCenter'],
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '30', '50'],
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total, range) => `${range[0]}-${range[1]}, 共${total}条数据`,
  onChange(page, size) {
    pagination.current = page
    pagination.pageSize = size
    getLists()
  }
})

const open = ref(false)
const currid = ref(null)

const handleDel = async record => {
  if (!record.uuid) {
    return
  }
  Modal.confirm({
    title: '提示',
    icon: createVNode(ExclamationCircleOutlined),
    content: '确认删除该奖品吗？',
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    onOk() {
      return new Promise((resolve, reject) => {
        ApiDelPrize(record.uuid)
          .then(res => {
            if (res.success === 200) {
              message.success(res.returnMsg)
              resolve()
              getLists()
            } else {
              message.error(res.errorMsg + ':' + res.errorCode)
              reject()
            }
          })
          .catch(err => {
            reject(err)
          })
      })
    },
    onCancel() {}
  })
}

const getLists = async () => {
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    const dataRes = await ApiPrize({ ...findForm, pageNum: pagination.current, pageSize: pagination.pageSize })
    if (dataRes.success === 200) {
      const { current, size, total, records } = dataRes.data
      pagination.current = current || 1
      pagination.pageSize = size || 10
      pagination.total = total || 0
      dataSource.value = records ?? []
    }
  } catch (error) {
  } finally {
    loading.value = false
  }
}
onMounted(() => {
  getLists()
})
</script>
<template>
  <div class="app-container">
    <a-flex gap="middle" vertical>
      <div>
        <a-form ref="formRef" :model="findForm">
          <a-row :gutter="12">
            <a-col :span="6">
              <a-form-item label="奖品名称" name="prizeNameCn">
                <a-input v-model:value="findForm.prizeNameCn" placeholder="请输入奖品名称" allowClear />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="奖品类型" name="prizeType">
                <a-select v-model:value="findForm.prizeType" :options="prizeType" placeholder="请选择奖品类型" allowClear></a-select>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="开始时间" name="startDate">
                <a-date-picker
                  :locale="locale"
                  v-model:value="findForm.startDate"
                  format="YYYY-MM-DD"
                  valueFormat="YYYY-MM-DD"
                  placeholder="开始时间"
                  class="w100pre"
                />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="结束时间" name="endDate">
                <a-date-picker
                  :locale="locale"
                  v-model:value="findForm.endDate"
                  format="YYYY-MM-DD"
                  valueFormat="YYYY-MM-DD"
                  placeholder="结束时间"
                  class="w100pre"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
        <a-space>
          <a-button
            type="primary"
            @click="
              () => {
                pagination.current = 1
                getLists()
              }
            "
            >查询</a-button
          >
          <a-button
            @click="
              () => {
                formRef.resetFields()
                pagination.current = 1
                getLists()
              }
            "
            >重置</a-button
          >
          <a-button
            type="primary"
            @click="
              () => {
                open = true
                currid = null
              }
            "
            >新增奖品</a-button
          >
        </a-space>
      </div>
      <div>
        <a-table
          :loading="loading"
          :rowKey="record => record.uuid"
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :scroll="{ x: 'max-content' }"
        ></a-table>
      </div>
    </a-flex>
    <!--  -->
    <modalForm v-model:open="open" v-model:id="currid" @reset-list="getLists" />
  </div>
</template>
