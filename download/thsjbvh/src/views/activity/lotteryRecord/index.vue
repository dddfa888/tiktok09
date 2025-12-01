<script setup lang="jsx">
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'
import { ref, reactive, onMounted } from 'vue'
import { ApiGetLotteryRecord } from '@/api/api.js'

defineOptions({ name: 'ActivityLotteryRecord' })

const formRef = ref()
const findForm = reactive({
  partyId: null, // 中奖用户ID
  partyName: '', // 中奖用户名称
  prizeType: null, // 奖品类型， 1-实物、2-彩金, 3-谢谢惠顾
  receiveState: null, // 是否领取 (1-已领取,0-未领取)
  sellerName: '', // 店铺名
  startDate: '', // 时间范围-开始
  endDate: '' // 时间范围-结束
})
const prizeType = [
  { label: '实物', value: 1 },
  { label: '彩金', value: 2 },
  { label: '谢谢惠顾', value: 3 }
]
const receiveState = [
  { label: '已领取', value: 1 },
  { label: '未领取', value: 0 }
]

const loading = ref(false)
const dataSource = ref([])
const columns = ref([
  { title: '活动ID', dataIndex: '' },
  { title: '活动名称', dataIndex: '' },
  { title: '用户ID', dataIndex: '' },
  { title: '用户名称', dataIndex: '' },
  { title: '店铺名称', dataIndex: '' },
  { title: '奖品ID', dataIndex: '' },
  { title: '奖品名称', dataIndex: '' },
  { title: '物品类型', dataIndex: '' },
  { title: '奖品价值', dataIndex: '' },
  { title: '推荐人', dataIndex: '' },
  { title: '是否领取', dataIndex: '' },
  { title: '创建时间', dataIndex: '' },
  { title: '中奖时间', dataIndex: '' },
  { title: '领取时间', dataIndex: '' }
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
    const dataRes = await ApiGetLotteryRecord({ ...findForm, pageNum: pagination.current, pageSize: pagination.pageSize })
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
              <a-form-item label="用户ID" name="partyId">
                <a-input v-model:value="findForm.partyId" placeholder="请输入中奖用户ID" allowClear />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item label="用户名称" name="partyName">
                <a-input v-model:value="findForm.partyName" placeholder="请输入中奖用户名称" allowClear />
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
              <a-form-item label="是否领取" name="receiveState">
                <a-select v-model:value="findForm.receiveState" :options="receiveState" placeholder="是否领取" allowClear></a-select>
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
