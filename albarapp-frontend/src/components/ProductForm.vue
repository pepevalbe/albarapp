<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1">Datos de producto</v-subheader>
      <v-text-field
        v-model="form.code"
        type="number"
        :counter="5"
        :rules="codeRules"
        label="Código *"
        required
      ></v-text-field>
      <v-text-field v-model="form.name" :counter="40" :rules="nameRules" label="Nombre *" required></v-text-field>
      <v-text-field
        v-model="form.factoryPrice"
        type="number"
        :rules="factoryPriceRules"
        label="Precio estándar (€) *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.tax"
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
      code: String,
      name: String,
      factoryPrice: Number,
      tax: Number
    }
  },
  data: () => ({
    codeRules: [
      v => !!v || "El código es obligatorio",
      v => (v && v.length <= 5) || "El código debe tener un máximo de 5 dígitos"
    ],
    nameRules: [
      v => !!v || "El nombre es obligatorio",
      v =>
        (v && v.length <= 40) || "El nombre debe tener menos de 40 caracteres"
    ],
    factoryPriceRules: [
      v => !!v || "El precio es obligatorio",
      v => (v && v > 0) || "El precio debe ser mayor que 0"
    ],
    taxRules: [
      v => !!v || "El tipo impositivo es obligatorio",
      v =>
        (v && v >= 0 && v <= 100) ||
        "El tipo impositivo debe ser menor que 100%, podemita"
    ]
  })
};
</script>