<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1">Datos de cliente</v-subheader>
      <v-divider></v-divider>

      <v-text-field
        v-model="form.code"
        type="number"
        :counter="5"
        :rules="codeRules"
        label="Código *"
        required
      ></v-text-field>

      <v-text-field v-model="form.alias" :counter="20" :rules="aliasRules" label="Alias *" required></v-text-field>

      <v-text-field v-model="form.name" :counter="40" :rules="nameRules" label="Nombre *" required></v-text-field>

      <v-text-field v-model="form.email" :rules="emailRules" label="E-mail"></v-text-field>

      <v-text-field v-model="form.idn" :rules="idnRules" label="NIF *" required></v-text-field>

      <v-text-field v-model="form.address" :counter="200" :rules="addressRules" label="Dirección"></v-text-field>

      <v-text-field v-model="form.province" :counter="20" :rules="provinceRules" label="Provincia"></v-text-field>

      <v-text-field
        v-model="form.telephone"
        type="number"
        :counter="15"
        :rules="telephoneRules"
        label="Teléfono"
      ></v-text-field>
    </v-form>
  </v-container>
</template>

<script>
export default {
  name: "CustomerForm",
  props: {
    form: {
      valid: Boolean,
      code: String,
      name: String,
      alias: String,
      email: String,
      idn: String,
      address: String,
      province: String,
      telephone: String
    }
  },
  data: () => ({
    codeRules: [
      v => !!v || "El código es obligatorio",
      v => (v && v.length <= 5) || "El nombre debe tener un máximo de 5 dígitos"
    ],
    nameRules: [
      v => !!v || "El nombre es obligatorio",
      v =>
        (v && v.length <= 40) || "El nombre debe tener menos de 40 caracteres"
    ],
    aliasRules: [
      v => !!v || "El alias es obligatorio",
      v => (v && v.length <= 20) || "El alias debe tener menos de 20 caracteres"
    ],
    emailRules: [v => !v || /.+@.+\..+/.test(v) || "E-mail no válido"],
    idnRules: [
      v => !!v || "El NIF es obligatorio",
      v =>
        /^(\d{8})([A-Z])$/.test(v) ||
        /^([ABCDEFGHJKLMNPQRSUVW])(\d{7})([0-9A-J])$/.test(v) ||
        /^[XYZ]\d{7,8}[A-Z]$/.test(v) ||
        "NIF no válido"
    ],
    addressRules: [
      v =>
        !v ||
        v.length <= 200 ||
        "La dirección debe tener menos de 200 caracteres"
    ],
    provinceRules: [
      v =>
        !v || v.length <= 20 || "La provincia debe tener menos de 20 caracteres"
    ],
    telephoneRules: [
      v => !v || v.length <= 15 || "El teléfono debe tener menos de 15 dígitos"
    ]
  })
};
</script>