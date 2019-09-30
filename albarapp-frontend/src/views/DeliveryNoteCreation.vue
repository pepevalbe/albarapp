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
            label="Código cliente"
            required
            v-on:blur="selectCustomerByCode()"
            v-on:input="clearCustomer()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="7">
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
        <v-col cols="12" md="3">
          <v-menu
            ref="menu1"
            v-model="menu1"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            max-width="290px"
            min-width="290px"
          >
            <template v-slot:activator="{ on }">
              <v-text-field
                v-model="dateFormatted"
                ref="dateText"
                label="Fecha"
                hint="Formatos: ddMMaa, ddMMaaaa, dd/MM/aa, dd/MM/aaaa"
                persistent-hint
                prepend-icon="mdi-calendar"
                @blur="parseDateText()"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
              v-model="date"
              no-title
              @input="parseDatePick()"
              locale="es-ES"
              first-day-of-week="1"
            ></v-date-picker>
          </v-menu>
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
    menu1: false,
    date: "",
    dateFormatted: "",
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
      if (this.form.customerCode != "" && this.form.customerCode != null) {
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
          this.$nextTick(this.$refs.dateText.focus);
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
      this.$nextTick(this.$refs.dateText.focus);
    },
    parseDateText() {
      var date = "";
      var day = "";
      var month = "";
      var year = "";
      switch (this.dateFormatted.length) {
        case 6: // ddMMyy
          day = this.dateFormatted.substring(0, 2);
          month = this.dateFormatted.substring(2, 4);
          year = this.dateFormatted.substring(4, 6);
          date = "20" + year + "-" + month + "-" + day;
          this.dateFormatted = day + "/" + month + "/" + year;
          break;
        case 8:
          if (!isNaN(this.dateFormatted)) {
            // ddMMyyyy
            day = this.dateFormatted.substring(0, 2);
            month = this.dateFormatted.substring(2, 4);
            year = this.dateFormatted.substring(4, 8);
            date = year + "-" + month + "-" + day;
            this.dateFormatted = day + "/" + month + "/" + year.substring(2, 4);
          } else {
            // dd-MM-yy
            day = this.dateFormatted.substring(0, 2);
            month = this.dateFormatted.substring(3, 5);
            year = this.dateFormatted.substring(6, 8);
            date = "20" + year + "-" + month + "-" + day;
            this.dateFormatted = day + "/" + month + "/" + year;
          }
          break;
        case 10: // dd-MM-yyyy
          day = this.dateFormatted.substring(0, 2);
          month = this.dateFormatted.substring(3, 5);
          year = this.dateFormatted.substring(6, 10);
          date = year + "-" + month + "-" + day;
          this.dateFormatted = day + "/" + month + "/" + year;
          break;
        default:
          this.dateFormatted = "";
      }
      this.date = date;
    },
    parseDatePick() {
      var day = this.date.substring(8, 10);
      var month = this.date.substring(5, 7);
      var year = this.date.substring(2, 4);
      this.dateFormatted = day + "/" + month + "/" + year;
    }
  }
};
</script>