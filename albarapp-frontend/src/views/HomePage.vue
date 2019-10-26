<template>
  <v-container>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn to="/Login/">
          <span>Login</span>
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
  </v-container>
</template>

<script>
export default {
  name: "home"
};
</script>