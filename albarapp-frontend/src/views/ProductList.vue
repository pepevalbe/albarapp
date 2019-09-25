<template>
  <v-content>
    <v-container>
      <v-layout text-right wrap class="pb-10 pt-5 mr-5">
        <v-flex xs12>
          <v-btn to="/product-creation/">
            <span>Nuevo</span>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-data-table
        :loading="!products || products.length == 0"
        loading-text="Cargando... Por favor, espere"
        :headers="headers"
        :items="products"
        :items-per-page="15"
      ></v-data-table>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn to="/">
            <span>Volver</span>
          </v-btn>
        </v-flex>
      </v-layout>
    </v-container>
  </v-content>
</template>

<script>
export default {
  name: "ProductList",
  data: () => {
    return {
      products: [],
      headers: [
        { text: "Código", sortable: true, value: "code" },
        { text: "Nombre", sortable: false, value: "name" },
        { text: "Precio estándar", sortable: true, value: "factoryPrice" },
        { text: "Tipo impositivo (%)", sortable: false, value: "tax" }
      ]
    };
  },
  created() {
    this.listProducts();
  },
  methods: {
    listProducts() {
      this.$axios
        .get("/products")
        .then(response => {
          this.products = response.data._embedded.products;
        })
        .catch(function(error) {
          alert("Ha ocurrido un error recuperando los productos");
        });
    }
  }
};
</script>

<style scoped>
</style>