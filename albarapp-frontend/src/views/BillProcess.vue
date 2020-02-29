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
    <v-dialog v-model="dialogInvoicesCreated.show" max-width="600">
      <v-card>
        <v-card-title class="headline">Facturas creadas</v-card-title>
        <v-card-text>Se han generado {{numberInvoicesCreated}} facturas. ¿Desea descargarlas en formato PDF?</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" text @click="dialogInvoicesCreated.show = false">No</v-btn>
          <v-btn color="red darken-1" text @click="downloadCreatedInvoices()">Sí</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{snackbar.message}}
      <v-btn text @click="snackbar.show=false">Cerrar</v-btn>
    </v-snackbar>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
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
    dialogInvoicesCreated: {
      show: false
    },
    invoicesCreated: [],
    snackbar: {
      show: false,
      message: "",
      color: ""
    },
    spinner: {
      loading: false,
      counter: 0
    },
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
      try {
        this.showSpinner();
        var invoicesCreated = await InvoiceService.createList(
          this.form.customerCodeFrom,
          this.form.customerCodeTo,
          this.$moment
            .utc(this.form.dateFromFormatted, "DD/MM/YYYY")
            .format("x"),
          this.$moment.utc(this.form.dateToFormatted, "DD/MM/YYYY").format("x"),
          this.$moment
            .utc(this.form.issuedDateFormatted, "DD/MM/YYYY")
            .format("x")
        );
        this.numberInvoicesCreated = invoicesCreated.length;
        this.invoicesCreated = invoicesCreated.map(dto => dto.id);
        if (this.numberInvoicesCreated) {
          this.dialogInvoicesCreated.show = true;
          this.$refs.form.reset();
        } else {
          this.snackbar = {
            show: true,
            message:
              "No hay albaranes pendientes de facturar, por favor revisa los datos introducidos.",
            color: "error"
          };
        }
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido generar las facturas, por favor vuelva a intentarlo.",
          color: "error"
        };
      } finally {
        this.closeSpinner();
      }
    },
    async downloadCreatedInvoices() {
      this.dialogInvoicesCreated.show = false;
      try {
        this.showSpinner();
        await InvoiceService.downloadPdfMultiple(this.invoicesCreated);
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido descargar las facturas, por favor descárguelas desde el listado.",
          color: "error"
        };
      } finally {
        this.closeSpinner();
      }
    },
    parseDateFromPick() {
      var moment = this.$moment.utc(this.form.dateFrom, "YYYY-MM-DD", true);
      this.form.dateFromFormatted = moment.format("DD/MM/YYYY");
      this.menuDateFromPicker = false;
    },
    parseDateFromText() {
      var moment = this.$moment.utc(
        this.form.dateFromFormatted,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.dateFrom = moment.format("YYYY-MM-DD");
        this.form.dateFromFormatted = moment.format("DD/MM/YYYY");
      } else {
        this.form.dateFrom = "";
        this.form.dateFromFormatted = "";
      }
    },
    parseDateToPick() {
      var moment = this.$moment.utc(this.form.dateTo, "YYYY-MM-DD", true);
      this.form.dateToFormatted = moment.format("DD/MM/YYYY");
      this.menuDateToPicker = false;
    },
    parseDateToText() {
      var moment = this.$moment.utc(
        this.form.dateToFormatted,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.dateTo = moment.format("YYYY-MM-DD");
        this.form.dateToFormatted = moment.format("DD/MM/YYYY");
      } else {
        this.form.dateTo = "";
        this.form.dateToFormatted = "";
      }
    },
    parseIssuedDatePick() {
      var moment = this.$moment.utc(this.form.issuedDate, "YYYY-MM-DD", true);
      this.form.issuedDateFormatted = moment.format("DD/MM/YYYY");
      this.menuIssuedDatePicker = false;
    },
    parseIssuedDateText() {
      var moment = this.$moment.utc(
        this.form.issuedDateFormatted,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.issuedDate = moment.format("YYYY-MM-DD");
        this.form.issuedDateFormatted = moment.format("DD/MM/YYYY");
      } else {
        this.form.issuedDate = "";
        this.form.issuedDateFormatted = "";
      }
    }
  }
};
</script>