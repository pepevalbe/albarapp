<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <ProductForm v-bind:form="form"></ProductForm>
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
      code: "",
      name: "",
      factoryPrice: 0,
      tax: 0
    },
    snackbar: false
  }),
  methods: {
    createProduct() {
      if (this.form.valid) {
        var product = {
          code: this.form.code,
          name: this.form.name,
          factoryPrice: this.form.factoryPrice,
          tax: this.form.tax
        };
        this.$axios
          .post("/products", product)
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
      this.form = {
        valid: false,
        code: "",
        name: "",
        factoryPrice: 0,
        tax: 0
      };
    }
  }
};
</script>