import { defineStore } from 'pinia'
import { useStorage } from '@vueuse/core'
import { GET_USERINFO, GET_BALANCE } from '@/store/types.store'
import { usekeepAliveStore } from "@/store/keepAlive.js";
import { _info, _getBalance } from "@/service/user.api.js";
import { useChatStore } from "@/store/chat.js";
import router from './../router';

export const useUserStore = defineStore('user', {
    persist: true,
    state: () => ({
        userInfo: {
            token: '',
        },
        sessionUUID: ''
    }),
    getters: {
    },
    actions: {
        async [GET_USERINFO](userInfoObj) { // 发送请求获取信息
            this.userInfo = userInfoObj
            let data = await _info()
            this.userInfo = { ...this.userInfo, ...data }
            let res = await _getBalance()
            let obj = { ...res }
            this.userInfo = { ...this.userInfo, ...obj }
        },
        async getUserInfo(flag, token) {
            if (this.userInfo.token && !flag) {
                // 本地有token，无需强制刷新
                if (!this.userInfo.username) {
                    const data = await _info()
                    this.userInfo = { ...this.userInfo, ...data }
                }
                return this.userInfo
            } else {
                if (token) {
                    this.userInfo.token = token
                }
                const TOKEN = JSON.parse(JSON.stringify(this.userInfo.token))
                let data
                try {
                    data = await _info()
                } catch (err) {
                    throw err   // 这里直接抛异常
                }

                try {
                    const res = await _getBalance()
                    const obj = { ...res }
                    this.userInfo = { ...data, ...obj }
                    if (TOKEN) this.userInfo.token = TOKEN
                    return this.userInfo
                } catch (err) {
                    throw err   // 这里也抛
                }
            }
        },
        async [GET_BALANCE]() {
            const res = await _getBalance()
            const obj = { ...res }
            this.userInfo = { ...this.userInfo, ...obj }
        },
        async logout(flag = false) {
            const keepAliveStore = usekeepAliveStore()
            keepAliveStore.clearKeepAlive()

            document.dispatchEvent(new CustomEvent('logout'))
            this.userInfo = {
                token: ''
            }
            const shopStore = useShopInfoStore()
            shopStore.setShopInfo({})
            const path = router.currentRoute.value.path

            if (sessionStorage.getItem('msgRequset')) {
                document.dispatchEvent(new CustomEvent('clearMsgRequset'))
            }

            await localStorage.removeItem('sellerId')

            useChatStore().closeChatHandle()

            if (path !== '/login' && !flag) {
                router.push('/login')
            }
        }
    },
})

export const useShopInfoStore = defineStore('shopInfo', {
    state: () => useStorage('shopInfo', {
        shopInfo: {}
    }),
    actions: {
        setShopInfo(obj) {
            this.shopInfo = obj
        }
    }
})
