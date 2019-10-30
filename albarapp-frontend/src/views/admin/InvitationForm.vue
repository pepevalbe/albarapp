<template>
  <v-flex align-self-start>
    <v-form ref="form" v-model="valid">
      <v-subheader class="title ml-1">Invitar usuario</v-subheader>
      <v-text-field v-model="email" :rules="emailRules" label="Email" required></v-text-field>
      <v-select v-model="role" :items="roles" :rules="roleRules" label="Tipo" required></v-select>
    </v-form>
    <v-btn class="mr-4" to="/admin">Volver</v-btn>
    <v-btn :disabled="!valid" color="success" @click="sendInvitation()">Enviar</v-btn>
  </v-flex>
</template>

<script>
import AdminService from "@/services/AdminService.js";

export default {
  name: "InvitationForm",
  data: () => ({
    valid: false,
    email: "",
    role: null,
    roles: ["USER", "ADMIN"],
    emailRules: [
      v => !!v || "El email es necesario",
      v => /.+@.+\..+/.test(v) || "Email inválido"
    ],
    roleRules: [v => !!v || "El tipo es necesario"]
  }),
  methods: {
    sendInvitation: async function() {
      await AdminService.sendInvitation(this.email, this.role);
      this.$refs.form.reset();
    }
  }
};
</script>