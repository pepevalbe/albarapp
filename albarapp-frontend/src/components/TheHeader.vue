<template>
  <div>
    <v-app-bar color extended>
      <v-app-bar-nav-icon></v-app-bar-nav-icon>

      <v-toolbar-title class="headline text-uppercase">
        <span>AlbarApp - </span>
        <span class="font-weight-light">{{currentState}}</span>
      </v-toolbar-title>

      <v-spacer></v-spacer>

      <!--<v-menu left bottom>
        <template v-slot:activator="{ on }">
          <v-btn icon v-on="on">
            <v-icon>mdi-dots-vertical</v-icon>
          </v-btn>
        </template>

        <v-list>
          <v-list-item v-if="!token" to="/Login/" @click="() => {}">
            <v-list-item-title>Login</v-list-item-title>
          </v-list-item>
          <v-list-item v-if="token" @click="logout()">
            <v-list-item-title>Desconectar</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>-->

      <template v-slot:extension>
        <v-tabs centered center-active fixed-tabs background-color="transparent">
          <v-tab v-if="token && parsedToken.roles.includes('ADMIN')" to="/">Home</v-tab>
          <v-tab v-if="token" to="/admin/">Administración de usuarios</v-tab>
          <v-tab v-if="token" to="/product-list/">Productos</v-tab>
          <v-tab v-if="token" to="/customer-list/">Clientes</v-tab>
          <v-tab v-if="token" to="/delivery-note-list/">Albaranes</v-tab>
          <v-tab v-if="token" to="/invoice-list/">Facturas</v-tab>
        </v-tabs>
      </template>
    </v-app-bar>
  </div>
</template>

<script>
export default {
  name: "WebHeader",
  props: {
    currentState: String
  }
};
</script>