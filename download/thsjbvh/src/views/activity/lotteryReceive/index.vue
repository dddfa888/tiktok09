<script setup lang="jsx">
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'
import { ref, reactive, onMounted } from 'vue'
import { ApiGetLotteryReceive } from '@/api/api.js'

defineOptions({ name: 'ActivityLotteryReceive' })

const formRef = ref()
const findForm = reactive({ partyId: null, partyName: '', prizeType: null, sellerName: '', state: null, startDate: '', endDate: '' })
const prizeType = [
  { label: '实物', value: 1 },
  { label: '彩金', value: 2 },
  { label: '谢谢惠顾', value: 3 }
]
const stateOptions = [
  { label: '已派发', value: 1 },
  { label: '未派发', value: 0 }
]

const loading = ref(false)
const dataSource = ref([])
const columns = ref([
  { title: '活动名称', dataIndex: 'lotteryName' },
  { title: '领取用户ID', dataIndex: 'partyId' },
  { title: '领取用户名称', dataIndex: 'partyName' },
  { title: '店铺名称', dataIndex: 'sellerName' },
  {
    title: '奖品类型',
    dataIndex: 'prizeType',
    customRender: ({ text, record }) => {
      let txt = prizeType.find(item => item.value === record.prizeType)?.label || '谢谢惠顾'
      return txt
    }
  },
  { title: '奖品价值', dataIndex: 'prizeAmount' },
  { title: '推荐人', dataIndex: 'recommendName' },
  {
    title: '派发状态',
    dataIndex: 'state',
    customRender: ({ text, record }) => {
      return stateOptions.find(item => item.value === record.state)?.label || ''
    }
  },
  { title: '创建时间', dataIndex: 'createTime' },
  { title: '申请时间', dataIndex: 'applyTime' },
  { title: '派发时间', dataIndex: 'issueTime' },
  { title: '操作', key: 'operation' }
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
    const dataRes = await ApiGetLotteryReceive({ ...findForm, pageNum: pagination.current, pageSize: pagination.pageSize })
    console.log(dataRes)
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
              <a-form-item label="领取用户ID" name="partyId">
                <a-input v-model:value="findForm.partyId" placeholder="请输入领取用户ID" allowClear />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="用户名称" name="partyName">
                <a-input v-model:value="findForm.partyName" placeholder="请输入领取用户名称" allowClear />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="店铺名称" name="sellerName">
                <a-input v-model:value="findForm.sellerName" placeholder="请输入店铺名称" allowClear />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="奖品类型" name="prizeType">
                <a-select v-model:value="findForm.prizeType" :options="prizeType" placeholder="请选择奖品类型" allowClear></a-select>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="派发状态" name="state">
                <a-select v-model:value="findForm.state" :options="stateOptions" placeholder="请选择派发状态" allowClear></a-select>
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
  </div>
</template>
