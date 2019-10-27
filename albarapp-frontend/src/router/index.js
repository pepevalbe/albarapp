import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: "/",
      component: () => import("@/views/HomePage"),
      name: "HomePage",
      meta: { headerName: "Home" }
    },
    {
      path: "/login",
      component: () => import("@/views/LoginPage"),
      name: "LoginPage",
      meta: { headerName: "Login" }
    },
    {
      path: "/admin",
      component: () => import("@/views/admin/AdminPage"),
      name: "AdminPage",
      meta: { headerName: "Administración de usuarios" }
    },
    {
      path: "/invitation",
      component: () => import("@/views/admin/InvitationForm"),
      name: "InvitationForm",
      meta: { headerName: "Enviar invitación" }
    },
    {
      path: "/user-creation",
      component: () => import("@/views/admin/UserCreation"),
      name: "UserCreation",
      meta: { headerName: "Creación de usuario" }
    },
    {
      path: "/product-list/",
      component: () => import("@/views/ProductList"),
      name: "ProductList",
      meta: { headerName: "Productos" }
    },
    {
      path: "/product-creation/",
      component: () => import("@/views/ProductCreation"),
      name: "ProductCreation",
      meta: { headerName: "Nuevo producto" }
    },
    {
      path: "/product-update/",
      component: () => import("@/views/ProductUpdate"),
      props: true,
      name: "ProductUpdate",
      meta: { headerName: "Actualizar producto" }
    },
    {
      path: "/customer-list/",
      component: () => import("@/views/CustomerList"),
      name: "CustomerList",
      meta: { headerName: "Clientes" }
    },
    {
      path: "/customer-creation/",
      component: () => import("@/views/CustomerCreation"),
      name: "CustomerCreation",
      meta: { headerName: "Nuevo cliente" }
    },
    {
      path: "/customer-detail/",
      component: () => import("@/views/CustomerDetail"),
      props: true,
      name: "CustomerDetail",
      meta: { headerName: "Detalle de cliente" }
    },
    {
      path: "/customer-update/",
      component: () => import("@/views/CustomerUpdate"),
      props: true,
      name: "CustomerUpdate",
      meta: { headerName: "Actualizar cliente" }
    },
    {
      path: "/delivery-note-list/",
      component: () => import("@/views/DeliveryNoteList"),
      name: "DeliveryNoteList",
      meta: { headerName: "Albaranes" }
    },
    {
      path: "/delivery-note-creation/",
      component: () => import("@/views/DeliveryNoteCreation"),
      name: "DeliveryNoteCreation",
      meta: { headerName: "Nuevo albarán" }
    },
    {
      path: "/delivery-note-update/",
      component: () => import("@/views/DeliveryNoteUpdate"),
      props: true,
      name: "DeliveryNoteUpdate",
      meta: { headerName: "Modificar albarán" }
    },
    {
      path: "/invoice-list/",
      component: () => import("@/views/InvoiceList"),
      name: "InvoiceList",
      meta: { headerName: "Facturas" }
    }
  ]
});