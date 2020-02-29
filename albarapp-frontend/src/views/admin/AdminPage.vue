<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-layout text-right wrap class="pt-2 pb-5 mr-5">
        <v-flex xs12>
          <v-btn to="/invitation/">
            Enviar Invitación
            <v-icon class="ml-4">mdi-account-plus</v-icon>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-card>
        <v-card-title>
          Listado de usuarios
          <div class="flex-grow-1"></div>
          <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            label="Buscar ..."
            single-line
            hide-details
          ></v-text-field>
        </v-card-title>
        <v-data-table
          :loading="!users || users.length == 0"
          loading-text="Cargando... Por favor, espere"
          :headers="headers"
          :items="users"
          :search="search"
          :sort-by.sync="sortBy"
          :sort-desc.sync="descending"
          :items-per-page="15"
        >
          <template v-slot:body="{ items }">
            <tbody v-if="!$vuetify.breakpoint.xsOnly">
              <tr v-for="item in items" :key="item.email">
                <td>{{item.email}}</td>
                <td>{{item.name}}</td>
                <td>{{item.surname}}</td>
                <td>{{item.role}}</td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr>
                <v-card class="flex-content" outlined v-for="item in items" :key="item.email">
                  <v-card-text>
                    <span class="black--text">Email:</span>
                    {{item.email}}
                    <br />
                    <span class="black--text">Nombre:</span>
                    {{item.name}}
                    <br />
                    <span class="black--text">Apellidos:</span>
                    {{item.surname}}
                    <br />
                    <span class="black--text">Tipo:</span>
                    {{item.role}}
                    <br />
                  </v-card-text>
                </v-card>
              </tr>
            </tbody>
          </template>
        </v-data-table>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center">Error al obtener los usuarios, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="listUsers()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
  </v-container>
</template>

<script>
import UserService from "@/services/UserService.js";

export default {
  name: "AdminPage",
  data: () => {
    return {
      users: [],
      headers: [
        { text: "Email", sortable: true, value: "email" },
        { text: "Nombre", sortable: false, value: "name" },
        { text: "Apellidos", sortable: true, value: "surname" },
        { text: "Tipo", sortable: false, value: "role" }
      ],
      search: "",
      sortBy: "email",
      descending: false,
      form: {
        valid: Boolean,
        Invitation: {
          email: "",
          role: ""
        }
      },
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0
      }
    };
  },
  async created() {
    this.listUsers();
  },
  methods: {
    async listUsers() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.users = await UserService.getAllUsers();
      } catch {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    }
  }
};
</script>

<style scoped>
</style>