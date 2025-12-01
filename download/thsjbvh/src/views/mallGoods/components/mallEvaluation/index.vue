<script setup lang="jsx">
import { ref, reactive, watch, createVNode } from 'vue'
import { ExclamationCircleOutlined } from '@ant-design/icons-vue'
import { ApiGetGoodsComment, ApiUpdateGoodsCommentItemStatus, ApiDeleteSystemComment } from '@/api/api.js'
import { message, Modal } from 'ant-design-vue'
import addModal from './add.vue'

const props = defineProps({
  open: { type: Boolean, default: () => false },
  id: { type: [String, Number], default: () => null }
})
const emits = defineEmits(['update:open'])

const findForm = reactive({ status: null })

const dataSource = ref([])
const selected = reactive({
  selectedRowKeys: [],
  loading: false
})
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
  { title: '评分', dataIndex: 'score' },
  {
    title: '图片',
    customRender: ({ text, record }) => {
      var keys = Object.keys(record)
      var nonEmptyImgUrls = keys.filter(function (key) {
        return key.startsWith('imgUrl') && record[key] !== null && record[key] !== undefined && record[key] !== ''
      })
      return `${nonEmptyImgUrls.length} 张`
    }
  },
  { title: '评论内容', dataIndex: 'content' },
  {
    title: '禁用评论',
    dataIndex: 'status',
    customRender: ({ text, record }) => {
      return <a-switch checked={record.status} checkedValue={1} unCheckedValue={0} onClick={event => handleChangeStatus(event, record.id)} />
    }
  },
  {
    title: '操作',
    customRender: ({ text, record }) => {
      return (
        <>
          <a-space>
            <a-button type="primary" danger size="small" onClick={() => handleDelete(record.id)}>
              删除
            </a-button>
          </a-space>
        </>
      )
    }
  }
])

const onSelectChange = selectedRowKeys => {
  selected.selectedRowKeys = selectedRowKeys
}

const handleChangeStatus = async (event, id) => {
  console.log(event, id)
  try {
    const dataRes = await ApiUpdateGoodsCommentItemStatus({ id, status: event ? 0 : 1 })
    console.log(dataRes)
    if (dataRes.code == '0') {
      getList()
    } else {
      message.error(dataRes.msg)
    }
  } catch (error) {}
}

const handleDeleteOperation = async (title, content, id) => {
  if (!id) return

  Modal.confirm({
    title,
    icon: createVNode(ExclamationCircleOutlined),
    content: createVNode('div', { style: 'color:red' }, content),
    cancelText: '取消',
    okText: '删除',
    okType: 'danger',
    centered: true,
    onOk: () => {
      return new Promise((resolve, reject) => {
        ApiDeleteSystemComment({ id }).then(res => {
          message[res.code == 0 ? 'success' : 'error'](res.msg)
          if (res.code == 0) {
            resolve()
            selected.selectedRowKeys = []
            getList()
          } else {
            reject(res)
          }
        })
      })
    }
  })
}

const handleDelete = id => {
  handleDeleteOperation('删除', '确认删除评论吗?', id)
}
const handleBatchDel = () => {
  const ids = selected.selectedRowKeys.join(',')
  handleDeleteOperation('批量删除', '确认删除选中的评论吗?', ids)
}

const spinning = ref(false)
const getList = async () => {
  if (spinning.value) {
    return
  }
  spinning.value = true
  try {
    const dataRes = await ApiGetGoodsComment({
      ...findForm,
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      systemGoodId: props.id
    })
    if (dataRes.code == '0') {
      const { elements, totalElements, pageSize } = dataRes.data
      pagination.total = totalElements
      pagination.pageSize = pageSize
      dataSource.value = elements ?? []
    }
  } catch (error) {
    console.log(error)
  } finally {
    spinning.value = false
  }
}

const handleCancel = () => {
  emits('update:open', false)
}

watch(
  [() => props.open, () => props.id],
  nval => {
    if (nval[0] && nval[1]) {
      getList()
    }
  },
  { immediate: true }
)

const addModalOpen = ref(false)
</script>
<template>
  <a-modal :open="open" title="评价库" centered width="860px" @cancel="handleCancel" destroyOnClose>
    <template #footer>
      <a-button key="back" @click="handleCancel">取消</a-button>
    </template>
    <a-form ref="formRef" :model="findForm">
      <a-row :gutter="12">
        <a-col :span="8">
          <a-form-item label="状态" name="status">
            <a-select v-model:value="findForm.status" placeholder="请选择状态" allowClear>
              <a-select-option :value="0">启用</a-select-option>
              <a-select-option :value="1">禁用</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item>
            <a-space>
              <a-button
                type="primary"
                @click="
                  () => {
                    pagination.current = 1
                    getList()
                  }
                "
                >查询</a-button
              >
              <a-button
                @click="
                  () => {
                    formRef.resetFields()
                    pagination.current = 1
                    getList()
                  }
                "
              >
                重置
              </a-button>
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <a-flex gap="middle" vertical>
      <a-space>
        <a-button type="primary" @click="() => (addModalOpen = true)">新增评价</a-button>
        <a-button type="primary" danger :disabled="selected.selectedRowKeys.length == 0" :loading="selected.loading" @click="handleBatchDel"
          >批量删除</a-button
        >
      </a-space>

      <a-card :bodyStyle="{ padding: 0 }">
        <a-table
          :row-selection="{ selectedRowKeys: selected.selectedRowKeys, onChange: onSelectChange }"
          :loading="spinning"
          :columns="columns"
          :dataSource="dataSource"
          :rowKey="record => record.id"
          :pagination="pagination"
        />
      </a-card>
    </a-flex>
    <addModal
      v-model:open="addModalOpen"
      :id="id"
      @reset-list="
        () => {
          pagination.current = 1
          getList()
        }
      "
    />
  </a-modal>
</template>
