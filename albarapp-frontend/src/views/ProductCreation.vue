<template>
  <v-flex align-self-start>
    <ProductForm v-bind:form="form" ref="form"></ProductForm>
    <div class="mb-3"></div>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
        <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>
        <v-btn :disabled="!form.valid" color="success" class="mr-4" @click="createProduct()">Crear</v-btn>
      </v-flex>
    </v-layout>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{snackbar.message}}
      <v-btn text @click="snackbar.show=false">Cerrar</v-btn>
    </v-snackbar>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-flex>
</template>

<script>
import ProductForm from "@/components/ProductForm";
import ProductService from "@/services/ProductService.js";

export default {
  name: "ProductCreation",
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
    snackbar: {
      show: false,
      message: "",
      color: ""
    },
    spinner: {
      loading: false,
      counter: 0
    }
  }),
  methods: {
    async createProduct() {
      try {
        this.showSpinner();
        await ProductService.create(this.form.product);
        this.snackbar = {
          show: true,
          message: "Producto creado correctamente",
          color: "success"
        };
        this.reset();
      } catch (e) {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido crear el producto, por favor vuelva a intentarlo.",
          color: "error"
        };
      } finally {
        this.closeSpinner();
      }
    },
    reset() {
      this.$refs.form.reset();
    }
  }
};
</script>