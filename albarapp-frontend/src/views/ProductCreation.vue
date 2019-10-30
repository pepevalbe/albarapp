<template>
  <v-flex align-self-start>
    <ProductForm v-bind:form="form" ref="form"></ProductForm>
    <div class="mb-3"></div>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn :disabled="!form.valid" color="success" class="mr-4" @click="createProduct()">Crear</v-btn>
        <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>
        <v-btn to="/product-list/">Volver</v-btn>
      </v-flex>
    </v-layout>
    <v-snackbar v-model="snackbar">
      Producto creado correctamente
      <v-btn color="green" text @click="snackbar=false">Cerrar</v-btn>
    </v-snackbar>
  </v-flex>
</template>

<script>
import ProductForm from "@/components/ProductForm";
import ProductService from "@/services/ProductService.js";

export default {
  components: {
    ProductForm
  },
  data: () => ({
    form: {
      valid: false,
      product: {
        code: "",
        name: "",
        factoryPrice: 0,
        tax: 0
      }
    },
    snackbar: false
  }),
  methods: {
    async createProduct() {
      await ProductService.create(this.form.product);
      this.snackbar = true;
      this.reset();
    },
    reset() {
      this.$refs.form.reset();
    }
  }
};
</script>