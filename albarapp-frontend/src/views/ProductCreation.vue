<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <v-form ref="form" v-model="valid">
        <v-subheader class="title ml-1">Datos de producto</v-subheader>
        <v-divider></v-divider>
        <v-text-field
          v-model="code"
          type="number"
          :counter="5"
          :rules="codeRules"
          label="Código *"
          required
        ></v-text-field>
        <v-text-field v-model="name" :counter="40" :rules="nameRules" label="Nombre *" required></v-text-field>
        <v-text-field
          v-model="factoryPrice"
          type="number"
          :rules="factoryPriceRules"
          label="Precio estándar (€) *"
          required
        ></v-text-field>
        <v-text-field
          v-model="tax"
          type="number"
          :rules="taxRules"
          label="Tipo impositivo (%) *"
          required
        ></v-text-field>
        <div class="mb-3"></div>
        <div class="mb-10"></div>
        <v-btn :disabled="!valid" color="success" class="mr-4" @click="validate">Crear</v-btn>
        <v-btn color="error" class="mr-4" @click="reset">Cancelar</v-btn>
        <v-btn to="/product-list/">Volver</v-btn>
      </v-form>
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
    valid: false,
    code: "",
    codeRules: [
      v => !!v || "El código es obligatorio",
      v => (v && v.length <= 5) || "El código debe tener un máximo de 5 dígitos"
    ],
    name: "",
    nameRules: [
      v => !!v || "El nombre es obligatorio",
      v =>
        (v && v.length <= 40) || "El nombre debe tener menos de 40 caracteres"
    ],
    factoryPrice: null,
    factoryPriceRules: [
      v => !!v || "El precio es obligatorio",
      v => (v && v > 0) || "El precio debe ser mayor que 0"
    ],
    tax: null,
    taxRules: [
      v => !!v || "El tipo impositivo es obligatorio",
      v =>
        (v && v >= 0 && v <= 100) ||
        "El tipo impositivo debe ser menor que 100%, podemita"
    ],
    products: [],
    product: undefined,
    snackbar: false,
    snackbarMessage: ""
  }),
  methods: {
    validate() {
      if (this.$refs.form.validate()) {
        // Rest call to create new customer
        var product = {
          code: this.code,
          name: this.name,
          factoryPrice: this.factoryPrice,
          tax: this.tax
        };
        this.$axios
          .post("/products", product)
          .then(response => {
            this.snackbar = true;
            this.snackbarMessage = "Producto creado correctamente";
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