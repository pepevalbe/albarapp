<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <div class="mb-3"></div>

      <div class="mb-10"></div>

      <v-btn :disabled="!form.valid" color="success" class="mr-4" @click="validate">Actualizar</v-btn>

      <v-btn to="/product-list/">Volver</v-btn>

      <v-snackbar v-model="snackbar">
        {{snackbarMessage}}
        <v-btn color="green" text @click="snackbar = false">Cerrar</v-btn>
      </v-snackbar>
    </v-container>
  </v-content>
</template>

<script>
export default {
  data: () => ({
    form: {
      valid: false,
      code: "",
      name: "",
      alias: "",
      email: "",
      idn: "",
      address: "",
      province: "",
      telephone: ""
    },
    componentKey: 0,
    products: [],
    product: undefined,
    price: 0,
    productPrices: [],
    productPricesOriginal: [],
    snackbar: false,
    snackbarMessage: ""
  }),
  props: {
    productHref: String
  },
  created() {
    this.$axios
      .get(this.productHref)
      .then(response => {})
      .catch(function(error) {
        alert("Ha ocurrido un error recuperando los datos del producto");
      });
  },
  methods: {
    validate() {}
  }
};
</script>