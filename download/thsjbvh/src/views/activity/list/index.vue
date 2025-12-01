<script setup lang="jsx">
import { ref, reactive, onMounted, createVNode } from 'vue'
import { ApiGetActivity, ApiDelActivity, ApiUpdShowActivity } from '@/api/api.js'
import { ExclamationCircleOutlined } from '@ant-design/icons-vue'
import { Modal, message } from 'ant-design-vue'
import modalForm from './components/modalForm.vue'

defineOptions({ name: 'ActivityList' })

const formRef = ref()
const findForm = reactive({ titleCn: '', status: null })
const statusOptions = [
  { label: '未启动', value: 0 },
  { label: '启用', value: 1 },
  { label: '结束活动', value: 2 }
]
// fruit_dial_lottery： 首充抽奖活动
// simple_lottery：简单抽奖活动
const lotteryType = [{ label: '抽奖活动', value: 'simple_lottery' }]
// 新增||编辑 模态框
const open = ref(false)
const currid = ref(null)

const loading = ref(false)
const dataSource = ref([])
const columns = ref([
  { title: '活动名称（中文）', dataIndex: 'titleCn' },
  {
    title: '活动类型',
    dataIndex: 'type',
    customRender: ({ text, record }) => {
      return lotteryType.find(item => item.value === record.type)?.label || ''
    }
  },
  {
    title: '活动状态',
    dataIndex: 'status',
    customRender: ({ text, record }) => {
      return statusOptions.find(item => item.value === record.status)?.label || ''
    }
  },
  { title: '活动开始时间', dataIndex: 'startTime' },
  { title: '活动结束时间', dataIndex: 'endTime' },
  {
    title: '活动链接',
    dataIndex: 'detailUrl',
    width: 300,
    customRender: ({ text, record }) => {
      return (
        <a href={record.detailUrl} target="_blank">
          {record.detailUrl}
        </a>
      )
    }
  },
  {
    title: '是否展示',
    dataIndex: 'isShow',
    customRender: ({ text, record }) => {
      return (
        <a-switch
          checked={record.isShow}
          checkedValue={1}
          unCheckedValue={0}
          onClick={(checked, event) => handleChangeShow(checked, event, record)}
        />
      )
    }
  },
  { title: '操作者', dataIndex: '' },
  { title: '创建时间', dataIndex: 'createTime' },
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
              currid.value = record.uuid
              open.value = true
            }}
          >
            编辑
          </a-button>
          <a-button type="primary" size="small" onClick={() => handleDel(record.uuid)}>
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

const getLists = async () => {
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    const dataRes = await ApiGetActivity({ ...findForm, pageNum: pagination.current, pageSize: pagination.pageSize })
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

const handleChangeShow = async (checked, e, record) => {
  try {
    let params = {
      isShow: record.isShow == 1 ? 0 : 1,
      uuid: record.uuid
    }
    const dataRes = await ApiUpdShowActivity(params)
    message[dataRes.success === 200 ? 'success' : 'error'](dataRes.success === 200 ? dataRes.returnMsg : dataRes.errorMsg + ':' + dataRes.errorCode)
    if (dataRes.success === 200) {
      let idx = dataSource.value.findIndex(item => item.uuid === params.uuid)
      dataSource.value[idx].isShow = params.isShow
    }
  } catch (error) {
    console.log(error)
  }
}

const handleDel = uuid => {
  if (!uuid) {
    return
  }
  Modal.confirm({
    title: '提示',
    icon: createVNode(ExclamationCircleOutlined),
    content: '确认删除该活动吗？',
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    onOk() {
      return new Promise((resolve, reject) => {
        ApiDelActivity(uuid)
          .then(res => {
            if (res.success === 200) {
              message.success(res.returnMsg)
              getLists()
              resolve()
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
              <a-form-item label="活动名称" name="titleCn">
                <a-input v-model:value="findForm.titleCn" placeholder="请输入活动名称" allowClear />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="活动状态" name="status">
                <a-select v-model:value="findForm.status" :options="statusOptions" placeholder="请选择活动状态" allowClear></a-select>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item>
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
                </a-space>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
        <a-space>
          <a-button
            type="primary"
            @click="
              () => {
                open = true
                currid = null
              }
            "
            >新增活动</a-button
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
    <modal-form
      v-model:open="open"
      v-model:id="currid"
      @resetList="
        () => {
          pagination.current = 1
          getLists()
        }
      "
    />
  </div>
</template>
