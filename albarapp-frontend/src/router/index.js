import Vue from "vue";
import Router from "vue-router";
import { store } from '@/store/store';

Vue.use(Router);

const router = new Router({
  routes: [
    { path: '*', redirect: '/billing/statistics' },
    {
      path: "/",
      component: () => import("@/views/Home"),
      name: "Home",
      meta: { headerName: "Home" }
    },
    {
      path: "/billing/statistics",
      component: () => import("@/views/billing/statistics/BillingStatistics"),
      name: "BillingStatistics",
      meta: { headerName: "Estadísticas de facturación" }
    },
    {
      path: "/login",
      component: () => import("@/views/Login"),
      name: "Login",
      meta: { headerName: "Login" }
    },
    {
      path: "/admin",
      component: () => import("@/views/admin/AdminPage"),
      name: "AdminPage",
      meta: { headerName: "Administración de usuarios" }
    },
    {
      path: "/admin/invitation",
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
      path: "/billing/products/",
      component: () => import("@/views/billing/products/ProductHome"),
      children: [
        {
          path: "",
          component: () => import("@/views/billing/products/ProductList"),
          name: "ProductList",
          meta: { headerName: "Productos" }
        },
        {
          path: "creation/",
          component: () => import("@/views/billing/products/ProductCreation"),
          name: "ProductCreation",
          meta: { headerName: "Nuevo producto" }
        },
        {
          path: "update/:productId",
          component: () => import("@/views/billing/products/ProductUpdate"),
          props: true,
          name: "ProductUpdate",
          meta: { headerName: "Actualizar producto" }
        },
      ]
    },
    {
      path: "/billing/customers/",
      component: () => import("@/views/billing/customers/CustomerHome"),
      children: [
        {
          path: "",
          component: () => import("@/views/billing/customers/CustomerList"),
          name: "CustomerList",
          meta: { headerName: "Clientes" }
        },
        {
          path: "creation",
          component: () => import("@/views/billing/customers/CustomerCreation"),
          name: "CustomerCreation",
          meta: { headerName: "Nuevo cliente" }
        },
        {
          path: "detail/:customerId",
          component: () => import("@/views/billing/customers/CustomerDetail"),
          props: true,
          name: "CustomerDetail",
          meta: { headerName: "Detalle de cliente" }
        },
        {
          path: "update/:customerId",
          component: () => import("@/views/billing/customers/CustomerUpdate"),
          props: true,
          name: "CustomerUpdate",
          meta: { headerName: "Actualizar cliente" }
        }
      ]
    },
    {
      path: "/billing/delivery-notes/",
      component: () => import("@/views/billing/deliveryNotes/DeliveryNoteHome"),
      children: [
        {
          path: "",
          component: () => import("@/views/billing/deliveryNotes/DeliveryNoteList"),
          name: "DeliveryNoteList",
          meta: { headerName: "Albaranes" }
        },
        {
          path: "creation/",
          component: () => import("@/views/billing/deliveryNotes/DeliveryNoteCreation"),
          name: "DeliveryNoteCreation",
          meta: { headerName: "Nuevo albarán" }
        },
        {
          path: "update/:deliveryNoteId",
          component: () => import("@/views/billing/deliveryNotes/DeliveryNoteUpdate"),
          props: true,
          name: "DeliveryNoteUpdate",
          meta: { headerName: "Modificar albarán" }
        },
      ]
    },
    {
      path: "/billing/invoices/",
      component: () => import("@/views/billing/invoices/InvoiceHome"),
      children: [
        {
          path: "",
          component: () => import("@/views/billing/invoices/InvoiceList"),
          name: "InvoiceList",
          meta: { headerName: "Facturas" }
        },
        {
          path: "update/:invoiceId",
          component: () => import("@/views/billing/invoices/InvoiceUpdate"),
          props: true,
          name: "InvoiceUpdate",
          meta: { headerName: "Modificar factura" }
        },
        {
          path: "bill-process/",
          component: () => import("@/views/billing/invoices/BillProcess"),
          name: "BillProcess",
          meta: { headerName: "Facturar" }
        }
      ]
    },
    {
      path: "/production/hens-batches/",
      component: () => import("@/views/production/hensBatches/HensBatchHome"),
      children: [
        {
          path: "",
          component: () => import("@/views/production/hensBatches/HensBatchList"),
          name: "HensBatchList",
          meta: { headerName: "Lotes" }
        },
        {
          path: "creation/",
          component: () => import("@/views/production/hensBatches/HensBatchCreation"),
          name: "HensBatchCreation",
          meta: { headerName: "Nuevo lote" }
        },
        {
          path: "update/:hensBatchId",
          component: () => import("@/views/production/hensBatches/HensBatchUpdate"),
          props: true,
          name: "HensBatchUpdate",
          meta: { headerName: "Modificar lote" }
        },
        {
          path: "statistics/:hensBatchId",
          component: () => import("@/views/production/hensBatches/HensBatchStatistics"),
          props: true,
          name: "HensBatchStatistics",
          meta: { headerName: "Estadísticas lote" }
        }
      ]
    },
    {
      path: "/production/hens-batch-reports/",
      component: () => import("@/views/production/hensBatchReports/HensBatchReportHome"),
      children: [
        {
          path: "",
          component: () => import("@/views/production/hensBatchReports/HensBatchReportList"),
          name: "HensBatchReportList",
          meta: { headerName: "Reporte diario" }
        },
        {
          path: "creation/:hensBatchId",
          component: () => import("@/views/production/hensBatchReports/HensBatchReportCreation"),
          props: true,
          name: "HensBatchReportCreation",
          meta: { headerName: "Nuevo reporte diario" }
        },
        {
          path: "update/:hensBatchReportId",
          component: () => import("@/views/production/hensBatchReports/HensBatchReportUpdate"),
          props: true,
          name: "HensBatchReportUpdate",
          meta: { headerName: "Modificar reporte diario" }
        },
      ]
    },
    {
      path: "/production/hens-batch-reports-charts/",
      component: () => import("@/views/production/hensBatchReports/HensBatchReportChart"),
      props: true,
      name: "HensBatchReportChart",
      meta: { headerName: "Gráficas" }
    },
    {
      path: "/production/hens-batch-reports-trazability/",
      component: () => import("@/views/production/hensBatchReports/HensBatchReportTraceability"),
      props: true,
      name: "HensBatchReportTraceability",
      meta: { headerName: "Trazabilidad" }
    },
  ]
});


router.beforeEach((to, from, next) => {
  if (to.name != "UserCreation" && to.name != "Login" && !store.getters.authenticated) {
    next({
      name: "Login",
      query: {
        destinationURL: to.path
      }
    })
  } else if (to.name === "Login" && store.getters.authenticated) {
    if (to.query?.destinationURL) next({ path: to.query?.destinationURL });
    else next({ name: "HomePage" });
  } else {
    next();
  }
});

export default router;