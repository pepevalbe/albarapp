<template>
  <v-container>
    <v-subheader class="title ml-1">Precios de productos</v-subheader>
    <v-divider></v-divider>
    <v-row class="mt-2" justify="center">
      <v-alert
        v-if="sameProduct()"
        type="error"
      >No puedes introducir dos precios para el mismo producto.</v-alert>
    </v-row>
    <v-row class="ml-5" justify="center" v-if="!readonly">
      <v-col cols="12" md="3">
        <v-select
          v-model="product"
          :items="products"
          item-text="name"
          item-value="id"
          label="Producto"
          return-object
        ></v-select>
      </v-col>
      <v-col cols="12" md="1">
        <v-text-field v-model="price" type="number" label="Precio" suffix="€"></v-text-field>
      </v-col>
      <v-col cols="12" md="1" align-self="center">
        <v-btn :disabled="!price || !product || sameProduct()" @click="addPrice()">Añadir</v-btn>
      </v-col>
    </v-row>
    <v-row class="ml-5" justify="center">
      <v-col cols="12" md="5">
        <v-simple-table>
          <template v-slot:default>
            <thead>
              <tr>
                <th class="text-left" v-if="!readonly">Orden</th>
                <th class="text-left">Producto</th>
                <th class="text-left">Precio</th>
                <th class="text-left" v-if="!readonly"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in customerProductPrices" :key="item.product.id">
                <td v-if="!readonly">
                  <v-btn class="ma-2" :disabled="index == 0" justify="center" @click="moveUp(item)">
                    <v-icon dark>mdi-chevron-up</v-icon>
                  </v-btn>
                  <v-btn class="ma-2" :disabled="index==customerProductPrices.length-1" justify="center" @click="moveDown(item)">
                    <v-icon dark>mdi-chevron-down</v-icon>
                  </v-btn>
                </td>
                <td>{{ item.product.name }}</td>
                <td>{{ item.offeredPrice }} €</td>
                <td justify="center" v-if="!readonly">
                  <div class="text-xs-center">
                    <v-btn
                      class="ma-2"
                      justify="center"
                      color="red"
                      dark
                      @click="removePrice(item)"
                    >
                      <v-icon dark>mdi-delete</v-icon>
                    </v-btn>
                  </div>
                </td>
              </tr>
            </tbody>
          </template>
        </v-simple-table>
      </v-col>
    </v-row>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-container>
</template>

<script>
import ProductService from "@/services/ProductService.js";

export default {
  name: "CustomerPriceTable",
  data: () => ({
    products: [],
    product: undefined,
    price: 0,
    spinner: {
      loading: false,
      counter: 0
    }
  }),
  props: {
    customerProductPrices: Array,
    readonly: Boolean
  },
  async created() {
    this.showSpinner();
    this.products = await ProductService.getAll();
    this.closeSpinner();
  },
  methods: {
    addPrice() {
      var vm = this;
      if (!this.sameProduct()) {
        this.customerProductPrices.push({
          product: vm.product,
          offeredPrice: vm.price
        });
        vm.product = undefined;
        vm.price = 0;
      }
    },
    sameProduct() {
      var vm = this;
      return (
        vm.product &&
        vm.customerProductPrices.some(
          product => product.product.code === vm.product.code
        )
      );
    },
    removePrice(price) {
      this.customerProductPrices.splice(
        this.customerProductPrices.findIndex(item => item === price),
        1
      );
    },
    moveUp(price){
      var index = this.customerProductPrices.indexOf(price);
      this.customerProductPrices.splice(index, 1);
      this.customerProductPrices.splice(index-1, 0, price);
    },
    moveDown(price){
      var index = this.customerProductPrices.indexOf(price);
      this.customerProductPrices.splice(index, 1);
      this.customerProductPrices.splice(index+1, 0, price);
    }
  }
};
</script>