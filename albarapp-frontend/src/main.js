import Vue from 'vue'
import App from './App.vue'
import router from "./router"
import vuetify from './plugins/vuetify';
import moment from 'moment';
import { store } from './store/store'

Vue.config.productionTip = false

Vue.prototype.$moment = moment;

Vue.mixin({
  methods: {
    showSpinner() {
      this.spinner.counter++;
      if (this.spinner.counter) this.spinner.loading = true;
    },
    closeSpinner() {
      this.spinner.counter--;
      if (!this.spinner.counter) this.spinner.loading = false;
    }
  }
})

new Vue({
  router,
  vuetify,
  store,
  render: h => h(App)
}).$mount('#app')