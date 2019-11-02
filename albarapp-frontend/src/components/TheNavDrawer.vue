<template>
  <v-navigation-drawer
    v-if="token"
    fixed
    :clipped="$vuetify.breakpoint.mdAndUp"
    app
    v-model="drawer.value"
  >
    <v-list-item>
      <v-list-item-avatar>
        <v-icon large>mdi-account-circle</v-icon>
      </v-list-item-avatar>
      <v-list-item-content>
        <v-list-item-title class="title">{{profile.name}}</v-list-item-title>
        <v-list-item-subtitle>{{parsedToken.sub}}</v-list-item-subtitle>
      </v-list-item-content>
    </v-list-item>
    <v-divider></v-divider>
    <v-list dense nav>
      <v-list-item link to="/">
        <v-list-item-icon>
          <v-icon>mdi-home</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>Home</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item link to="/admin/" v-if="token && parsedToken.roles.includes('ADMIN')">
        <v-list-item-icon>
          <v-icon>mdi-account-group</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>Administrar</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item link to="/product-list/">
        <v-list-item-icon>
          <v-icon>mdi-baguette</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>Productos</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item link to="/customer-list/">
        <v-list-item-icon>
          <v-icon>mdi-account-tie</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>Clientes</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item link to="/delivery-note-list/">
        <v-list-item-icon>
          <v-icon>mdi-basket</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>Albaranes</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
      <v-list-item link to="/invoice-list/">
        <v-list-item-icon>
          <v-icon>mdi-cash</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>Facturas</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </v-list>
    <template v-slot:append>
      <div class="pa-2">
        <v-btn v-if="token" block @click="logout()">Cerrar sesión</v-btn>
      </div>
    </template>
  </v-navigation-drawer>
</template>

<script>
import UserService from "@/services/UserService.js";

export default {
  name: "WebNavDrawer",
  props: {
    drawer: Object
  },
  data: () => {
    return {
      profile: Object
    };
  },
  async created() {
    if (this.token) {
      this.profile = await UserService.getProfile();
    }
  }
};
</script>