import Vue from 'vue'
import App from '@/App.vue'
import router from "@/router"
import vuetify from '@/plugins/vuetify';
import moment from 'moment';
import { store } from '@/store/store'
import HighchartsVue from 'highcharts-vue'

Vue.config.productionTip = false

Vue.prototype.$moment = moment;

Vue.use(HighchartsVue);

Vue.mixin({
  methods: {
    showSpinner() {
      this.spinner.counter++;
      if (this.spinner.counter) this.spinner.loading = true;
    },
    closeSpinner() {
      this.spinner.counter--;
      if (!this.spinner.counter) this.spinner.loading = false;
    },
    currencyFormatted(value) {
      return value.toLocaleString("es-ES", {
        style: "currency",
        currency: "EUR"
      });
    },
    dateFormatted(timestamp) {
      return this.$moment.utc(timestamp, "x").format("DD/MM/YYYY");
    },
    booleanFormatted(value) {
      return (value ? 'Sí' : 'No')
    }
  }
})

new Vue({
  router,
  vuetify,
  store,
  render: h => h(App)
}).$mount('#app')