<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-subheader class="title ml-1">Información de usuario</v-subheader>
      <v-text-field
        v-model="user.name"
        autocomplete="off"
        label="Nombre"
        readonly
      ></v-text-field>
      <v-text-field
        v-model="user.surname"
        label="Apellidos"
        readonly
      ></v-text-field>
      <v-text-field v-model="user.email" label="Email" readonly></v-text-field>
      <v-select
        v-model="user.roles"
        @change="rolesRules"
        :items="roles"
        attach
        chips
        label="Roles"
        multiple
      ></v-select>
      <v-row justify="center">
        <v-btn
          class="ma-2"
          :disabled="!user.roles.length"
          color="success"
          @click="updateUser"
          >Guardar</v-btn
        >
        <v-btn class="ma-2" @click="$router.back()">Volver</v-btn>
      </v-row>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener los datos del usuario, por favor vuelva a
        cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadUser()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-container>
</template>

<script>
import UserService from "@/services/UserService.js";

export default {
  name: "UserUpdate",
  props: {
    id: String,
  },
  data: () => {
    return {
      user: {
        id: "",
        name: "",
        surname: "",
        email: "",
        roles: [],
      },
      roles: ["ADMIN", "HENS_BATCH_USER", "BILLING_USER"],
      spinner: {
        loading: false,
        counter: 0,
      },
      dialogDelete: {
        show: false,
        deliveryNote: {},
      },
    };
  },
  async created() {
    await this.loadUser();
    this.user.roles.push("INVENT");
  },
  methods: {
    async loadUser() {
      try {
        this.errorLoading = false;
        this.showSpinner();
        this.user = await UserService.getUser(this.id);
      } catch {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    rolesRules(newRolesValue) {
      if (newRolesValue.includes("ADMIN")) {
        this.user.roles = ["ADMIN"];
      } else {
        this.user.roles = this.user.roles.filter((role) => this.roles.includes(role));
      }
    },
    async updateUser() {
      try {
        this.errorLoading = false;
        this.showSpinner();
        await UserService.updateUser(this.user);
        this.loadUser();
      } catch {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>

<style>
</style>