import Vue from 'vue'
import App from './App.vue'
import router from "./router"
import vuetify from './plugins/vuetify';
import axios from 'axios'

Vue.config.productionTip = false

Vue.use({
  install (Vue) {
    Vue.prototype.$axios = axios.create({
      baseURL: process.env.VUE_APP_API_URL
    })
    Vue.prototype.$axios.defaults.timeout = 10000
    Vue.prototype.$axios.interceptors.request.use(function (config) {
      var token = localStorage.token
      if (token) {
        config.headers.common['bearer'] = token
      }
      return config
    })
  }
})

new Vue({
  router,
  vuetify,
  render: h => h(App)
}).$mount('#app')
