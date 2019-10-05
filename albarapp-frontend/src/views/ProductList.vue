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
      <v-card>
        <v-card-title>
          Listado de productos
          <div class="flex-grow-1"></div>
          <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            label="Buscar ..."
            single-line
            hide-details
          ></v-text-field>
        </v-card-title>
        <v-data-table
          :loading="!products || products.length == 0"
          loading-text="Cargando... Por favor, espere"
          :headers="headers"
          :items="products"
          :search="search"
          :sort-by.sync="sortBy"
          :sort-desc.sync="descending"
          :items-per-page="15"
        >
          <template v-slot:body="{ items }">
            <tbody>
              <tr v-for="item in items" :key="item.code">
                <td>{{item.code}}</td>
                <td>{{item.name}}</td>
                <td>{{item.factoryPrice}} €</td>
                <td>{{item.tax}} %</td>
                <td>
                  <v-btn @click="updateProduct(item)">
                    <v-icon dark>mdi-pencil</v-icon>
                  </v-btn>
                </td>
              </tr>
            </tbody>
          </template>
        </v-data-table>
      </v-card>
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
        { text: "Tipo impositivo (%)", sortable: false, value: "tax" },
        { text: "", sortable: false, value: "update" }
      ],
      search: "",
      sortBy: "code",
      descending: false
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
    },
    updateProduct(item) {
      this.$router.push({
        name: "Actualizar producto",
        params: { productHref: item._links.self.href }
      });
    }
  }
};
</script>

<style scoped>
</style>