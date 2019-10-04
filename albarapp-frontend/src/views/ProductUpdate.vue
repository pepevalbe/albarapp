<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <ProductForm v-bind:form="form"></ProductForm>
      <div class="mb-3"></div>
      <div class="mb-10"></div>
      <v-btn
        :disabled="!form.valid"
        color="success"
        class="mr-4"
        @click="updateProduct()"
      >Actualizar</v-btn>
      <v-btn to="/product-list/">Volver</v-btn>
      <v-snackbar v-model="snackbar">
        Producto actualizado correctamente
        <v-btn color="green" text @click="snackbar = false">Cerrar</v-btn>
      </v-snackbar>
    </v-container>
  </v-content>
</template>

<script>
import ProductForm from "@/components/ProductForm";

export default {
  components: {
    ProductForm
  },
  data: () => ({
    form: {
      valid: false,
      code: "",
      name: "",
      factoryPrice: 0,
      tax: 0
    },
    snackbar: false
  }),
  props: {
    productHref: String
  },
  created() {
    this.$axios
      .get(this.productHref)
      .then(response => {
        this.form.code = response.data.code.toString();
        this.form.name = response.data.name;
        this.form.factoryPrice = response.data.factoryPrice;
        this.form.tax = response.data.tax.toString();
      })
      .catch(function(error) {
        alert("Ha ocurrido un error recuperando los datos del producto");
      });
  },
  methods: {
    updateProduct() {
      var vm = this;
      if (this.form.valid) {
        var product = {
          code: this.form.code,
          name: this.form.name,
          factoryPrice: this.form.factoryPrice,
          tax: this.form.tax
        };
        this.$axios
          .put(this.productHref, product)
          .then(response => {
            this.snackbar = true;
          })
          .catch(function(error) {
            alert("Ha ocurrido un error actualizando el producto");
          });
      }
    }
  }
};
</script>