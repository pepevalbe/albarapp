import Vue from 'vue'
import App from './App.vue'
import router from "./router"
import vuetify from './plugins/vuetify';
import moment from 'moment';

Vue.config.productionTip = false

Vue.prototype.$moment = moment;

Vue.mixin({
  methods: {
    alertError: function (error) {
      if (
        error != null &&
        error.response != null &&
        error.response.data != null &&
        error.response.data.errorMessage != null &&
        error.response.data.errorCode != null
      ) {
        alert(error.response.data.errorCode + ' : ' + error.response.data.errorMessage)
      } else {
        alert('Ha ocurrido un error, por favor inténtelo de nuevo más adelante')
      }
    }
  }
})

new Vue({
  router,
  vuetify,
  render: h => h(App)
}).$mount('#app')