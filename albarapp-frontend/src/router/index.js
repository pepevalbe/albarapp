import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: "/",
      component: () => import("@/views/HomePage"),
      name: "Home"
    },
    {
      path: "/product-list/",
      component: () => import("@/views/ProductList"),
      name: "Productos"
    },
    {
      path: "/customer-list/",
      component: () => import("@/views/CustomerList"),
      name: "Clientes"
    },
    {
      path: "/customer-creation/",
      component: () => import("@/views/CustomerCreation"),
      name: "Nuevo cliente"
    }
  ]
});