import { createPinia } from 'pinia'
import { createApp } from 'vue'
import Antd from 'ant-design-vue';
import Root from './App.vue'
import 'ant-design-vue/dist/reset.css';
import './style.css'
import router from './router'

import VXETable from 'vxe-table'
import 'vxe-table/lib/style.css'

const pinia = createPinia()
const app = createApp(Root)


app.use(pinia)
app.use(router)
app.use(Antd)
app.use(VXETable)
app.mount('#app')

