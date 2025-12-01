import axios from "axios";
import { notification } from 'ant-design-vue';


const instance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_URL,
  timeout: 60000
});


instance.interceptors.request.use(
  config => { return config },
  error => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      const { data, status, statusText } = error.response
      notification.error({ message: data.errorMsg, description: data.errorCode })
    }
    return Promise.reject(error)
  }
)

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_URL2,
  timeout: 60000
})

service.interceptors.request.use(
  config => { return config },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      const { data, status, statusText } = error.response
      notification.error({ message: data.errorMsg, description: data.errorCode })
    }
    return Promise.reject(error)
  }
)

export { instance, service }