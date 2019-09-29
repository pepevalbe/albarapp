<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-row>
        <v-col cols="12" md="2">
          <v-text-field
            ref="customerCode"
            v-model="form.customerCode"
            type="number"
            :counter="5"
            :rules="codeRules"
            label="Código cliente"
            required
            v-on:blur="selectCustomerByCode()"
            v-on:input="clearCustomer()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="10">
          <v-autocomplete
            v-model="customer"
            label="Alias cliente"
            :items="customers"
            item-text="alias"
            return-object
            no-data-text="Sin coincidencias"
            v-on:blur="selectCustomerByAlias()"
            v-on:change="selectCustomerByAlias()"
          ></v-autocomplete>
        </v-col>
      </v-row>
    </v-form>
    <v-snackbar v-model="snackbar">
      {{snackbarMessage}}
      <v-btn :color="snackbarColor" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
  </v-container>
</template>
<script>
export default {
  data: () => ({
    form: {
      valid: false,
      customerCode: "",
      customerAlias: ""
    },
    customers: [],
    customer: {},
    snackbar: false,
    snackbarMessage: "",
    snackbarColor: ""
  }),
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
    selectCustomerByCode() {
      var vm = this;
      if (this.form.customerCode != "") {
        var index = this.customers.findIndex(function(element) {
          return element.code == vm.form.customerCode;
        });
        if (index === -1) {
          this.snackbar = true;
          this.snackbarMessage = "No existe ningún cliente con ese código";
          this.snackbarColor = "error";
          this.$nextTick(this.$refs.customerCode.focus);
        } else {
          this.customer = this.customers[index];
        }
      }
    },
    clearCustomer() {
      this.customer = {};
    },
    selectCustomerByAlias() {
      if (
        this.customer != {} &&
        this.customer != null &&
        this.customer != undefined
      ) {
        this.form.customerCode = this.customer.code;
      }
    }
  }
};
</script>