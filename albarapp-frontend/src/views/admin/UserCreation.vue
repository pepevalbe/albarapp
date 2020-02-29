<template>
  <v-flex align-self-start>
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
    <v-dialog v-model="dialogCreation.show" max-width="600">
      <v-card>
        <v-card-title class="headline">Usuario creado</v-card-title>
        <v-card-text>Se ha creado el usuario correctamente. Por favor, identifícate con tu usuario y contraseña</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" text @click="goToLogin()">Entendido</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
import UserService from "@/services/UserService.js";

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
    ],
    dialogCreation: {
      show: false
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
      try {
        this.showSpinner();
        await UserService.createUser(
          this.invitation,
          this.name,
          this.surname,
          this.password
        );
      } catch {
        this.snackbar = {
          show: true,
          message: "Ha ocurrido un error al crear el usuario",
          color: "error"
        };
      } finally {
        this.closeSpinner();
      }
    },
    reset() {
      this.$refs.form.reset();
    },
    goToLogin() {
      this.$router.push({
        name: "LoginPage"
      });
    }
  }
};
</script>