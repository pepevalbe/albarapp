<template>
  <v-flex align-self-start>
    <ProductForm v-bind:form="form"></ProductForm>
    <div class="mb-3"></div>
    <div class="mb-10"></div>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn
          :disabled="!form.valid"
          color="success"
          class="mr-4"
          @click="updateProduct()"
        >Actualizar</v-btn>
        <v-btn to="/product-list/">Volver</v-btn>
      </v-flex>
    </v-layout>
    <v-snackbar v-model="snackbar">
      Producto actualizado correctamente
      <v-btn color="green" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
  </v-flex>
</template>

<script>
import ProductForm from "@/components/ProductForm";
import ProductService from "@/services/ProductService.js";

export default {
  name: "ProductUpdate",
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
  props: {
    productId: String
  },
  async created() {
    this.form.product = await ProductService.get(this.productId);
  },
  methods: {
    async updateProduct() {
      await ProductService.update(this.productId, this.form.product);
      this.snackbar = true;
    }
  }
};
</script>