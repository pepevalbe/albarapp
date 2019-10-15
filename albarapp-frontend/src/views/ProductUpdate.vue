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
  props: {
    productHref: String
  },
  async created() {
    this.form.product = await ProductService.get(this.productHref);
  },
  methods: {
    async updateProduct() {
      if (this.form.valid) {
        ProductService.update(this.productHref, this.form.product).then(() => {
          this.snackbar = true;
        });
      }
    }
  }
};
</script>