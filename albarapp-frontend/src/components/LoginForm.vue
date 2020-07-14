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
        ref="password"
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
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{snackbar.message}}
      <v-btn text @click="snackbar.show=false">Cerrar</v-btn>
    </v-snackbar>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
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
      passwordRules: [v => !!v || "Password obligatoria"],
      emailRules: [v => !!v, v => /.+@.+\..+/.test(v) || "Email inválido"],
      snackbar: {
        show: false,
        message: "",
        color: ""
      },
      spinner: {
        loading: false,
        counter: 0
      }
    };
  },
  created() {
    if (this.$store.getters.authenticated) {
      if (this.$route.query && this.$route.query.destinationURL) {
        this.$router.push(this.$route.query.destinationURL);
      } else {
        this.$router.push("/");
      }
    }
  },
  methods: {
    async login() {
      try {
        this.showSpinner();
        var result = await LoginService.login(this.email, this.password);
        if (result.isError) {
          this.snackbar = {
            show: true,
            message: "Email o credenciales incorrectas.",
            color: "error"
          };
          this.password = "";

          this.$nextTick(this.$refs.password.focus);
        } else {
          this.$store.commit("login", result);
          if (this.$route.query && this.$route.query.destinationURL) {
            this.$router.push(this.$route.query.destinationURL);
          } else {
            this.$router.push("/");
          }
        }
      } catch {
        this.snackbar = {
          show: true,
          message: "Error al intentar realizar la identificación.",
          color: "error"
        };
      } finally {
        this.closeSpinner();
      }
    }
  }
};
</script>