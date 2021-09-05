<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-layout text-right wrap class="pt-2 pb-5 mr-5">
        <v-flex xs12>
          <v-btn :to="{ name: 'InvitationForm' }">
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
            autocomplete="off"
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
                <td>{{ item.email }}</td>
                <td>{{ item.name }}</td>
                <td>{{ item.surname }}</td>
                <td>{{ item.rolesPlain }}</td>
                <td>
                  <v-btn
                    class="mr-2"
                    v-if="item.email !== $store.getters.parsedToken.sub"
                    @click="updateUser(item)"
                  >
                    <v-icon dark>mdi-pencil</v-icon>
                  </v-btn>
                  <v-btn
                    v-if="item.email !== $store.getters.parsedToken.sub"
                    color="red"
                    dark
                    @click="openDeleteDialog(item)"
                  >
                    <v-icon dark>mdi-delete</v-icon>
                  </v-btn>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr>
                <v-card
                  class="flex-content"
                  outlined
                  v-for="item in items"
                  :key="item.email"
                >
                  <v-card-text>
                    <span class="black--text">Email:</span>
                    {{ item.email }}
                    <br />
                    <span class="black--text">Nombre:</span>
                    {{ item.name }}
                    <br />
                    <span class="black--text">Apellidos:</span>
                    {{ item.surname }}
                    <br />
                    <span class="black--text">Roles:</span>
                    {{ item.rolesPlain }}
                  </v-card-text>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn
                      v-if="item.email !== $store.getters.parsedToken.sub"
                      @click="updateUser(item)"
                    >
                      <v-icon dark>mdi-pencil</v-icon>
                    </v-btn>
                    <v-btn
                      v-if="item.email !== $store.getters.parsedToken.sub"
                      color="red"
                      dark
                      @click="openDeleteDialog(item)"
                    >
                      <v-icon dark>mdi-delete</v-icon>
                    </v-btn>
                    <v-spacer></v-spacer>
                  </v-card-actions>
                </v-card>
              </tr>
            </tbody>
          </template>
        </v-data-table>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener los usuarios, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="listUsers()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-dialog v-model="dialogDelete.show" max-width="600">
      <v-card>
        <v-card-title class="headline">¿Eliminar usuario?</v-card-title>
        <v-card-text
          >Si sigue adelante eliminará el usuario {{ dialogDelete.user.email }}.
          Esto hará que dicho usuario no pueda iniciar sesión. Este proceso es
          irreversible.</v-card-text
        >
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" text @click="dialogDelete.show = false"
            >Cancelar</v-btn
          >
          <v-btn
            color="red darken-1"
            text
            @click="deleteUser(dialogDelete.user)"
            >Confirmar</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import UserService from "@/services/UserService.js";

export default {
  name: "AdminPage",
  data: () => {
    return {
      users: [],
      dialogDelete: {
        show: false,
        user: { email: "" },
      },
      headers: [
        { text: "Email", sortable: true, value: "email" },
        { text: "Nombre", sortable: false, value: "name" },
        { text: "Apellidos", sortable: true, value: "surname" },
        { text: "Roles", sortable: false, value: "rolesPlain" },
        { text: "", sortable: false, value: "actions" },
      ],
      search: "",
      sortBy: "email",
      descending: false,
      form: {
        valid: Boolean,
        Invitation: {
          email: "",
          role: "",
        },
      },
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0,
      },
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
    },
    openDeleteDialog(user) {
      this.dialogDelete.user = user;
      this.dialogDelete.show = true;
    },
    async deleteUser(user) {
      await UserService.delete(user.id);
      this.listUsers();
      this.dialogDelete.show = false;
    },
    updateUser(user) {
      this.$router.push({ name: "UserUpdate", params: { id: user.id } });
    },
  },
};
</script>

<style scoped>
</style>