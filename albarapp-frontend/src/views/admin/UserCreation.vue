<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <v-form v-model="valid">
        <v-subheader class="title ml-1">Datos de producto</v-subheader>
        <v-text-field
          v-model="name"
          :counter="15"
          :rules="nameRules"
          label="Nombre"
          required
          autofocus
        ></v-text-field>
        <v-text-field
          v-model="surname"
          :counter="30"
          :rules="surnameRules"
          label="Apellidos"
          required
        ></v-text-field>
        <v-text-field
          v-model="password"
          :counter="30"
          :rules="passwordRules"
          label="Contraseña"
          required
        ></v-text-field>
      </v-form>
      <div class="mb-3"></div>
      <v-btn
        :disabled="!valid && token !== null"
        color="success"
        class="mr-4"
        @click="createUser()"
      >Crear</v-btn>
      <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>
      <v-btn to="/User-list/">Volver</v-btn>
    </v-container>
  </v-content>
</template>

<script>
import AdminService from "@/services/AdminService.js";

export default {
  data: () => ({
    valid: false,
    name: "",
    surname: "",
    password: "",
    nameRules: [
      v => !!v || "El nombre es obligatorio",
      v =>
        (v && v.length <= 15) || "El nombre debe tener menos de 15 caracteres"
    ],
    surnameRules: [
      v => !!v || "Los apellidos son obligatorio",
      v =>
        (v && v.length <= 30) ||
        "Los apellidos deben tener menos de 30 caracteres"
    ],
    passwordRules: [
      v => !!v || "La contraseña es obligatorio",
      v =>
        (v && v.length <= 15) || "La contraseña de tener menos de 15 caracteres"
    ]
  }),
  computed: {
    token: function() {
      return this.$route.query.token;
    }
  },
  methods: {
    async createUser() {
      await AdminService.createUser(
        this.token,
        this.name,
        this.surname,
        this.password
      );
      this.$router.push({
        name: "LoginPage"
      });
    },
    reset() {
      this.name = "";
      this.surname = "";
      this.password = "";
    }
  }
};
</script>