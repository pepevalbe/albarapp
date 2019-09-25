<template>
  <v-content>
    <v-container>
      <v-layout text-right wrap class="pb-10 pt-5 mr-5">
        <v-flex xs12>
          <v-btn to="/new-customer/">
            <span>Nuevo</span>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-data-table :loading="!customers || customers.length == 0" loading-text="Cargando... Por favor, espere" :headers="headers" :items="customers" :items-per-page="15">
      </v-data-table>
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
          {text: "Código", sortable: false, value: "code"},
          {text: "Alias", sortable: false, value: "alias"},
          {text: "Nombre", sortable: false, value: "name"},
          {text: "NIF", sortable: false, value: "fiscalId"},
          {text: "Teléfono", sortable: false, value: "phoneNumber"},
          {text: "E-mail", sortable: false, value: "email"},
          {text: "Dirección", sortable: false, value: "address"},
          {text: "Provincia", sortable: false, value: "province"}
      ]
    };
  },
  created() {
    this.listCustomers();
  },
  methods: {
    listCustomers() {
      this.$axios
        .get('/customers')
        .then(response => {
          this.customers = response.data._embedded.customers 
        })
        .catch(function (error) {
          alert('Ha ocurrido un error recuperando los clientes')
        })
    }
  }
};
</script>