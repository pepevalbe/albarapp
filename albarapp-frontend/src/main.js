import Vue from 'vue'
import App from './App.vue'
import router from "./router"
import vuetify from './plugins/vuetify';
import moment from 'moment';

Vue.prototype.$moment = moment;
Vue.config.productionTip = false

new Vue({
  router,
  vuetify,
  render: h => h(App)
}).$mount('#app')