<script setup lang="jsx">
import { message } from 'ant-design-vue'
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'
import { ref, reactive, onMounted, computed } from 'vue'
import { ApiGetPosLogs, ApiBatchEndPosLogs } from '@/api/api.js'

defineOptions({ name: 'poslogs' })

const formRef = ref()
const loading = ref(false)
const findForm = reactive({
  uuid: '',
  status: null,
  startDate: '',
  endDate: ''
})
const statusOptions = [
  { label: '进行中', value: 1 },
  { label: '系统结束', value: 2 },
  { label: '异常中断', value: 3 },
  { label: '已暂停', value: 4 },
  { label: '人工结束', value: 5 }
]
const lists = ref([])
const columns = ref([
  { title: '任务编码', dataIndex: 'uuid' },
  { title: '开始日期', dataIndex: 'startTime' },
  { title: '结束日期', dataIndex: 'endTime' },
  { title: '总订单', dataIndex: 'orderNumAll' },
  { title: '订单总额', dataIndex: 'lumpAmount' },
  { title: '已下单', dataIndex: 'orderedNum' },
  { title: '下单金额', dataIndex: 'orderAmount' },
  {
    title: '状态',
    dataIndex: 'status',
    customRender: ({ text, record }) => {
      let txt = statusOptions.find(item => item.value === record.status)?.label || ''
      return txt
    }
  },
  {
    title: '单笔金额范围',
    customRender: ({ text, record }) => {
      return (
        <>
          {record.buyAmountStart} - {record.buyAmountEnd}
        </>
      )
    }
  },
  { title: '创建人', dataIndex: 'createUser' },
  {
    title: '操作',
    key: 'operation',
    customRender: ({ text, record }) => {
      return (
        <>
          <a-space>
            <a-button disabled={![1, 4].includes(record.status)} type="dashed" danger size="small" onClick={() => handleEnd(record.uuid)}>
              结束任务
            </a-button>
          </a-space>
        </>
      )
    }
  }
])
// 分页数据
const pagination = reactive({
  position: ['bottomCenter'],
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '30', '50'],
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total, range) => `${range[0]}-${range[1]}, 共${total}条数据`,
  onChange(page) {
    pagination.current = page
    getList()
  },
  onShowSizeChange(current, size) {
    pagination.pageSize = size
    getList()
  }
})
const handleResetFindForm = () => {
  formRef.value.resetFields()
  pagination.pageNum = 1
  getList()
}
// 批处理
const choice = reactive({
  uuids: [],
  secret: 'dolor',
  loading: false
})
const hasSelected = computed(() => choice.uuids.length > 0)
const onSelectChange = selectedRowKeys => {
  choice.uuids = selectedRowKeys
}
// 批量结束
const handleBatchEnd = async () => {
  if (choice.loading) {
    return
  }
  choice.loading = true
  try {
    const dataRes = await ApiBatchEndPosLogs({ uuids: choice.uuids })
    message[dataRes.success === 200 ? 'success' : 'error'](dataRes.returnMsg)
    if (dataRes.success === 200) {
      choice.uuids = []
      pagination.current = 1
      getList()
    }
  } catch (error) {
    console.log(error)
  } finally {
    choice.loading = false
  }
}
// 单个结束
const handleEnd = async uuid => {
  if (!uuid) return
  try {
    const dataRes = await ApiBatchEndPosLogs({ uuids: [uuid] })
    message[dataRes.success === 200 ? 'success' : 'error'](dataRes.returnMsg)
    if (dataRes.success === 200) {
      pagination.current = 1
      getList()
    }
  } catch (error) {
    console.log(error)
  }
}

// 获取数据列表
const getList = async () => {
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    const dataRes = await ApiGetPosLogs({
      ...findForm,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    })
    if (dataRes.success === 200) {
      const { current, size, total, records } = dataRes.data
      lists.value = records ?? []
      pagination.current = current
      pagination.pageSize = size
      pagination.total = total
    }
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
<template>
  <div class="pos-logs-page">
    <a-flex gap="middle" vertical>
      <a-form ref="formRef" :model="findForm">
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item label="任务编码" name="uuid">
              <a-input v-model:value="findForm.uuid" placeholder="任务编码" allowClear />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="findForm.status" placeholder="状态" :options="statusOptions" allowClear />
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
        <a-space>
          <a-button
            type="primary"
            @click="
              () => {
                pagination.pageNum = 1
                getList()
              }
            "
            >查询</a-button
          >
          <a-button @click="handleResetFindForm">重置</a-button>
          <a-button :disabled="!hasSelected" type="primary" @click="handleBatchEnd">一键结束</a-button>
        </a-space>
      </a-form>
      <a-table
        :loading="loading"
        :row-selection="{
          selectedRowKeys: choice.uuids,
          onChange: onSelectChange
        }"
        :columns="columns"
        :dataSource="lists"
        :rowKey="record => record.uuid"
        :scroll="{ scrollToFirstRowOnChange: true, x: 'max-content' }"
        :pagination="pagination"
      />
    </a-flex>
  </div>
</template>
<style scoped>
.pos-logs-page {
  padding: 20px;
}
</style>
