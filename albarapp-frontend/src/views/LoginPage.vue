<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <v-form ref="form" v-model="valid">
        <v-subheader class="title ml-1">Introduzca sus credenciales</v-subheader>
        <v-text-field
          v-model="email"
          type="email"
          :rules="emailRules"
          label="Email"
          required
          autofocus
        ></v-text-field>
        <v-text-field
          v-model="password"
          type="password"
          :rules="passwordRules"
          label="Contraseña"
          required
          @keyup.enter="login()"
        ></v-text-field>
      </v-form>
      <v-layout text-right>
        <v-flex xs12>
          <v-btn :disabled="!valid" color="success" class="mr-4" @click="login()">Acceder</v-btn>
        </v-flex>
      </v-layout>
    </v-container>
  </v-content>
</template>

<script>
import LoginService from "@/services/LoginService.js";

export default {
  name: "LoginPage",
  data() {
    return {
      email: "",
      password: "",
      valid: false,
      passwordRules: [v => !!v || "La contraseña es necesaria"],
      emailRules: [
        v => !!v || "El email es necesario",
        v => /.+@.+\..+/.test(v) || "Email inválido"
      ]
    };
  },
  methods: {
    async login() {
      var result = await LoginService.login(this.email, this.password);
      if (result.isError) {
        this.alertError(result);
        this.password = "";
      } else {
        localStorage.setItem("token", result);
        this.$router.push({ name: "HomePage" });
      }
    }
  }
};
</script>