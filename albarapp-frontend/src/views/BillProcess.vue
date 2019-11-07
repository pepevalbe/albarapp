<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1">Proceso de facturación</v-subheader>
      <v-row justify="center">
        <v-col cols="12" md="3">
          <v-text-field
            v-model="form.customerCodeFrom"
            type="number"
            :counter="5"
            autofocus
            :rules="codeFromRules"
            label="Código desde *"
            required
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-text-field
            v-model="form.customerCodeTo"
            :counter="5"
            :rules="codeToRules"
            label="Código hasta *"
            required
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row justify="center">
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
                v-model="form.dateFromFormatted"
                ref="dateText"
                label="Fecha desde"
                hint="Formato: ddMMaaaa"
                :rules="dateFromRules"
                persistent-hint
                @focus="$event.target.select()"
                prepend-icon="mdi-calendar"
                @blur="parseDateFromText()"
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
                v-model="form.dateToFormatted"
                ref="dateText"
                label="Fecha hasta"
                hint="Formato: ddMMaaaa"
                :rules="dateToRules"
                persistent-hint
                @focus="$event.target.select()"
                prepend-icon="mdi-calendar"
                @blur="parseDateToText()"
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
      <v-row justify="center">
        <v-col cols="12" md="3">
          <v-menu
            ref="menuDatePicker"
            v-model="menuIssuedDatePicker"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            max-width="290px"
            min-width="290px"
          >
            <template v-slot:activator="{ on }">
              <v-text-field
                v-model="form.issuedDateFormatted"
                ref="dateText"
                label="Fecha de emisión"
                hint="Formato: ddMMaaaa"
                :rules="issuedDateRules"
                persistent-hint
                @focus="$event.target.select()"
                prepend-icon="mdi-calendar"
                @blur="parseIssuedDateText()"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
              v-model="form.issuedDate"
              no-title
              @input="parseIssuedDatePick()"
              locale="es-ES"
              first-day-of-week="1"
            ></v-date-picker>
          </v-menu>
        </v-col>
      </v-row>
    </v-form>
    <v-row class="mt-5" justify="center">
      <v-btn
        ref="billButton"
        class="mr-4"
        :disabled="!form.valid"
        @click="createInvoices()"
      >Facturar</v-btn>
      <v-btn to="/invoice-list/">Volver</v-btn>
    </v-row>
    <v-snackbar v-model="snackbar">
      Creadas {{numberInvoicesCreated}} facturas correctamente
      <v-btn color="success" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
import InvoiceService from "@/services/InvoiceService.js";

export default {
  name: "BillProcess",
  data: () => ({
    form: {
      valid: false,
      dateFrom: "",
      dateFromFormatted: "",
      dateTo: "",
      dateToFFormatted: "",
      issuedDate: "",
      issuedDateFormatted: "",
      customerCodeFrom: "",
      customerCodeTo: ""
    },
    menuDateFromPicker: false,
    menuDateToPicker: false,
    menuIssuedDatePicker: false,
    snackbar: false,
    numberInvoicesCreated: "",
    codeFromRules: [
      v => !!v || "El código es obligatorio",
      v =>
        (v && v > 0 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos"
    ],
    codeToRules: [
      v => !!v || "El código es obligatorio",
      v =>
        (v && v > 0 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos"
    ],
    dateFromRules: [v => !!v || "La fecha desde es obligatoria"],
    dateToRules: [v => !!v || "La fecha hasta es obligatoria"],
    issuedDateRules: [v => !!v || "La fecha de emisión es obligatoria"]
  }),
  methods: {
    async createInvoices() {
      await InvoiceService.createList(
        this.form.customerCodeFrom,
        this.form.customerCodeTo,
        this.$moment(this.form.dateFromFormatted, "DD/MM/YYYY").format("x"),
        this.$moment(this.form.dateToFormatted, "DD/MM/YYYY").format("x"),
        this.$moment(this.form.issuedDateFormatted, "DD/MM/YYYY").format("x")
      ).then(values => {
        this.numberInvoicesCreated = values.length;
        this.snackbar = true;
        this.$refs.form.reset();
      });
    },
    parseDateFromPick() {
      var moment = this.$moment(this.form.dateFrom, "YYYY-MM-DD", true);
      this.form.dateFromFormatted = moment.format("DD/MM/YYYY");
      this.menuDateFromPicker = false;
    },
    parseDateFromText() {
      var moment = this.$moment(
        this.form.dateFromFormatted,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.dateFrom = moment.format("YYYY-MM-DD");
        this.form.dateFromFormatted = moment.format("DD/MM/YYYY");
      }
    },
    parseDateToPick() {
      var moment = this.$moment(this.form.dateTo, "YYYY-MM-DD", true);
      this.form.dateToFormatted = moment.format("DD/MM/YYYY");
      this.menuDateToPicker = false;
    },
    parseDateToText() {
      var moment = this.$moment(
        this.form.dateToFormatted,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.dateTo = moment.format("YYYY-MM-DD");
        this.form.dateToFormatted = moment.format("DD/MM/YYYY");
      }
    },
    parseIssuedDatePick() {
      var moment = this.$moment(this.form.issuedDate, "YYYY-MM-DD", true);
      this.form.issuedDateFormatted = moment.format("DD/MM/YYYY");
      this.menuIssuedDatePicker = false;
    },
    parseIssuedDateText() {
      var moment = this.$moment(
        this.form.issuedDateFormatted,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.issuedDate = moment.format("YYYY-MM-DD");
        this.form.issuedDateFormatted = moment.format("DD/MM/YYYY");
      }
    }
  }
};
</script>