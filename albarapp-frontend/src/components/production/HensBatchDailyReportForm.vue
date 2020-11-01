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
          v-model="form.hensBatchDailyReport.xl"
          type="number"
          :rules="xlRules"
          autocomplete="off"
          label="XL *"
          required
          autofocus
          @focus="$event.target.select()"
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchDailyReport.l"
          type="number"
          :rules="lRules"
          autocomplete="off"
          label="L *"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchDailyReport.m"
          type="number"
          :rules="mRules"
          autocomplete="off"
          label="M *"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchDailyReport.s"
          type="number"
          :rules="sRules"
          autocomplete="off"
          label="S *"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchDailyReport.xs"
          type="number"
          :rules="xsRules"
          autocomplete="off"
          label="XS *"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchDailyReport.dirties"
          type="number"
          :rules="dirtiesRules"
          autocomplete="off"
          label="Sucios *"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchDailyReport.brokens"
          type="number"
          :rules="brokensRules"
          autocomplete="off"
          label="Rotos *"
          required
        ></v-text-field>
      </v-row>
      <v-subheader>Gallinas</v-subheader>
      <v-divider></v-divider>
      <v-row class="mr-6 ml-6">
        <v-text-field
          v-model="form.hensBatchDailyReport.deaths"
          type="number"
          :rules="deathsRules"
          autocomplete="off"
          label="Muertas *"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchDailyReport.departures"
          type="number"
          :rules="departuresRules"
          autocomplete="off"
          label="Salidas *"
          required
        ></v-text-field>
      </v-row>
      <v-subheader>Temperatura</v-subheader>
      <v-divider></v-divider>
      <v-row class="mr-6 ml-6">
        <v-text-field
          v-model="form.hensBatchDailyReport.maxTemperature"
          type="number"
          :rules="maxTempRules"
          autocomplete="off"
          label="Máxima *"
          required
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchDailyReport.minTemperature"
          type="number"
          :rules="minTempRules"
          autocomplete="off"
          label="Mínima *"
          required
        ></v-text-field>
      </v-row>
      <v-subheader>Consumos</v-subheader>
      <v-divider></v-divider>
      <v-row class="mr-6 ml-6">
        <v-text-field
          v-model="form.hensBatchDailyReport.waterConsumption"
          type="number"
          autocomplete="off"
          label="Agua en litros"
        ></v-text-field>
        <v-text-field
          v-model="form.hensBatchDailyReport.poultryMashConsumption"
          type="number"
          autocomplete="off"
          label="Pienso en kilogramos"
        ></v-text-field>
      </v-row>
      <v-subheader>Observaciones</v-subheader>
      <v-divider></v-divider>
      <v-row class="mr-6 ml-6">
        <v-text-field
          v-model="form.hensBatchDailyReport.comments"
          autocomplete="off"
          label="Observaciones"
        ></v-text-field>
      </v-row>
    </v-form>
  </v-container>
</template>
        
<script>
export default {
  name: "HensBatchDailyReportForm",
  props: {
    form: {
      valid: Boolean,
      hensBatchDailyReport: {
        name: String,
        race: String,
        animalQuantity: Number,
        birthDateTimestamp: Number,
      },
    },
    hensBatch: {
      name: String,
      race: String,
      animalQuantity: Number,
      birthDateTimestamp: Number,
    },
  },
  created() {
    if (this.form?.hensBatchDailyReport?.dateTimestamp) {
      let moment = this.$moment.utc(
        this.form.hensBatchDailyReport.dateTimestamp,
        "x",
        true
      );
      this.date = moment.format("YYYY-MM-DD");
      this.dateText = moment.format("DD/MM/YYYY");
    } else {
      let moment = this.$moment.utc();
      this.date = moment.format("YYYY-MM-DD");
      this.dateText = moment.format("DD/MM/YYYY");
      this.form.hensBatchDailyReport.dateTimestamp = moment.format("x");
    }
  },
  data: () => ({
    xlRules: [(v) => !!v || "La cantidad de huevos XL es obligatoria"],
    lRules: [(v) => !!v || "La cantidad de huevos L es obligatoria"],
    mRules: [(v) => !!v || "La cantidad de huevos M es obligatoria"],
    sRules: [(v) => !!v || "La cantidad de huevos S es obligatoria"],
    xsRules: [(v) => !!v || "La cantidad de huevos XS es obligatoria"],
    dirtiesRules: [(v) => !!v || "La cantidad de huevos sucios es obligatoria"],
    brokensRules: [(v) => !!v || "La cantidad de huevos rotos es obligatoria"],
    deathsRules: [
      (v) => !!v || "La cantidad de gallinas muertas es obligatoria",
    ],
    departuresRules: [
      (v) => !!v || "La cantidad de gallinas dadas de baja es obligatoria",
    ],
    maxTempRules: [(v) => !!v || "La temperatura máxima es obligatoria"],
    minTempRules: [(v) => !!v || "La temperatura mínima es obligatoria"],
    animalQuantityRules: [
      (v) => !!v || "La cantidad de animales es obligatoria",
      (v) => (v && v >= 0) || "La cantidad de animales debe ser mayor a 0",
    ],
    date: "",
    dateText: "",
    menuDatePicker: false,
  }),
  methods: {
    reset: function () {
      this.$refs.form.reset();
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
        this.form.hensBatchDailyReport.dateTimestamp = moment.format("x");
      } else {
        this.$nextTick(this.$refs.dateText.focus);
      }
    },
    datePickedOnCalendar() {
      var moment = this.$moment.utc(this.date, "YYYY-MM-DD", true);
      this.dateText = moment.format("DD/MM/YYYY");
      this.form.hensBatchDailyReport.dateTimestamp = moment.format("x");
      this.menuDatePicker = false;
    },
  },
};
</script>