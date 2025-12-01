/*
 * @Description: 
 * @Version: 
 * @Autor: O2AM
 * @Date: 2024-03-18 16:44:28
 * @LastEditors: O2AM
 * @LastEditTime: 2024-03-18 17:28:43
 */
import { defineConfig } from 'vite'
import path from 'path'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx()
  ],
  resolve: {
    alias: [
      {
        find: '@',
        replacement: path.resolve(__dirname, 'src')
      }
    ]
  },
  server: {
    open: true,
    port: 5052,
    host: '0.0.0.0',
    hmr: true,
    proxy: {
      '/summer': {
        target: 'http://192.168.1.248:8010/',
        changeOrigin: true,
        // pathRewrite: { '^/wap': '' } // Vue CLI 配置
        // rewrite: (path) => path.replace(/^\/wap/, '/')
      }
    }
  },
})
