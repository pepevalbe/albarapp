<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1">Invitar usuario</v-subheader>
      <v-text-field v-model="form.email" :rules="emailRules" label="Email" required></v-text-field>
      <v-text-field v-model="form.role" :rules="roleRules" label="Tipo" required></v-text-field>
    </v-form>
    <v-btn class="mr-4" to="/admin">Volver</v-btn>
    <v-btn :disabled="!form.valid" color="success" @click="sendInvitation()">Enviar</v-btn>
  </v-container>
</template>

<script>
import AdminService from "@/services/AdminService.js";

export default {
  name: "InvitationForm",
  data: () => ({
    form: {
      valid: false,
      email: "",
      role: ""
    },
    emailRules: [
      v => !!v || "El email es necesario",
      v => /.+@.+\..+/.test(v) || "Email inválido"
    ],
    roleRules: [v => !!v || "El tipo es necesario"]
  }),
  methods: {
    sendInvitation: async function() {
      await AdminService.sendInvitation(this.form.email, this.form.role);
      this.reset();
    },
    reset: function() {
      this.$refs.form.reset();
    }
  }
};
</script>