<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ApiGoodCategory, ApigetAllAttributeCategory, ApiGetAttrCategoryList, ApiUpdateGoods, ApiGetGoodsDesc } from '@/api/api.js'
import { clone, pick, omit } from 'xe-utils'
import addIcon from './components/addIcon/addIcon.vue'
import sku from './components/sku/sku.vue'
import addAttr from './components/addAttr/addAttr.vue'
import uploadAttrImg from '@/views/mallGoods/components/goods/components/uploadAttrImg/uploadAttrImg.vue'
import wangEditor from '@/components/wangeditor/wangeditor.vue'
import { message } from 'ant-design-vue'

const props = defineProps({
  open: { type: Boolean, default: () => false },
  id: { type: [String, Number] }
})
const emits = defineEmits(['update:open'])
const title = computed(() => (props.id ? '编辑产品' : '添加产品'))

const IMGSEQ = ['imgUrl1', 'imgUrl2', 'imgUrl3', 'imgUrl4', 'imgUrl5', 'imgUrl6', 'imgUrl7', 'imgUrl8', 'imgUrl9', 'imgUrl10']
const langIn = [
  { label: '英文', value: 'en' },
  { label: '中文', value: 'cn' },
  { label: '中文繁体', value: 'tw' },
  { label: '德语', value: 'de' },
  { label: '法语', value: 'fr' },
  { label: '日语', value: 'jr' },
  { label: '韩语', value: 'ko' },
  { label: '马来语', value: 'ma' },
  { label: '泰语', value: 'th' },
  { label: '葡萄牙语', value: 'pt' },
  { label: '西班牙语', value: 'es' },
  { label: '俄罗斯语', value: 'ru' },
  { label: '希腊语', value: 'el' },
  { label: '意大利语', value: 'it' },
  { label: '土耳其语', value: 'tr' },
  { label: '南非荷兰语', value: 'af' },
  { label: '越南语', value: 'vi' },
  { label: '印度语', value: 'hi' },
  { label: '印度尼西亚语', value: 'id' },

]
const convertSkusToAttributes = skus => {
  const attributes = []
  skus.forEach(sku => {
    sku.attrs.forEach(attr => {
      const existingAttribute = attributes.find(a => a.attrId === attr.attrId)
      if (existingAttribute) {
        // 如果已经存在相同的属性ID，则向该属性的attrValues中添加新的值
        const existingAttrValue = existingAttribute.attrValues.find(av => av.attrValueId === attr.attrValueId)
        if (!existingAttrValue) {
          existingAttribute.attrValues.push({
            attrValueId: attr.attrValueId,
            attrValueName: attr.attrValueName,
            iconImg: attr.iconImg,
            icon: attr.icon,
            checked: true
          })
        }
      } else {
        // 如果不存在该属性ID，则创建一个新的属性对象
        attributes.push({
          attrId: attr.attrId,
          attrName: attr.attrName,
          attrValues: [
            {
              attrValueId: attr.attrValueId,
              attrValueName: attr.attrValueName,
              iconImg: attr.iconImg,
              icon: attr.icon,
              checked: true
            }
          ]
        })
      }
    })
  })
  console.log(attributes)
  return attributes
}

const skuRef = ref()
const formRef = ref()
const formdata = reactive({
  lang: 'en',
  name: '', // 产品名称
  categoryId: null, // 产品分类
  secondaryCategoryId: null, // 二级分类
  goodsSort: 0, // 产品排序
  unit: '', // 单位
  buyMin: 1, // 最小采购数量
  isRefund: 0, // 是否可退款 1可退款 0不可退款
  systemPrice: 0, // 进货价格
  imgUrl1: '',
  imgUrl2: '',
  imgUrl3: '',
  imgUrl4: '',
  imgUrl5: '',
  imgUrl6: '',
  imgUrl7: '',
  imgUrl8: '',
  imgUrl9: '',
  imgUrl10: '',
  link: '', // 外部产品链接
  freightType: 0, // 运费设置 1否 0是
  freightAmount: 0, // 运费
  goodsTax: 0, // 税收费用
  des: '', // 产品描述
  imgDes: '' // 产品描述图片
})

const rules = {
  name: [
    { required: true, message: '请输入产品名称' },
    { max: 255, message: '名称最大254个字符' }
  ],
  categoryId: [{ required: true, message: '请选择产品分类' }],
  unit: [{ required: true, message: '请填写产品单位' }],
  // attributeCategoryId: [{ required: true, message: '请选择产品属性' }],
  // categoryID: [{ required: true, message: '请选择产品属性规格' }],
  buyMin: [{ required: true, message: '最小采购数量' }],
  isRefund: [{ required: true, message: '请选择是否可退款' }],
  systemPrice: [{ required: true, message: '请填写产品价格' }]
}

// 分类
const categoryOptions = ref([])
const getCategory = async params => {
  try {
    const { data } = await ApiGoodCategory(params)
    let cate = data.map(item => {
      item.subList = [{ name: '无二级分类', categoryId: '0', id: '0', status: 1, subList: null }, ...(item.subList?.length && item.subList)]
      return item
    })
    categoryOptions.value = cate ?? []
  } catch (error) {
    console.log(error)
  }
}
// 属性
const attributeCategory = ref([])
const getAttr = async params => {
  try {
    const dataRes = await ApigetAllAttributeCategory(params)
    attributeCategory.value = dataRes.data ?? []
  } catch (err) {
    console.log(err)
  }
}

// 远程数据
const originSkus = ref(null)

// 切换属性
const attributes = ref([])
const handleChangeAttributeCategory = async categoryId => {
  if (!categoryId) {
    attributes.value = []
    return
  }
  try {
    getAttrCategory({ lang: formdata.lang, categoryId })
  } catch (error) {
    console.error('Error fetching attribute category:', error)
  }
}
const getAttrCategory = async params => {
  try {
    const { data } = await ApiGetAttrCategoryList(params)
    if (data.length) {
      attributes.value =
        data?.map(item => ({
          ...item,
          attrValues: item.attrValues.map(attrItem => reactive({ ...attrItem, checked: false }))
        })) || []
    }
  } catch (error) {
    console.error('Error fetching attribute categories:', error)
  }
}

// 切换语言
const handleChangeLang = e => {
  formdata.lang = e.target.value
  getCategory({ lang: formdata.lang })
  getAttr({ lang: formdata.lang, categoryId: 'undefined' })
  if (props.id) {
    getDesc()
  }
}
// 提交
const confirmLoading = ref(false)
const handleSubmit = async () => {
  if (confirmLoading.value) {
    return
  }
  confirmLoading.value = true
  try {
    await formRef.value.validate()
    const parameter = {
      ...formdata,
      categoryId: formdata.categoryId.length == 1 || formdata.categoryId.length == 2 ? formdata.categoryId[0] : '',
      secondaryCategoryId: formdata.categoryId.length == 1 ? '0' : formdata.categoryId.length == 2 ? formdata.categoryId[1] : '',
      skus: skuRef.value.skuList || []
    }
    let dataRes = null
    dataRes = await ApiUpdateGoods(parameter)
    if (dataRes.code == 0) {
      message.success('新增成功')
      handleCancel()
      // emits('resetList')
    }
  } catch (error) {
    console.log(error)
  } finally {
    confirmLoading.value = false
  }
}
// 取消模态框
const handleCancel = () => {
  emits('update:open', false)
  formRef.value.resetFields()
  categoryOptions.value = []
  attributeCategory.value = []
  originSkus.value = null
  attributes.value = []
}
// 获取数据
const getDesc = async () => {
  try {
    const dataRes = await ApiGetGoodsDesc({ lang: formdata.lang, goodsId: props.id })
    if (dataRes.code === '0') {
      const { categoryId, secondaryCategoryId, goodSkuAttrDto } = dataRes.data
      const params = {
        ...omit(dataRes.data, ['categoryId', 'secondaryCategoryId', 'upTime', 'remindNum', 'lastAmount', 'createTime', 'goodSkuAttrDto']),
        categoryId: secondaryCategoryId === '0' ? [categoryId] : [categoryId, secondaryCategoryId]
      }
      Object.assign(formdata, params)
      attributes.value = convertSkusToAttributes(goodSkuAttrDto?.skus || [])
      originSkus.value = goodSkuAttrDto?.skus || null
    } else {
      message.error(dataRes.msg)
    }
  } catch (error) {
    console.log(error)
  }
}

watch([() => props.open, () => props.id], nval => {
  if (nval[0]) {
    getCategory({ lang: formdata.lang })
    getAttr({ lang: formdata.lang, categoryId: 'undefined' })
    if (nval[1]) {
      getDesc()
    }
  }
})
</script>
<template>
  <a-modal :open="open" :title="title" centered width="860px" @cancel="handleCancel" destroyOnClose :maskClosable="false">
    <template #footer>
      <a-button @click="handleCancel">取消</a-button>
      <a-button type="primary" @click="handleSubmit">提交</a-button>
    </template>
    <a-form ref="formRef" :model="formdata" :rules="rules">
      <a-card size="small">
        <a-radio-group v-model:value="formdata.lang" name="lang" @change="handleChangeLang">
          <a-row :gutter="[8, 8]">
            <a-col v-for="item in langIn" :key="item.value" :span="4">
              <a-radio :value="item.value">{{ item.label }}</a-radio>
            </a-col>
          </a-row>
        </a-radio-group>
      </a-card>
      <a-card size="small" title="基本信息" class="mr-t12">
        <a-row :gutter="12">
          <a-col :span="24">
            <a-form-item label="产品名称" name="name">
              <a-input v-model:value="formdata.name" placeholder="请输入产品名称" :rows="1" show-count :maxlength="254" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产品分类" name="categoryId">
              <a-cascader
                v-model:value="formdata.categoryId"
                show-search
                placeholder="请选择产品分类"
                changeOnSelect
                :options="categoryOptions"
                :field-names="{ label: 'name', value: 'categoryId', children: 'subList' }"
                tree-node-filter-prop="name"
                allowClear
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="产品单位" name="unit">
              <a-input v-model:value="formdata.unit" placeholder="请输入单位" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="最小采购数量" name="buyMin">
              <a-input v-model:value.number="formdata.buyMin" placeholder="最小采购数量" class="w100pre" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="产品属性" name="attributeCategoryId">
              <a-flex gap="middle" vertical>
                <a-select
                  v-model:value="formdata.attributeCategoryId"
                  :options="attributeCategory"
                  :field-names="{ label: 'attrName', value: 'id' }"
                  placeholder="请选择产品属性"
                  allowClear
                  @change="handleChangeAttributeCategory"
                />
                <a-form-item-rest>
                  <a-flex gap="middle" vertical>
                    <addIcon v-model:attributes="attributes" />
                    <a-card v-for="(item, idx) in attributes" :key="item.attrId" :title="item.attrName" size="small">
                      <a-row :gutter="12" align="top" :wrap="false">
                        <a-col flex="auto">
                          <a-checkbox v-for="attr in item.attrValues" :key="attr.attrValueId" v-model:checked="attr.checked">
                            {{ attr.attrValueName }}
                          </a-checkbox>
                        </a-col>
                        <a-col flex="240px">
                          <addAttr v-model:attributeItem="attributes[idx]" :lang="formdata.lang" />
                        </a-col>
                      </a-row>
                    </a-card>
                    <sku ref="skuRef" :attributes="attributes" :originSkus="originSkus" />
                  </a-flex>
                </a-form-item-rest>
              </a-flex>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="外部链接" name="link">
              <a-input v-model:value="formdata.link" placeholder="请输入外部产品链接"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="进货价格" name="systemPrice">
              <a-input-number v-model:value="formdata.systemPrice" :min="0" placeholder="进货价格" class="w100pre" />
            </a-form-item>
          </a-col>

          <a-col :span="12">
            <a-form-item label="产品排序" name="goodsSort">
              <a-input v-model:value.number="formdata.goodsSort" placeholder="产品排序" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="可否退款" name="isRefund">
              <a-switch
                v-model:checked="formdata.isRefund"
                :checkedValue="1"
                :unCheckedValue="0"
                checked-children="是"
                un-checked-children="否"
              ></a-switch>
            </a-form-item>
          </a-col>
        </a-row>
      </a-card>
      <a-card size="small" title="封面 图片尺寸：(750 * 750)" class="mr-t12">
        <a-flex wrap="wrap">
          <a-form-item v-for="(item, idx) in 10" :key="idx" :name="'imgUrl' + (idx + 1)">
            <uploadAttrImg v-model:value="formdata['imgUrl' + (idx + 1)]" accept="image/*" :maxCount="1" />
            <div style="text-align: center">{{ '封面' + (idx + 1) }}</div>
          </a-form-item>
        </a-flex>
      </a-card>
      <a-card size="small" title="运费设置" class="mr-t12">
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="运费设置" name="freightType">
              <a-switch v-model:checked="formdata.freightType" :checkedValue="1" :unCheckedValue="0" checked-children="否" un-checked-children="是" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="运费" name="freightAmount">
              <a-input-number :disabled="!formdata.freightType" v-model:value="formdata.freightAmount" :min="0" class="w100pre" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-card>
      <a-card size="small" title="税收费用" class="mr-t12">
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="税收费用" name="goodsTax">
              <a-input v-model:value.number="formdata.goodsTax" class="w100pre" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-card>
      <a-form-item class="mr-t12">
        <wangEditor v-model:value="formdata.des" label="产品描述" />
      </a-form-item>
      <a-form-item class="mr-t12">
        <wangEditor v-model:value="formdata.imgDes" label="图片介绍" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<style scoped>
.tableContainer tr > th {
  padding: 8px 12px;
  border: 1px solid #e8e8e8;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 500;
  text-align: center;
  background: #fafafa;
}
.tableContainer tr > td {
  border: 1px solid #e8e8e8;
  padding: 6px 8px;
}
</style>
