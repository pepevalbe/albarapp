<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-layout text-right wrap class="pt-2 pb-5 mr-5">
        <v-flex xs12>
          <v-btn to="/product-creation/">
            Nuevo
            <v-icon class="ml-2">mdi-plus-circle</v-icon>
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
            autocomplete="off"
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
            <tbody v-if="!$vuetify.breakpoint.xsOnly">
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
            <tbody v-else>
              <tr>
                <v-card class="flex-content" outlined v-for="item in items" :key="item.code">
                  <v-card-text>
                    <span class="black--text">Código:</span>
                    {{item.code}}
                    <br />
                    <span class="black--text">Nombre:</span>
                    {{item.name}}
                    <br />
                    <span class="black--text">Precio estándar:</span>
                    {{item.factoryPrice}} €
                    <br />
                    <span class="black--text">Tipo impositivo:</span>
                    {{item.tax}} %
                    <br />
                  </v-card-text>
                  <v-card-actions>
                    <v-layout text-center wrap>
                      <v-flex xs12>
                        <v-btn @click="updateProduct(item)">
                          <v-icon dark>mdi-pencil</v-icon>
                        </v-btn>
                      </v-flex>
                    </v-layout>
                  </v-card-actions>
                </v-card>
              </tr>
            </tbody>
          </template>
        </v-data-table>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row
        class="mb-2"
        justify="center"
      >Error al obtener los productos, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="loadProducts()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-container>
</template>

<script>
import ProductService from "@/services/ProductService.js";

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
        { text: "", sortable: false, value: "update" },
      ],
      search: "",
      sortBy: "code",
      descending: false,
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0,
      },
    };
  },
  async created() {
    this.loadProducts();
  },
  methods: {
    async loadProducts() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.products = await ProductService.getAll();
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    updateProduct(item) {
      this.$router.push({
        name: "ProductUpdate",
        params: { productId: item.id },
      });
    },
  },
};
</script>

<style scoped>
</style>