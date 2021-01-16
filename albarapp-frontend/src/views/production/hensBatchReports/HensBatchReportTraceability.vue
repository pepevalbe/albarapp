<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
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
                autocomplete="off"
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
                autocomplete="off"
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
      <v-row class="mt-5" justify="center">
        <v-btn
          ref="billButton"
          class="mr-4"
          :disabled="!form.valid"
          @click="calculateTraceability()"
          >Buscar</v-btn
        >
      </v-row>
    </v-form>
    <v-data-table
      loading-text="Cargando... Por favor, espere"
      :headers="headers"
      :items="traceabilities"
      :items-per-page="15"
    />
  </v-container>
</template>

<script>
import DeliveryNoteService from "@/services/DeliveryNoteService.js";
import HensBatchReportService from "@/services/production/HensBatchReportService.js";
import HensBatchService from "@/services/production/HensBatchService.js";

var ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
var THREE_DAYS_IN_MILLIS = 3 * ONE_DAY_IN_MILLIS;

export default {
  name: "HensBatchReportTraceability",
  data: () => ({
    form: {
      valid: false,
      dateFrom: "",
      dateFromFormatted: "",
      dateTo: "",
      dateToFFormatted: "",
    },
    menuDateFromPicker: false,
    menuDateToPicker: false,
    traceabilities: [],
    headers: [
      { text: "Fecha", sortable: false, value: "date" },
      { text: "Cliente", sortable: false, value: "customer" },
      { text: "Lote envasado", sortable: false, value: "batchCode" },
      { text: "Lote de gallinas", sortable: false, value: "batchName" },
    ],
    spinner: {
      loading: false,
      counter: 0,
    },
    dateFromRules: [(v) => !!v || "La fecha desde es obligatoria"],
    dateToRules: [(v) => !!v || "La fecha hasta es obligatoria"],
  }),
  methods: {
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
    async getDeliveryNotesForTraceability() {
      var productCodes = [1, 2, 3, 4, 5, 6, 7];
      var filter = {
        form: {
          dateFrom: this.form.dateFrom,
          dateTo: this.form.dateTo,
        },
        products: {
          productCodes: productCodes,
        },
      };
      var options = {
        sortBy: ["issuedTimestamp", "id"],
        sortDesc: [true],
      };
      var response = await DeliveryNoteService.getAllWithCustomerAndTotal(
        filter,
        options
      );
      return response.deliveryNotes;
    },
    async getReportsForTraceability() {
      var timestampFrom = this.$moment
        .utc(this.form.dateFrom, "YYYY-MM-DD")
        .format("x");
      var timestampTo = this.$moment
        .utc(this.form.dateTo, "YYYY-MM-DD")
        .format("x");
      var reports = await HensBatchReportService.getByInterval(
        timestampFrom - THREE_DAYS_IN_MILLIS - ONE_DAY_IN_MILLIS,
        timestampTo - ONE_DAY_IN_MILLIS
      );

      return reports;
    },
    plainReports(reports, hensBatches) {
      var plainReports = reports.map((report) => {
        var hensBatch = hensBatches.find((hb) => hb.id === report.hensBatchId);
        var matches = hensBatch.name.match(/\b(\w)/g);
        var acronym = matches.slice(0, 2).join("");
        var plainReport = {
          reportTimestamp: report.reportTimestamp,
          batchCode:
            acronym +
            this.$moment.utc(report.reportTimestamp).format("DDMMYYYY"),
          batchName: hensBatch.name,
          eggsQuantity:
            report.numXL +
            report.numL +
            report.numM +
            report.numS +
            report.numXS,
        };
        return plainReport;
      });
      plainReports.sort((a, b) => {
        var diffTimestamp = a.reportTimestamp - b.reportTimestamp;
        if (!diffTimestamp) {
          return a.batchName.localeCompare(b.batchName);
        } else {
          return diffTimestamp;
        }
      });
      return plainReports;
    },
    associateReportsToDeliveryNotes(deliveryNotes, plainReports) {
      var traceabilities = [];
      deliveryNotes.forEach((dn) => {
        if (
          !plainReports.length ||
          dn.issuedTimestamp -
            plainReports[plainReports.length - 1].reportTimestamp >
            THREE_DAYS_IN_MILLIS
        ) {
          return;
        }
        while (
          dn.issuedTimestamp <=
          plainReports[plainReports.length - 1].reportTimestamp
        ) {
          plainReports.pop();
        }
        traceabilities.push({
          timestamp: dn.issuedTimestamp,
          date: this.$moment.utc(dn.issuedTimestamp).format("DD/MM/YYYY"),
          customer: dn.customerAlias,
          deliveryNoteId: "A" + dn.id,
          batchCode: plainReports[plainReports.length - 1].batchCode,
          batchName: plainReports[plainReports.length - 1].batchName,
        });
        dn.deliveryNoteItems.forEach((dni) => {
          plainReports[plainReports.length - 1].eggsQuantity -= dni.quantity;
        });
        if (plainReports[plainReports.length - 1].eggsQuantity <= 0) {
          plainReports.pop();
        }
      });
      return traceabilities;
    },
    async calculateTraceability() {
      var deliveryNotes = await this.getDeliveryNotesForTraceability();
      var reports = await this.getReportsForTraceability();
      var hensBatches = await HensBatchService.getAll();
      var plainReports = this.plainReports(reports, hensBatches);
      this.traceabilities = this.associateReportsToDeliveryNotes(deliveryNotes, plainReports);
    },
  },
};
</script>