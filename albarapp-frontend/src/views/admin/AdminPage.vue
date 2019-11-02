<template>
  <v-flex align-self-start>
    <v-layout text-right wrap class="pb-10 pt-5 mr-5">
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
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn to="/">
          <span>Volver</span>
        </v-btn>
      </v-flex>
    </v-layout>
  </v-flex>
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
      }
    };
  },
  async created() {
    this.users = await UserService.getAllUsers();
  },
  methods: {}
};
</script>

<style scoped>
</style>