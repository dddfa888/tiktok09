<script setup lang="jsx">
import { ref, reactive, onBeforeMount } from 'vue'
import { message } from 'ant-design-vue'
import { CheckOutlined, CloseOutlined } from '@ant-design/icons-vue'
import { ApiGoodCategory, ApiGoodMange, ApiChangeGoodMangeisShelf, ApiChangeGoodMangeUpdateStatus } from '@/api/api.js'
import modalGoods from './components/goods/index.vue'
import mallEvaluation from './components/mallEvaluation/index.vue'
import memberEvaluation from './components/memberEvaluation/index.vue'

defineOptions({ name: 'MallGoods' })

const fallbackImg =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMIAAADDCAYAAADQvc6UAAABRWlDQ1BJQ0MgUHJvZmlsZQAAKJFjYGASSSwoyGFhYGDIzSspCnJ3UoiIjFJgf8LAwSDCIMogwMCcmFxc4BgQ4ANUwgCjUcG3awyMIPqyLsis7PPOq3QdDFcvjV3jOD1boQVTPQrgSkktTgbSf4A4LbmgqISBgTEFyFYuLykAsTuAbJEioKOA7DkgdjqEvQHEToKwj4DVhAQ5A9k3gGyB5IxEoBmML4BsnSQk8XQkNtReEOBxcfXxUQg1Mjc0dyHgXNJBSWpFCYh2zi+oLMpMzyhRcASGUqqCZ16yno6CkYGRAQMDKMwhqj/fAIcloxgHQqxAjIHBEugw5sUIsSQpBobtQPdLciLEVJYzMPBHMDBsayhILEqEO4DxG0txmrERhM29nYGBddr//5/DGRjYNRkY/l7////39v///y4Dmn+LgeHANwDrkl1AuO+pmgAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAwqADAAQAAAABAAAAwwAAAAD9b/HnAAAHlklEQVR4Ae3dP3PTWBSGcbGzM6GCKqlIBRV0dHRJFarQ0eUT8LH4BnRU0NHR0UEFVdIlFRV7TzRksomPY8uykTk/zewQfKw/9znv4yvJynLv4uLiV2dBoDiBf4qP3/ARuCRABEFAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghgg0Aj8i0JO4OzsrPv69Wv+hi2qPHr0qNvf39+iI97soRIh4f3z58/u7du3SXX7Xt7Z2enevHmzfQe+oSN2apSAPj09TSrb+XKI/f379+08+A0cNRE2ANkupk+ACNPvkSPcAAEibACyXUyfABGm3yNHuAECRNgAZLuYPgEirKlHu7u7XdyytGwHAd8jjNyng4OD7vnz51dbPT8/7z58+NB9+/bt6jU/TI+AGWHEnrx48eJ/EsSmHzx40L18+fLyzxF3ZVMjEyDCiEDjMYZZS5wiPXnyZFbJaxMhQIQRGzHvWR7XCyOCXsOmiDAi1HmPMMQjDpbpEiDCiL358eNHurW/5SnWdIBbXiDCiA38/Pnzrce2YyZ4//59F3ePLNMl4PbpiL2J0L979+7yDtHDhw8vtzzvdGnEXdvUigSIsCLAWavHp/+qM0BcXMd/q25n1vF57TYBp0a3mUzilePj4+7k5KSLb6gt6ydAhPUzXnoPR0dHl79WGTNCfBnn1uvSCJdegQhLI1vvCk+fPu2ePXt2tZOYEV6/fn31dz+shwAR1sP1cqvLntbEN9MxA9xcYjsxS1jWR4AIa2Ibzx0tc44fYX/16lV6NDFLXH+YL32jwiACRBiEbf5KcXoTIsQSpzXx4N28Ja4BQoK7rgXiydbHjx/P25TaQAJEGAguWy0+2Q8PD6/Ki4R8EVl+bzBOnZY95fq9rj9zAkTI2SxdidBHqG9+skdw43borCXO/ZcJdraPWdv22uIEiLA4q7nvvCug8WTqzQveOH26fodo7g6uFe/a17W3+nFBAkRYENRdb1vkkz1CH9cPsVy/jrhr27PqMYvENYNlHAIesRiBYwRy0V+8iXP8+/fvX11Mr7L7ECueb/r48eMqm7FuI2BGWDEG8cm+7G3NEOfmdcTQw4h9/55lhm7DekRYKQPZF2ArbXTAyu4kDYB2YxUzwg0gi/41ztHnfQG26HbGel/crVrm7tNY+/1btkOEAZ2M05r4FB7r9GbAIdxaZYrHdOsgJ/wCEQY0J74TmOKnbxxT9n3FgGGWWsVdowHtjt9Nnvf7yQM2aZU/TIAIAxrw6dOnAWtZZcoEnBpNuTuObWMEiLAx1HY0ZQJEmHJ3HNvGCBBhY6jtaMoEiJB0Z29vL6ls58vxPcO8/zfrdo5qvKO+d3Fx8Wu8zf1dW4p/cPzLly/dtv9Ts/EbcvGAHhHyfBIhZ6NSiIBTo0LNNtScABFyNiqFCBChULMNNSdAhJyNSiECRCjUbEPNCRAhZ6NSiAARCjXbUHMCRMjZqBQiQIRCzTbUnAARcjYqhQgQoVCzDTUnQIScjUohAkQo1GxDzQkQIWejUogAEQo121BzAkTI2agUIkCEQs021JwAEXI2KoUIEKFQsw01J0CEnI1KIQJEKNRsQ80JECFno1KIABEKNdtQcwJEyNmoFCJAhELNNtScABFyNiqFCBChULMNNSdAhJyNSiECRCjUbEPNCRAhZ6NSiAARCjXbUHMCRMjZqBQiQIRCzTbUnAARcjYqhQgQoVCzDTUnQIScjUohAkQo1GxDzQkQIWejUogAEQo121BzAkTI2agUIkCEQs021JwAEXI2KoUIEKFQsw01J0CEnI1KIQJEKNRsQ80JECFno1KIABEKNdtQcwJEyNmoFCJAhELNNtScABFyNiqFCBChULMNNSdAhJyNSiECRCjUbEPNCRAhZ6NSiAARCjXbUHMCRMjZqBQiQIRCzTbUnAARcjYqhQgQoVCzDTUnQIScjUohAkQo1GxDzQkQIWejUogAEQo121BzAkTI2agUIkCEQs021JwAEXI2KoUIEKFQsw01J0CEnI1KIQJEKNRsQ80JECFno1KIABEKNdtQcwJEyNmoFCJAhELNNtScABFyNiqFCBChULMNNSdAhJyNSiEC/wGgKKC4YMA4TAAAAABJRU5ErkJggg=='
const updateStatusOptions = [
  { label: '全部', value: '' },
  { label: '锁定', value: '1' },
  { label: '未解锁', value: '0' }
]
const isShelfOptions = [
  { label: '全部', value: '' },
  { label: '上架', value: '1' },
  { label: '下架', value: '0' }
]
const category = ref([]) // 分类数据
const loading = ref(false)
const findRef = ref()
const findForm = reactive({
  commodityName: '', // 商品名称
  name: '', // 商品名称2
  commodityId: '', // 商品ID
  id: '', // 商品ID2
  state: '', //
  isShelf: '', // 1上架 0下架
  updateStatus: '', // 锁定状态 0未锁定 1锁定
  category: [null],
  categoryId: null,
  secondaryCategoryId: null
})
const modalGoodsOpen = ref(false) // 添加商品
const mallEvaluationOpen = ref(false) // 评价库
const memberEvaluationOpen = ref(false) // 会员评价
const currid = ref(null)

const dataSource = ref([])
const columns = ref([
  { title: '商品ID', dataIndex: 'id' },
  {
    title: '封面图',
    dataIndex: 'imgUrl1',
    customRender: ({ text, record }) => {
      return <a-image src={record.imgUrl1 || ''} fallback={fallbackImg} width={45} height={45} />
    }
  },
  { title: '商品名称', dataIndex: 'goodName', width: 300 },
  {
    title: '商品分类',
    customRender: ({ text, record }) => {
      if (record.secondaryCategoryId == 0) {
        return record.categoryName
      } else {
        return category.value.find(item => item.categoryId == record.categoryId)?.name || ''
      }
    }
  },
  {
    title: '二级分类',
    customRender: ({ text, record }) => {
      if (record.secondaryCategoryId != 0) {
        return record.categoryName
      }
    }
  },
  {
    title: '进货价格',
    dataIndex: 'systemPrice',
    customRender: ({ text, record }) => {
      return <>${record.systemPrice}</>
    }
  },
  {
    title: '是否上架',
    dataIndex: 'isShelf',
    customRender: ({ text, record }) => {
      return (
        <a-switch
          checked={record.isShelf}
          checkedValue={1}
          unCheckedValue={0}
          v-slots={{
            checkedChildren: () => <CheckOutlined />,
            unCheckedChildren: () => <CloseOutlined />
          }}
          onClick={() => {
            ApiChangeGoodMangeisShelf({ isShelf: record.isShelf == 1 ? 0 : 1, id: record.id })
              .then(res => {
                if (res.code == 0) {
                  record.isShelf = record.isShelf == 1 ? 0 : 1
                } else {
                  message.error(res.msg)
                }
              })
              .catch(err => {
                message.error(err)
              })
          }}
        />
      )
    }
  },
  {
    title: '解锁状态',
    dataIndex: 'updateStatus',
    customRender: ({ text, record }) => {
      return (
        <a-switch
          checked={record.updateStatus}
          checkedValue={1}
          unCheckedValue={0}
          v-slots={{
            checkedChildren: () => <CheckOutlined />,
            unCheckedChildren: () => <CloseOutlined />
          }}
          onClick={() => {
            ApiChangeGoodMangeUpdateStatus({ updateStatus: record.updateStatus == 1 ? 0 : 1, id: record.id })
              .then(res => {
                if (res.code == 0) {
                  record.updateStatus = record.updateStatus == 1 ? 0 : 1
                } else {
                  message.error(res.msg)
                }
              })
              .catch(err => {
                message.error(err)
              })
          }}
        />
      )
    }
  },
  { title: '排序', dataIndex: 'goodsSort' },
  { title: '销量', dataIndex: '' },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    customRender: ({ text, record }) => {
      return (
        <a-space>
          <a-button
            type="primary"
            size="small"
            disabled={!!record.updateStatus}
            onClick={() => {
              currid.value = record.id
              modalGoodsOpen.value = true
            }}
          >
            编辑
          </a-button>
          <a-button
            type="default"
            size="small"
            onClick={() => {
              currid.value = record.id
              mallEvaluationOpen.value = true
            }}
          >
            评价库
          </a-button>
          <a-button
            type="default"
            size="small"
            onClick={() => {
              currid.value = record.id
              memberEvaluationOpen.value = true
            }}
          >
            会员评价
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
  showSizeChanger: false,
  showQuickJumper: true,
  showTotal: (total, range) => `${range[0]}-${range[1]}, 共${total}条数据`,
  onChange(page, size) {
    pagination.current = page
    pagination.pageSize = size
    getLists()
  }
})

// 查询条件字段处理
const ctFindFields = () => {
  const parameter = {
    ...findForm
  }
  if (Array.isArray(findForm.category) && findForm.category.every(cat => cat !== null)) {
    parameter.categoryId = findForm.category[0]
    parameter.secondaryCategoryId = findForm.category[1]
  } else {
    parameter.categoryId = undefined
    parameter.secondaryCategoryId = undefined
  }
  delete parameter.category
  return parameter
}

// 查询
const handleFind = () => {
  pagination.current = 1
  getLists(ctFindFields())
}
// 重置+查询
const handleResetFind = () => {
  findRef.value.resetFields()
  findForm.name = ''
  findForm.id = ''
  handleFind()
}
//
const handleAddGoodsModal = () => {
  currid.value = null
  modalGoodsOpen.value = true
}

const resetList = () => {
  pagination.current = 1
  getLists()
}

// 获取分类数据
const getCategory = async () => {
  try {
    const { data } = await ApiGoodCategory({ lang: 'en' })
    if (data && data.length) {
      const copiedData = data.slice()
      copiedData.unshift({ name: '全部', categoryId: null, id: '0', subList: null })
      copiedData.forEach(element => {
        if (element.subList && element.subList.length) {
          element.subList.unshift({ name: '无二级分类', categoryId: '0', subList: null })
        }
      })
      category.value = copiedData
    } else {
      category.value = []
    }
  } catch (error) {
    console.error('Error fetching category data:', error)
  }
}
// 获取商品库列表
const getLists = async (params = {}) => {
  if (loading.value) {
    return
  }
  loading.value = true
  try {
    const dataRes = await ApiGoodMange({
      ...params,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    })
    if (dataRes.code == 0) {
      const { thisPageNumber, pageSize, totalElements, elements } = dataRes.data
      dataSource.value = elements ?? []
      pagination.pageSize = pageSize
      pagination.total = totalElements
    }
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
}
onBeforeMount(() => {
  getCategory()
  getLists(ctFindFields())
})
</script>
<template>
  <div class="app-container">
    <a-flex gap="middle" vertical>
      <a-form ref="findRef" :model="findForm">
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item label="商品名称" name="commodityName">
              <a-input
                v-model:value="findForm.commodityName"
                @change="e => (findForm.name = e.target.value)"
                placeholder="请输入商品名称"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="商品ID" name="commodityId">
              <a-input v-model:value="findForm.commodityId" @change="e => (findForm.id = e.target.value)" placeholder="请输入商品ID" allowClear />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="商品分类" name="category">
              <a-cascader
                v-model:value="findForm.category"
                placeholder="请选择商品分类"
                allowClear
                :options="category"
                :field-names="{ label: 'name', value: 'categoryId', children: 'subList' }"
                changeOnSelect
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="商品状态" name="isShelf">
              <a-select v-model:value="findForm.isShelf" :options="isShelfOptions" placeholder="商品状态" allowClear></a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="锁定状态" name="updateStatus">
              <a-select v-model:value="findForm.updateStatus" :options="updateStatusOptions" placeholder="请选择锁定状态" allowClear></a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item>
              <a-space>
                <a-button type="primary" @click="handleFind">查询</a-button>
                <a-button type="primary" @click="handleResetFind">重置</a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <a-space>
        <a-button type="primary" @click="handleAddGoodsModal">添加商品</a-button>
      </a-space>
      <div class="mt-t15">
        <a-table
          :loading="loading"
          :rowKey="record => record.uuid"
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :scroll="{ x: 'max-content' }"
        />
      </div>
    </a-flex>
    <!--  -->
    <modalGoods v-model:open="modalGoodsOpen" :id="currid" @resetList="resetList" />
    <!--  -->
    <mallEvaluation v-model:open="mallEvaluationOpen" :id="currid" />
    <!--  -->
    <memberEvaluation v-model:open="memberEvaluationOpen" :id="currid" />
  </div>
</template>
