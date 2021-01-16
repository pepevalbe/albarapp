<template>
  <v-navigation-drawer
    fixed
    :clipped="$vuetify.breakpoint.mdAndUp"
    app
    v-model="drawer.value"
  >
    <div v-if="!errorLoading">
      <v-list-item>
        <v-list-item-avatar>
          <v-icon large>mdi-account-circle</v-icon>
        </v-list-item-avatar>
        <v-list-item-content>
          <v-list-item-title class="title">{{
            profile.name
          }}</v-list-item-title>
          <v-list-item-subtitle>{{
            $store.getters.parsedToken.sub
          }}</v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>
    </div>
    <div v-if="errorLoading" class="mb-2 mt-2">
      <v-row class="mb-2" justify="center"
        >Error al obtener los datos de perfil</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadProfile()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-divider></v-divider>
    <v-list dense nav>
      <v-list-group prepend-icon="mdi-receipt" group="^.*billing.*$">
        <template v-slot:activator>
          <v-list-item-title>FACTURACIÓN</v-list-item-title>
        </template>
        <v-list-item class="pl-8" link :to="{ name: 'HomePage' }">
          <v-list-item-icon>
            <v-icon>mdi-home</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Home</v-list-item-title>
        </v-list-item>
        <v-list-item class="pl-8" link :to="{ name: 'ProductList' }">
          <v-list-item-icon>
            <v-icon>mdi-baguette</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Productos</v-list-item-title>
        </v-list-item>
        <v-list-item class="pl-8" link :to="{ name: 'CustomerList' }">
          <v-list-item-icon>
            <v-icon>mdi-account-tie</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Clientes</v-list-item-title>
        </v-list-item>
        <v-list-item class="pl-8" link :to="{ name: 'DeliveryNoteList' }">
          <v-list-item-icon>
            <v-icon>mdi-basket</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Albaranes</v-list-item-title>
        </v-list-item>
        <v-list-item class="pl-8" link :to="{ name: 'InvoiceList' }">
          <v-list-item-icon>
            <v-icon>mdi-cash</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Facturas</v-list-item-title>
        </v-list-item>
      </v-list-group>
      <v-list-group
        prepend-icon="mdi-chart-bar-stacked"
        group="^.*production.*$"
      >
        <template v-slot:activator>
          <v-list-item-content>
            <v-list-item-title>LOTES Y PUESTA</v-list-item-title>
          </v-list-item-content>
        </template>
        <v-list-item class="pl-8" link :to="{ name: 'HensBatchList' }">
          <v-list-item-icon>
            <v-icon>mdi-barn</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Lotes</v-list-item-title>
        </v-list-item>
        <v-list-item class="pl-8" link :to="{ name: 'HensBatchReportList' }">
          <v-list-item-icon>
            <v-icon>mdi-clipboard-list-outline</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Reporte diario</v-list-item-title>
        </v-list-item>
        <v-list-item class="pl-8" link :to="{ name: 'HensBatchReportChart' }">
          <v-list-item-icon>
            <v-icon>mdi-chart-bell-curve-cumulative</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Gráficas</v-list-item-title>
        </v-list-item>
        <v-list-item class="pl-8" link :to="{ name: 'HensBatchReportTraceability' }">
          <v-list-item-icon>
            <v-icon>mdi-ray-start-vertex-end</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Trazabilidad</v-list-item-title>
        </v-list-item>
      </v-list-group>
      <v-list-group prepend-icon="mdi-form-dropdown" group="^.*admin.*$">
        <template v-slot:activator>
          <v-list-item-title>ADMINISTRAR</v-list-item-title>
        </template>
        <v-list-item
          class="pl-8"
          link
          :to="{ name: 'AdminPage' }"
          v-if="$store.getters.isAdmin"
        >
          <v-list-item-icon>
            <v-icon>mdi-account-group</v-icon>
          </v-list-item-icon>
          <v-list-item-title>Usuarios</v-list-item-title>
        </v-list-item>
      </v-list-group>
    </v-list>
    <template v-slot:append>
      <div class="pa-2">
        <v-btn block @click="logout()">Cerrar sesión</v-btn>
      </div>
    </template>
  </v-navigation-drawer>
</template>

<script>
import UserService from "@/services/UserService.js";

export default {
  name: "WebNavDrawer",
  props: {
    drawer: Object,
  },
  data: () => {
    return {
      profile: Object,
      errorLoading: false,
    };
  },
  async created() {
    this.loadProfile();
  },
  methods: {
    async loadProfile() {
      if (this.$store.getters.authenticated) {
        try {
          this.profile = await UserService.getProfile();
          this.errorLoading = false;
        } catch (e) {
          this.errorLoading = true;
        }
      }
    },
    logout() {
      this.$store.commit("logout");
      this.$router.push({
        path: "/login",
        query: { destinationURL: this.$router.currentRoute.path },
      });
    },
  },
};
</script>