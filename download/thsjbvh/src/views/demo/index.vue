<script setup>
import { onMounted, reactive, ref, toRefs } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const categoryId = ref([null])
const category = ref([
  {
    id: 'ff80808184809ef9018480ab04a30010',
    name: 'Food & Beverage',
    children: [
      {
        id: 'ff808081882b196f018848562d1a0000',
        name: 'Mineral Water'
      },
      {
        id: 'ff808081882b196f0188485670550002',
        name: 'Juice'
      }
    ]
  },
  {
    id: 'ff80808184809ef9018480a8a8ef0008',
    name: "Men's Clothing",
    children: [
      {
        id: 'ff808081875b781101875bf130a600ba',
        name: 'Tie'
      },
      {
        id: 'ff808081875b781101875bf184de00bc',
        name: 'Bow Tie'
      }
    ]
  }
])

const form = reactive({
  name: '',
  lang: 'en',
  categoryId: undefined
})

const handleChange = value => {
  console.log(value)
  // if(typeof value === 'undefined'){
  //   categoryId.value = [null]
  // }
}
const setCategory = () => {
  category.value.unshift({ id: null, name: '全部', children: null })
  category.value.forEach(item => {
    if (item.children && item.children.length) {
      item.children.unshift({ id: '0', name: '无二级分类', children: null })
    }
  })
  console.log(category.value)
}

const handleFind = () => {
  const params = {...form}
  console.log(params)
}

onMounted(() => {
  setCategory()
})
</script>
<template>
  <div>
    <!-- <router-view /> -->
    <a-cascader
      style="width: 300px"
      v-model:value="form.categoryId"
      placeholder="请选择商品分类"
      allowClear
      :options="category"
      :field-names="{ label: 'name', value: 'id', children: 'children' }"
      changeOnSelect
      @change="handleChange"
    />
    <a-button @click="handleFind">查询</a-button>
    <div>
      {{ categoryId }}
    </div>
  </div>
</template>
