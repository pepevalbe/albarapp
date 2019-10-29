<template>
  <v-container>
    <v-layout v-if="!token" text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn to="/Login/">
          <span>Login</span>
        </v-btn>
      </v-flex>
    </v-layout>
    <div v-if="token">
      <v-layout v-if="parsedToken.roles.includes('ADMIN')" text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn to="/admin/">
            <span>Administración de usuarios</span>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn to="/product-list/">
            <span>Productos</span>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn to="/customer-list/">
            <span>Clientes</span>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn to="/delivery-note-list/">
            <span>Albaranes</span>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn to="/invoice-list/">
            <span>Facturas</span>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn class="mr-4" @click="logout()">Salir</v-btn>
        </v-flex>
      </v-layout>
    </div>
  </v-container>
</template>

<script>
export default {
  name: "home"
};
</script>