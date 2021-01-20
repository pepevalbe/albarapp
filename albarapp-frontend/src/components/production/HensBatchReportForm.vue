<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1"
        >Datos diarios del lote {{ hensBatch.name }}</v-subheader
      >
      <v-menu
        ref="menuDatePicker"
        v-model="menuDatePicker"
        :close-on-content-click="false"
        transition="scale-transition"
        offset-y
        max-width="290px"
        min-width="290px"
      >
        <template v-slot:activator="{ on }">
          <v-text-field
            v-model="dateText"
            ref="dateText"
            label="Fecha *"
            hint="Formato: ddMMaaaa"
            persistent-hint
            autocomplete="off"
            @focus="$event.target.select()"
            prepend-icon="mdi-calendar"
            @blur="parseDateText()"
            @keypress.enter="$refs.numXL.focus()"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker
          v-model="date"
          no-title
          @input="datePickedOnCalendar()"
          locale="es-ES"
          first-day-of-week="1"
        ></v-date-picker>
      </v-menu>
      <v-subheader>Huevos</v-subheader>
      <v-divider></v-divider>
      <v-row class="mr-6 ml-6">
        <v-text-field
          v-model="form.hensBatchReport.numXL"
          ref="numXL"
          type="number"
          :rules="numXLRules"
          autocomplete="off"
          label="XL *"
          required
          autofocus
          @focus="$event.target.select()"
          @keypress.enter="$refs.numL.focus()"
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchReport.numL"
          ref="numL"
          type="number"
          :rules="numLRules"
          autocomplete="off"
          label="L *"
          required
          @focus="$event.target.select()"
          @keypress.enter="$refs.numM.focus()"
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchReport.numM"
          ref="numM"
          type="number"
          :rules="numMRules"
          autocomplete="off"
          label="M *"
          required
          @focus="$event.target.select()"
          @keypress.enter="$refs.numS.focus()"
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchReport.numS"
          ref="numS"
          type="number"
          :rules="numSRules"
          autocomplete="off"
          label="S *"
          required
          @focus="$event.target.select()"
          @keypress.enter="$refs.numXS.focus()"
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchReport.numXS"
          ref="numXS"
          type="number"
          :rules="numXSRules"
          autocomplete="off"
          label="XS *"
          @focus="$event.target.select()"
          @keypress.enter="$refs.dirties.focus()"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchReport.dirties"
          ref="dirties"
          type="number"
          :rules="dirtiesRules"
          autocomplete="off"
          label="Sucios *"
          @focus="$event.target.select()"
          @keypress.enter="$refs.brokens.focus()"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchReport.brokens"
          ref="brokens"
          type="number"
          :rules="brokensRules"
          autocomplete="off"
          label="Rotos *"
          @focus="$event.target.select()"
          @keypress.enter="$refs.deaths.focus()"
          required
        ></v-text-field>
        <v-text-field
          disabled
          label="Total"
          :value="
            parseInt(form.hensBatchReport.numXL) +
            parseInt(form.hensBatchReport.numL) +
            parseInt(form.hensBatchReport.numM) +
            parseInt(form.hensBatchReport.numS) +
            parseInt(form.hensBatchReport.numXS) +
            parseInt(form.hensBatchReport.dirties) +
            parseInt(form.hensBatchReport.brokens)
          "
        >
        </v-text-field>
      </v-row>
      <v-subheader>Gallinas</v-subheader>
      <v-divider></v-divider>
      <v-row class="mr-6 ml-6">
        <v-text-field
          v-model="form.hensBatchReport.deaths"
          ref="deaths"
          type="number"
          :rules="deathsRules"
          autocomplete="off"
          label="Muertas *"
          @focus="$event.target.select()"
          @keypress.enter="$refs.departures.focus()"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchReport.departures"
          ref="departures"
          type="number"
          :rules="departuresRules"
          autocomplete="off"
          label="Salidas *"
          @focus="$event.target.select()"
          @keypress.enter="$refs.maxTemperature.focus()"
          required
        ></v-text-field>
      </v-row>
      <v-subheader>Temperatura</v-subheader>
      <v-divider></v-divider>
      <v-row class="mr-6 ml-6">
        <v-text-field
          v-model="form.hensBatchReport.maxTemperature"
          ref="maxTemperature"
          type="number"
          :rules="maxTempRules"
          autocomplete="off"
          label="Máxima *"
          @focus="$event.target.select()"
          @keypress.enter="$refs.minTemperature.focus()"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchReport.minTemperature"
          ref="minTemperature"
          type="number"
          :rules="minTempRules"
          autocomplete="off"
          label="Mínima *"
          @focus="$event.target.select()"
          @keypress.enter="$refs.waterReading.focus()"
          required
        ></v-text-field>
      </v-row>
      <v-subheader>Consumos</v-subheader>
      <v-divider></v-divider>
      <v-row class="mr-6 ml-6">
        <v-col cols="12" md="3">
          <v-row>
            <v-text-field
              v-model="form.hensBatchReport.waterReading"
              ref="waterReading"
              type="number"
              autocomplete="off"
              label="Lectura contador agua"
              @blur="compareWaterReadingWithLastReport()"
              @focus="$event.target.select()"
              @keypress.enter="$refs.comments.focus()"
            ></v-text-field>
            <v-text-field
              class="ml-2"
              v-model="estimatedWaterConsumption"
              disabled
              label="Consumo estimado"
            ></v-text-field>
          </v-row>
        </v-col>
        <v-col cols="12" md="9">
          <v-row>
            <v-switch
              class="mr-2"
              v-model="switchPoultryMashConsumption"
              label="Cambio de silo"
            ></v-switch>
            <v-text-field
              v-if="switchPoultryMashConsumption"
              v-model="form.hensBatchReport.poultryMashAdditionQuantity"
              type="number"
              autocomplete="off"
              label="Pienso en kilogramos"
            ></v-text-field>
            <v-text-field
              v-if="switchPoultryMashConsumption"
              v-model="form.hensBatchReport.poultryMashAdditionFeedTurn"
              type="number"
              autocomplete="off"
              label="Turno de comida del cambio"
            ></v-text-field>
            <v-text-field
              v-if="switchPoultryMashConsumption"
              v-model="form.hensBatchReport.poultryMashMaxFeedTurns"
              type="number"
              autocomplete="off"
              label="Turnos totales de comida del día"
            ></v-text-field>
          </v-row>
        </v-col>
      </v-row>
      <v-subheader>Observaciones</v-subheader>
      <v-divider></v-divider>
      <v-row class="mr-6 ml-6">
        <v-text-field
          v-model="form.hensBatchReport.comments"
          ref="comments"
          autocomplete="off"
          label="Observaciones"
          @focus="$event.target.select()"
          @keypress.enter="$emit('nextfocus')"
        ></v-text-field>
      </v-row>
    </v-form>
  </v-container>
</template>
        
<script>
import HensBatchReportService from "@/services/production/HensBatchReportService.js";

export default {
  name: "HensBatchReportForm",
  props: {
    form: {
      valid: Boolean,
      hensBatchReport: {
        name: String,
        breed: String,
        animalQuantity: Number,
        birthTimestamp: Number,
      },
    },
    hensBatch: {
      id: String,
      name: String,
      breed: String,
      animalQuantity: Number,
      birthTimestamp: Number,
    },
  },
  created() {
    if (this.form?.hensBatchReport?.reportTimestamp) {
      let moment = this.$moment.utc(
        this.form.hensBatchReport.reportTimestamp,
        "x",
        true
      );
      this.date = moment.format("YYYY-MM-DD");
      this.dateText = moment.format("DD/MM/YYYY");
    } else {
      let moment = this.$moment.utc().startOf("day");
      this.date = moment.format("YYYY-MM-DD");
      this.dateText = moment.format("DD/MM/YYYY");
      this.form.hensBatchReport.reportTimestamp = moment.format("x");
    }
    if (this.form?.hensBatchReport?.poultryMashAdditionQuantity) {
      this.switchPoultryMashConsumption = true;
    }
  },
  data: () => ({
    numXLRules: [
      (v) => !!v || v === 0 || "La cantidad de huevos XL es obligatoria",
    ],
    numLRules: [
      (v) => !!v || v === 0 || "La cantidad de huevos L es obligatoria",
    ],
    numMRules: [
      (v) => !!v || v === 0 || "La cantidad de huevos M es obligatoria",
    ],
    numSRules: [
      (v) => !!v || v === 0 || "La cantidad de huevos S es obligatoria",
    ],
    numXSRules: [
      (v) => !!v || v === 0 || "La cantidad de huevos XS es obligatoria",
    ],
    dirtiesRules: [
      (v) => !!v || v === 0 || "La cantidad de huevos sucios es obligatoria",
    ],
    brokensRules: [
      (v) => !!v || v === 0 || "La cantidad de huevos rotos es obligatoria",
    ],
    deathsRules: [
      (v) => !!v || v === 0 || "La cantidad de gallinas muertas es obligatoria",
    ],
    departuresRules: [
      (v) =>
        !!v ||
        v === 0 ||
        "La cantidad de gallinas dadas de baja es obligatoria",
    ],
    maxTempRules: [
      (v) => !!v || v === 0 || "La temperatura máxima es obligatoria",
    ],
    minTempRules: [
      (v) => !!v || v === 0 || "La temperatura mínima es obligatoria",
    ],
    date: "",
    dateText: "",
    menuDatePicker: false,
    switchPoultryMashConsumption: false,
    estimatedWaterConsumption: "N/A",
  }),
  methods: {
    reset: function () {
      this.form.hensBatchReport.numXL = 0;
      this.form.hensBatchReport.numL = 0;
      this.form.hensBatchReport.numM = 0;
      this.form.hensBatchReport.numS = 0;
      this.form.hensBatchReport.numXS = 0;
      this.form.hensBatchReport.dirties = 0;
      this.form.hensBatchReport.brokens = 0;
      this.form.hensBatchReport.deaths = 0;
      this.form.hensBatchReport.departures = 0;
      this.form.hensBatchReport.maxTemperature = 0;
      this.form.hensBatchReport.minTemperature = 0;
      this.form.hensBatchReport.waterReading = "";
      this.form.hensBatchReport.poultryMashAdditionQuantity = null;
      this.form.hensBatchReport.poultryMashAdditionFeedTurn = null;
      this.form.hensBatchReport.poultryMashMaxFeedTurns = null;
      this.switchPoultryMashConsumption = false;
      this.form.hensBatchReport.comments = null;
      this.estimatedWaterConsumption = "N/A";

      this.$nextTick(this.$refs.dateText.focus);
    },
    parseDateText() {
      var moment = this.$moment.utc(
        this.dateText,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.date = moment.format("YYYY-MM-DD");
        this.dateText = moment.format("DD/MM/YYYY");
        this.form.hensBatchReport.reportTimestamp = moment.format("x");
        this.menuDatePicker = false;
      } else {
        this.$nextTick(this.$refs.dateText.focus);
        this.menuDatePicker = false;
      }
    },
    datePickedOnCalendar() {
      var moment = this.$moment.utc(this.date, "YYYY-MM-DD", true);
      this.dateText = moment.format("DD/MM/YYYY");
      this.form.hensBatchReport.reportTimestamp = moment.format("x");
      this.menuDatePicker = false;
    },
    async compareWaterReadingWithLastReport() {
      if (
        this.form.hensBatchReport.waterReading &&
        this.form.hensBatchReport.reportTimestamp
      ) {
        var lastReportWithWaterReading = await HensBatchReportService.getLastWithWaterReading(
          this.hensBatch.id,
          this.form.hensBatchReport.reportTimestamp
        );
        if (lastReportWithWaterReading?.waterReading) {
          this.estimatedWaterConsumption =
            parseInt(this.form.hensBatchReport.waterReading) -
            lastReportWithWaterReading.waterReading;
        } else {
          this.estimatedWaterConsumption = "N/A";
        }
      }
    },
  },
};
</script>