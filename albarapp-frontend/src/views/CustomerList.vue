<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-layout text-right wrap class="pt-2 pb-5 mr-5">
        <v-flex xs12>
          <v-btn to="/customer-creation/">
            Nuevo
            <v-icon class="ml-2">mdi-plus-circle</v-icon>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-card>
        <v-card-title>
          Listado de clientes
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
          :loading="!customers || customers.length == 0"
          loading-text="Cargando... Por favor, espere"
          :headers="headers"
          :items="customers"
          :search="search"
          :sort-by.sync="sortBy"
          :sort-desc.sync="descending"
          :items-per-page="15"
        >
          <template v-slot:body="{ items }">
            <tbody v-if="!$vuetify.breakpoint.xsOnly">
              <tr v-for="item in items" :key="item.code">
                <td>{{item.code}}</td>
                <td>{{item.alias}}</td>
                <td>{{item.name}}</td>
                <td>{{item.phoneNumber}}</td>
                <td>
                  <v-btn @click="showCustomer(item)">
                    <v-icon dark>mdi-badge-account-horizontal-outline</v-icon>
                  </v-btn>
                </td>
                <td>
                  <v-btn @click="updateCustomer(item)">
                    <v-icon dark>mdi-pencil</v-icon>
                  </v-btn>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr>
                <v-card class="flex-content" outlined v-for="item in items" :key="item.code">
                  <v-card-text>
                    <span class="black--text">Código:</span>
                    {{item.code}}
                    <br />
                    <span class="black--text">Nombre Comercial:</span>
                    {{item.alias}}
                    <br />
                    <span class="black--text">Razón social:</span>
                    {{item.name}}
                    <br />
                    <span class="black--text">Teléfono:</span>
                    {{item.phoneNumber}}
                    <br />
                  </v-card-text>
                  <v-card-actions>
                    <v-layout text-center wrap>
                      <v-flex xs12 class="mb-4">
                        <v-btn @click="showCustomer(item)">
                          <v-icon dark>mdi-badge-account-horizontal-outline</v-icon>
                        </v-btn>
                        <v-btn @click="updateCustomer(item)">
                          <v-icon dark>mdi-pencil</v-icon>
                        </v-btn>
                      </v-flex>
                    </v-layout>
                  </v-card-actions>
                </v-card>
              </tr>
            </tbody>
          </template>
        </v-data-table>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row
        class="mb-2"
        justify="center"
      >Error al obtener los clientes, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="loadCustomers()">
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
import CustomerService from "@/services/CustomerService.js";

export default {
  name: "CustomerList",
  data: () => {
    return {
      customers: [],
      headers: [
        { text: "Código", sortable: true, value: "code" },
        { text: "Nombre Comercial", sortable: false, value: "alias" },
        { text: "Razón Social", sortable: false, value: "name" },
        { text: "Teléfono", sortable: false, value: "phoneNumber" },
        { text: "", sortable: false, value: "detail" },
        { text: "", sortable: false, value: "update" }
      ],
      search: "",
      sortBy: "code",
      descending: false,
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0
      }
    };
  },
  async created() {
    this.loadCustomers();
  },
  methods: {
    async loadCustomers() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.customers = await CustomerService.getAll();
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    showCustomer(item) {
      this.$router.push({
        name: "CustomerDetail",
        params: { customerId: item.id }
      });
    },
    updateCustomer(item) {
      this.$router.push({
        name: "CustomerUpdate",
        params: { customerId: item.id }
      });
    }
  }
};
</script>