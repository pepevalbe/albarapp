<template>
  <v-container>
    <v-expansion-panels :dark="!!products.productCodes.length">
      <v-expansion-panel>
        <v-expansion-panel-header>
          <span class="subtitle-1 font-italic font-weight-light">Filtrar por productos</span>
        </v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-list>
            <v-list-item-group v-model="products.productCodes" :multiple="true">
              <v-list-item
                :value="product.code"
                v-for="product in totalProducts"
                :key="product.code"
              >
                <template v-slot:default="{ active }">
                  <v-list-item-content>
                    <v-list-item-title v-text="product.name"></v-list-item-title>
                  </v-list-item-content>
                  <v-list-item-action>
                    <v-checkbox :input-value="active" :true-value="product.code" color="black"></v-checkbox>
                  </v-list-item-action>
                </template>
              </v-list-item>
            </v-list-item-group>
          </v-list>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-container>
</template>

<script>
import ProductService from "@/services/ProductService.js";

export default {
  name: "ProductFilter",
  props: {
    products: {
      productCodes: Array
    }
  },
  data: () => ({
    totalProducts: []
  }),
  created() {
    this.listProducts();
  },
  methods: {
    async listProducts() {
      this.totalProducts = await ProductService.getAll();
      this.totalProducts.sort(function(a, b) {
        return a.code > b.code ? 1 : -1;
      });
    }
  }
};
</script>