<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <v-form ref="form" v-model="valid">
        <v-subheader class="title ml-1">Inhformación de usuario</v-subheader>
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
          :append-icon="showPassword ? 'mdi-eye-outline' : 'mdi-eye'"
          :rules="passwordRules"
          :type="showPassword ? 'text' : 'password'"
          label="Contraseña"
          hint="Como mínimo 5 caracteres"
          @click:append="showPassword = !showPassword"
        ></v-text-field>
        <v-text-field
          v-model="rePassword"
          :append-icon="showRepassword ? 'mdi-eye-outline' : 'mdi-eye'"
          :rules="[rePasswordMatchRule]"
          :type="showRepassword ? 'text' : 'password'"
          label="Repita la contraseña"
          counter
          @click:append="showRepassword = !showRepassword"
        ></v-text-field>
      </v-form>
      <div class="mb-3"></div>
      <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>
      <v-btn :disabled="!valid && token !== null" color="success" @click="createUser()">Crear</v-btn>
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
    rePassword: "",
    showPassword: false,
    showRepassword: false,
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
      v => !!v || "La contraseña es obligatoria",
      v =>
        (v && v.length >= 5) ||
        "La contraseña debe tener al menos 5 caracteres",
      v =>
        (v && v.length <= 15) ||
        "La contraseña debe tener menos de 15 caracteres"
    ]
  }),
  computed: {
    invitation: function() {
      return this.$route.query.token;
    },
    rePasswordMatchRule() {
      return () =>
        this.password === this.rePassword || "Las contraseñas no coinciden";
    }
  },
  methods: {
    async createUser() {
      await AdminService.createUser(
        this.invitation,
        this.name,
        this.surname,
        this.password
      );
      this.$router.push({
        name: "LoginPage"
      });
    },
    reset() {
      this.$refs.form.reset();
    }
  }
};
</script>