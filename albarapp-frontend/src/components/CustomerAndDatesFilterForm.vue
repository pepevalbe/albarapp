<template>
  <v-container>
    <v-expansion-panels v-model="panelsExpanded">
      <v-expansion-panel>
        <v-expansion-panel-header>
          <span class="subtitle-1 font-italic font-weight-light">Filtrar resultados</span>
        </v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-form ref="form" v-model="form.valid">
            <v-row>
              <v-col cols="12" md="6">
                <v-autocomplete
                  v-model="customer"
                  label="Cliente"
                  :items="customers"
                  item-text="alias"
                  return-object
                  clearable
                  no-data-text="Sin coincidencias"
                  v-on:change="selectCustomerByAlias()"
                ></v-autocomplete>
              </v-col>
              <v-col cols="12" md="3">
                <v-menu
                  ref="menuDatePicker"
                  v-model="menuDateFromPicker"
                  :close-on-content-click="false"
                  transition="scale-transition"
                  offset-y
                  max-width="290px"
                  min-width="290px"
                >
                  <template v-slot:activator="{ on }">
                    <v-text-field
                      v-model="dateFromFormatted"
                      clearable
                      ref="dateText"
                      label="Desde"
                      hint="Formato: ddMMaaaa"
                      persistent-hint
                      @focus="$event.target.select()"
                      prepend-icon="mdi-calendar"
                      @blur="parseDateFromText()"
                      @click:clear="form.dateFrom = ''"
                      v-on="on"
                    ></v-text-field>
                  </template>
                  <v-date-picker
                    v-model="form.dateFrom"
                    no-title
                    @input="parseDateFromPick()"
                    locale="es-ES"
                    first-day-of-week="1"
                  ></v-date-picker>
                </v-menu>
              </v-col>
              <v-col cols="12" md="3">
                <v-menu
                  ref="menuDatePicker"
                  v-model="menuDateToPicker"
                  :close-on-content-click="false"
                  transition="scale-transition"
                  offset-y
                  max-width="290px"
                  min-width="290px"
                >
                  <template v-slot:activator="{ on }">
                    <v-text-field
                      v-model="dateToFormatted"
                      clearable
                      ref="dateText"
                      label="Hasta"
                      hint="Formato: ddMMaaaa"
                      persistent-hint
                      @focus="$event.target.select()"
                      prepend-icon="mdi-calendar"
                      @blur="parseDateToText()"
                      @click:clear="form.dateTo = ''"
                      v-on="on"
                    ></v-text-field>
                  </template>
                  <v-date-picker
                    v-model="form.dateTo"
                    no-title
                    @input="parseDateToPick()"
                    locale="es-ES"
                    first-day-of-week="1"
                  ></v-date-picker>
                </v-menu>
              </v-col>
            </v-row>
          </v-form>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>

    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-container>
</template>
<script>
import CustomerService from "@/services/CustomerService.js";

export default {
  name: "CustomerAndDatesFilterForm",
  props: {
    form: {
      valid: Boolean,
      customerCode: String,
      dateFrom: String,
      dateTo: String
    }
  },
  data: () => ({
    panelsExpanded: null,
    customerCode: "",
    customerCodeRules: [
      v =>
        (v && v > 0 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos"
    ],
    customers: [],
    customer: {},
    dateFromFormatted: "",
    dateToFormatted: "",
    menuDateFromPicker: false,
    menuDateToPicker: false,
    spinner: {
      loading: false,
      counter: 0
    }
  }),
  created() {
    this.listCustomers();
    if (this.form.dateFrom) this.parseDateFromPick();
    if (this.form.dateTo) this.parseDateToPick();
    if (this.form.dateTo || this.form.dateFrom || this.form.customerCode) {
      this.panelsExpanded = 0;
    }
  },
  methods: {
    async listCustomers() {
      this.showSpinner();
      this.customers = await CustomerService.getAll();
      this.customers.forEach(function(element) {
        element.alias = element.code + " - " + element.alias;
      });
      this.customers.sort(function(a, b) {
        if (a.code < b.code) return -1;
        if (a.code > b.code) return 1;
        if (a.code == b.code) return 0;
      });
      this.selectCustomerByCode();
      this.closeSpinner();
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
          this.$nextTick(this.$refs.customerCode.focus);
        } else {
          this.customer = this.customers[index];
        }
      }
    },
    selectCustomerByAlias() {
      if (
        this.customer != {} &&
        this.customer != null &&
        this.customer != undefined &&
        this.customer.code != null
      ) {
        this.form.customerCode = this.customer.code;
      } else {
        this.form.customerCode = null;
      }
      return false;
    },
    clearCustomer() {
      this.form.customer = {};
    },
    parseDateFromPick() {
      var moment = this.$moment(this.form.dateFrom, "YYYY-MM-DD", true);
      this.dateFromFormatted = moment.format("DD/MM/YYYY");
      this.menuDateFromPicker = false;
    },
    parseDateFromText() {
      var moment = this.$moment(
        this.dateFromFormatted,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.dateFrom = moment.format("YYYY-MM-DD");
        this.dateFromFormatted = moment.format("DD/MM/YYYY");
      } else {
        this.form.dateFrom = "";
        this.dateFromFormatted = "";
      }
    },
    parseDateToPick() {
      var moment = this.$moment(this.form.dateTo, "YYYY-MM-DD", true);
      this.dateToFormatted = moment.format("DD/MM/YYYY");
      this.menuDateToPicker = false;
    },
    parseDateToText() {
      var moment = this.$moment(
        this.dateToFormatted,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.dateTo = moment.format("YYYY-MM-DD");
        this.dateToFormatted = moment.format("DD/MM/YYYY");
      } else {
        this.form.dateTo = "";
        this.dateToFormatted = "";
      }
    }
  }
};
</script>