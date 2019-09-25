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
        component: () => import("@/views/CustomerList"),
        name: "Clientes"
      },
      {
        path: "/new-customer/",
        component: () => import("@/views/CustomerCreation"),
        name: "Nuevo cliente"
      }
    ]
});