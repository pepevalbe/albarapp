<template>
  <v-flex align-self-start>
    <v-form ref="form" v-model="valid">
      <v-subheader class="title ml-1">Invitar usuario</v-subheader>
      <v-text-field v-model="email" :rules="emailRules" autocomplete="off" label="Email" required></v-text-field>
      <v-select v-model="role" :items="roles" :rules="roleRules" label="Tipo" required></v-select>
    </v-form>
    <v-btn class="mr-4" to="/admin">Volver</v-btn>
    <v-btn :disabled="!valid" color="success" @click="sendInvitation()">Enviar</v-btn>
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
  name: "InvitationForm",
  data: () => ({
    valid: false,
    email: "",
    role: null,
    roles: ["USER", "ADMIN"],
    emailRules: [
      (v) => !!v || "El email es necesario",
      (v) => /.+@.+\..+/.test(v) || "Email inválido",
    ],
    roleRules: [(v) => !!v || "El tipo es necesario"],
    snackbar: {
      show: false,
      message: "",
      color: "",
    },
    spinner: {
      loading: false,
      counter: 0,
    },
  }),
  methods: {
    async sendInvitation() {
      try {
        this.showSpinner();
        await UserService.sendInvitation(this.email, this.role);
        this.snackbar = {
          show: true,
          message: "Invitación enviada correctamente",
          color: "success",
        };
        this.$refs.form.reset();
      } catch {
        this.snackbar = {
          show: true,
          message: "Ha ocurrido un error al intentar enviar la invitación",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>