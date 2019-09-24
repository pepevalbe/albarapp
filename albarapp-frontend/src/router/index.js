import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
    routes: [
      {
        path: "/",
        component: () => import("@/views/Home"),
        name: "Home"
      },
      {
        path: "/customer/",
        component: () => import("@/views/Customer"),
        name: "Clientes"
      },
      {
        path: "/new-customer/",
        component: () => import("@/views/NewCustomer"),
        name: "Nuevo cliente"
      }
    ]
});