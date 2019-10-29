<template>
  <v-container>
    <v-card height="400" width="256" class="mx-auto" v-if="token">
      <v-navigation-drawer permanent>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title class="title">Albarapp</v-list-item-title>
            <v-list-item-subtitle>{{parsedToken.sub}}</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
        <v-divider></v-divider>
        <v-list dense nav>
          <v-list-item link to="/admin/" v-if="parsedToken.roles.includes('ADMIN')">
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
            <v-btn block @click="logout()">Cerrar sesión</v-btn>
          </div>
        </template>
      </v-navigation-drawer>
    </v-card>
    <LoginForm v-if="!token"></LoginForm>
  </v-container>
</template>

<script>
import LoginForm from "@/components/LoginForm";

export default {
  name: "home",
  components: {
    LoginForm
  }
};
</script>