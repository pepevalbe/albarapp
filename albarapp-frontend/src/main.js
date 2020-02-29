import Vue from 'vue'
import App from './App.vue'
import router from "./router"
import vuetify from './plugins/vuetify';
import moment from 'moment';

Vue.config.productionTip = false

Vue.prototype.$moment = moment;

Vue.mixin({
  data: () => {
    return {
      get token() {
        return localStorage.getItem('token') || 0;
      },
      set token(value) {
        if (value === null) {
          localStorage.removeItem('token');
        } else {
          localStorage.setItem('token', value);
        }
      }
    };
  },
  computed: {
    parsedToken: () => {
      var token = localStorage.getItem('token');
      if (token) {
        var base64Url = localStorage.getItem('token').split('Bearer ')[1].split('.')[1];
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        var jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
      } else {
        return "";
      }
    }
  },
  methods: {
    setToken: function (token) {
      this.token = token;
    },
    logout: function () {
      localStorage.clear();
      this.$router.go();
    },
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
  render: h => h(App)
}).$mount('#app')