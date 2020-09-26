<template>
  <v-flex align-self-start>
    <div v-if="!errorLoading">
      <ProductForm :form="form"></ProductForm>
      <div class="mb-3"></div>
      <div class="mb-10"></div>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
          <v-btn
            :disabled="!form.valid"
            color="success"
            class="mr-4"
            @click="updateProduct()"
          >Actualizar</v-btn>
        </v-flex>
      </v-layout>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center">Error al cargar el producto, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="loadProduct()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
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
  name: "ProductUpdate",
  components: {
    ProductForm,
  },
  data: () => ({
    form: {
      valid: false,
      product: {
        code: "",
        name: "",
        factoryPrice: 0,
        tax: 0,
      },
    },
    errorLoading: false,
    snackbar: {
      show: false,
      message: "",
      color: "",
    },
    spinner: {
      loading: false,
      counter: 0,
    },
  }),
  props: {
    productId: String,
  },
  async created() {
    this.loadProduct();
  },
  methods: {
    async loadProduct() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.form.product = await ProductService.get(this.productId);
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async updateProduct() {
      try {
        this.showSpinner();
        await ProductService.update(this.productId, this.form.product);
        this.snackbar = {
          show: true,
          message: "Producto actualizado correctamente",
          color: "success",
        };
      } catch (e) {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido modificar el producto, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>