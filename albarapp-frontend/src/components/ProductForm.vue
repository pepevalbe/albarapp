<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1">Datos de producto</v-subheader>
      <v-text-field
        v-model="form.product.code"
        type="number"
        :counter="5"
        autofocus
        :rules="codeRules"
        label="Código *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.product.name"
        :counter="40"
        :rules="nameRules"
        label="Nombre *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.product.factoryPrice"
        type="number"
        :rules="factoryPriceRules"
        label="Precio estándar (€) *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.product.tax"
        type="number"
        :rules="taxRules"
        label="Tipo impositivo (%) *"
        required
      ></v-text-field>
    </v-form>
  </v-container>
</template>

<script>
export default {
  name: "ProductForm",
  props: {
    form: {
      valid: Boolean,
      product: {
        code: Number,
        name: String,
        factoryPrice: Number,
        tax: Number
      }
    }
  },
  data: () => ({
    codeRules: [
      v => v != undefined || v != null || v != "" || "El código es obligatorio",
      v =>
        (v && v > 0 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos"
    ],
    nameRules: [
      v => !!v || "El nombre es obligatorio",
      v =>
        (v && v.length <= 40) || "El nombre debe tener menos de 40 caracteres"
    ],
    factoryPriceRules: [
      v => v != undefined || v != null || v != "" || "El precio es obligatorio",
      v => (v && v > 0) || "El precio debe ser mayor que 0"
    ],
    taxRules: [
      v =>
        v != undefined ||
        v != null ||
        v != "" ||
        "El tipo impositivo es obligatorio",
      v =>
        (v >= 0 && v <= 100) ||
        "El tipo impositivo debe ser menor que 100%, podemita"
    ]
  }),
  methods: {
    reset: function() {
      this.$refs.form.reset();
    }
  }
};
</script>