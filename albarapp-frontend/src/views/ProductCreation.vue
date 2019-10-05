<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <ProductForm v-bind:form="form" ref="form"></ProductForm>
      <div class="mb-3"></div>
      <v-btn :disabled="!form.valid" color="success" class="mr-4" @click="createProduct()">Crear</v-btn>
      <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>
      <v-btn to="/product-list/">Volver</v-btn>
      <v-snackbar v-model="snackbar">
        Producto creado correctamente
        <v-btn color="green" text @click="snackbar=false">Cerrar</v-btn>
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
    createProduct() {
      if (this.form.valid) {
        this.$axios
          .post("/products", this.form.product)
          .then(response => {
            this.snackbar = true;
            this.reset();
          })
          .catch(function(error) {
            alert("Ha ocurrido un error creando el producto");
          });
      }
    },
    reset() {
      this.$refs.form.reset();
    }
  }
};
</script>