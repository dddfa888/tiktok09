<script setup lang="jsx">
import { ref, reactive, watch, createVNode } from 'vue'
import { ApiGetGoodsMemberComment, ApiCgstatusMemberComment } from '@/api/api.js'
import { Modal, message } from 'ant-design-vue'
import { ExclamationCircleOutlined } from '@ant-design/icons-vue'
import mevModal from './mevModal.vue'
import batchModal from './batchModal.vue'

const props = defineProps({
  open: { type: Boolean, default: () => false },
  id: { type: [String, Number], default: () => null }
})
const emits = defineEmits(['update:open'])
const accountTypeOptions = [
  { label: '正式账号', value: '1' },
  { label: '演示账号', value: '2' }
]
const evaluationTypeOptions = [
  { label: '好评', value: '1' },
  { label: '中评', value: '2' },
  { label: '差评', value: '3' }
]

const findForm = reactive({
  username: '', // 会员账号
  accountType: '', // 全部'' 正式账号:'1', 演示账号:'2'
  evaluationType: '' // 全部'' 好评:'1', 全部:'2', 差评:'3'
})

const loading = ref(false)
const dataSource = ref([])
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
    getList()
  }
})
const columns = ref([
  { title: '会员账号', dataIndex: 'userName' },
  {
    title: '账号类型',
    dataIndex: 'accountType',
    customRender: ({ text, record }) => {
      return accountTypeOptions.find(item => item.value == record.accountType)?.label || ''
    }
  },
  {
    title: '评价',
    dataIndex: 'evaluationType',
    customRender: ({ text, record }) => {
      return evaluationTypeOptions.find(item => item.value == record.evaluationType)?.label || ''
    }
  },
  { title: '评分', dataIndex: 'rating' },
  { title: '所在店铺', dataIndex: 'sellerName' },
  {
    title: '评论内容',
    dataIndex: 'content',
    width: 520,
    customRender: ({ text, record }) => {
      return (
        <div className="table-cell-ellipsis" alt={text}>
          {text}
        </div>
      )
    }
  },
  { title: '评价时间', dataIndex: 'commentTime' },
  {
    title: '前端展示',
    dataIndex: 'status',
    customRender: ({ text, record }) => {
      return <a-switch checked={record.status} unCheckedValue={1} checkedValue={0} onChange={e => handleChangeStatus(e, record)} />
    }
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    customRender: ({ text, record }) => {
      return (
        <a-space>
          <a-button type="primary" size="small" onClick={() => handleViewMev(record)}>
            查看评论
          </a-button>
          <a-button type="primary" danger size="small" onClick={() => handleDel(record)}>
            删除
          </a-button>
        </a-space>
      )
    }
  }
])
const muChoice = reactive({
  selectedRowKeys: [],
  loading: false
})

const handleFind = () => {
  pagination.current = 1
  getList()
}

const handleCancel = () => {
  emits('update:open', false)
}

const onSelectChange = selectedRowKeys => {
  muChoice.selectedRowKeys = selectedRowKeys
}
const handleChangeStatus = async (e, record) => {
  try {
    const dataRes = await ApiCgstatusMemberComment(e == 0 ? 'open' : 'hide', { id: record.id })
    message[dataRes.code == 0 ? 'success' : 'error'](dataRes.msg)
    record.status = e
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
}
const handleDel = record => {
  Modal.confirm({
    title: '提示',
    icon: createVNode(ExclamationCircleOutlined),
    content: '确认删除吗？',
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    centered: true,
    onOk() {
      return new Promise((resolve, reject) => {
        ApiCgstatusMemberComment('hide', { id: record.id })
          .then(res => {
            message[res.code == 0 ? 'success' : 'error'](res.msg)
            if (res.code == 0) {
              getList()
              resolve()
            } else {
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

const getList = async () => {
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    const dataRes = await ApiGetGoodsMemberComment({
      ...findForm,
      pageNo: pagination.current,
      pageSize: pagination.pageSize,
      systemGoodsId: props.id || null
    })
    if (dataRes.code == 0) {
      const { elements, totalElements, pageSize } = dataRes.data
      dataSource.value = elements
      pagination.total = totalElements
      pagination.pageSize = pageSize
    }
    console.log(dataRes)
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
}

watch([() => props.open, () => props.id], nval => {
  if (nval[0] && nval[1]) {
    getList()
  }
})

const batchModalOpen = ref(false) // 一键修改评论时间弹窗

const mevModalOpen = ref(false)
const mevInfo = ref(null)
const handleViewMev = record => {
  mevInfo.value = record
  mevModalOpen.value = true
}

const resetList = () => {
  getList()
}
</script>
<template>
  <a-modal :open="open" title="会员评价" centered width="860px" @cancel="handleCancel" destroyOnClose>
    <template #footer>
      <a-button key="back" @click="handleCancel">取消</a-button>
      <a-button key="submit" type="primary" @click="handleCancel">确定</a-button>
    </template>
    <a-flex gap="middle" vertical>
      <a-form ref="formRef" :model="findForm">
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item label="会员账号" name="username">
              <a-input v-model:value="findForm.username" placeholder="会员账号" allowClear />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="账号类型" name="accountType">
              <a-select v-model:value="findForm.accountType" placeholder="账号类型">
                <a-select-option value="">全部</a-select-option>
                <a-select-option v-for="item in accountTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="评分" name="evaluationType">
              <a-select v-model:value="findForm.evaluationType" placeholder="评分">
                <a-select-option value="">全部</a-select-option>
                <a-select-option v-for="item in evaluationTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-space>
              <a-button type="primary" @click="handleFind">查询</a-button>
              <a-button>重置</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
      <a-space>
        <a-button :disabled="!muChoice.selectedRowKeys.length" type="primary" @click="() => (batchModalOpen = true)">一键修改评论时间</a-button>
      </a-space>
      <a-table
        :row-selection="{ selectedRowKeys: muChoice.selectedRowKeys, onChange: onSelectChange }"
        :loading="loading"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="pagination"
        :scroll="{ x: 'max-content' }"
      />
    </a-flex>
    <!--  -->
    <batchModal v-model:open="batchModalOpen" @resetList="resetList" />
    <!--  -->
    <mevModal v-model:open="mevModalOpen" v-model:info="mevInfo" />
  </a-modal>
</template>
<style>
.table-cell-ellipsis {
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 控制显示的行数 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal; /* 或者 nowrap，根据你的需求选择 */
  word-wrap: break-word; /* 允许在单词内换行 */
  width: 100%; /* 或者你需要的宽度 */
}
</style>
