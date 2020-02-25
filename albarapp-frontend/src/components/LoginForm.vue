<template>
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
</template>

<script>
import LoginService from "@/services/LoginService.js";

export default {
  name: "LoginForm",
  data() {
    return {
      email: "",
      password: "",
      valid: false,
      passwordRules: [v => !!v],
      emailRules: [v => !!v, v => /.+@.+\..+/.test(v) || "Email inválido"]
    };
  },
  created() {
    if (this.token) {
      if (this.$route.query && this.$route.query.destinationURL) {
        this.$router.push(this.$route.query.destinationURL);
      } else {
        this.$router.push("/");
      }
    }
  },
  methods: {
    async login() {
      var result = await LoginService.login(this.email, this.password);
      if (result.isError) {
        this.alertError(result);
        this.password = "";
      } else {
        this.setToken(result); // Esto no funciona. No lo mete en el localStorage
        localStorage.setItem("token", result); // Así que lo meto manualmente...
        if (this.$route.query && this.$route.query.destinationURL) {
          this.$router.push(this.$route.query.destinationURL);
        } else {
          this.$router.push("/");
        }
      }
    }
  }
};
</script>