<template>
  <v-content>
    <v-container>
      <v-layout text-right wrap class="pb-10 pt-5 mr-5">
        <v-flex xs12>
          <v-btn to="/customer-creation/">
            <span>Nuevo</span>
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
            <tbody>
              <tr v-for="item in items" :key="item.code">
                <td>{{item.code}}</td>
                <td>{{item.name}}</td>
                <td>{{item.alias}}</td>
                <td>{{item.fiscalId}}</td>
                <td>{{item.phoneNumber}}</td>
                <td>{{item.email}}</td>
                <td>{{item.address}}</td>
                <td>{{item.province}}</td>
                <td>
                  <v-btn @click="updateCustomer(item)">
                    <v-icon dark>mdi-pencil</v-icon>
                  </v-btn>
                </td>
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
    </v-container>
  </v-content>
</template>

<script>
export default {
  name: "CustomerList",
  data: () => {
    return {
      customers: [],
      headers: [
        { text: "Código", sortable: false, value: "code" },
        { text: "Alias", sortable: false, value: "alias" },
        { text: "Nombre", sortable: false, value: "name" },
        { text: "NIF", sortable: false, value: "fiscalId" },
        { text: "Teléfono", sortable: false, value: "phoneNumber" },
        { text: "E-mail", sortable: false, value: "email" },
        { text: "Dirección", sortable: false, value: "address" },
        { text: "Provincia", sortable: false, value: "province" },
        { text: "", sortable: false, value: "update" }
      ],
      search: "",
      sortBy: "code",
      descending: false
    };
  },
  created() {
    this.listCustomers();
  },
  methods: {
    listCustomers() {
      this.$axios
        .get("/customers")
        .then(response => {
          this.customers = response.data._embedded.customers;
        })
        .catch(function(error) {
          alert("Ha ocurrido un error recuperando los clientes");
        });
    },
    updateCustomer(item) {
      this.$router.push({
        name: "Actualizar cliente",
        params: { customerHref: item._links.self.href }
      });
    }
  }
};
</script>