<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-card>
        <v-card-title class="mb-4">
          <v-icon large class="mr-2">mdi-chart-bar</v-icon>Evolución mensual
        </v-card-title>
        <highcharts
          class="chart"
          :options="chartOptions"
          :updateArgs="updateArgs"
        ></highcharts>
        <v-row class="mt-3">
          <v-autocomplete
            v-model="hensBatch"
            label="Seleccione un lote"
            :items="hensBatches"
            item-text="name"
            return-object
            clearable
            autocomplete="off"
            no-data-text="Sin coincidencias"
            class="mr-5 ml-10"
          />
          <v-combobox
            v-model="chartType"
            label="Seleccione un atributo"
            :items="chartTypes"
            item-text="name"
            return-object
            no-data-text="Sin coincidencias"
            class="mr-5 ml-5"
          ></v-combobox>
          <v-btn
            class="mr-10 mt-3"
            :disabled="!hensBatch || !chartType"
            @click="drawNewChart()"
            >Añadir</v-btn
          >
        </v-row>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener las estadísticas, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="refreshPage()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
  </v-container>
</template>

<script>
import HensBatchService from "@/services/production/HensBatchService.js";
import HensBatchReportService from "@/services/production/HensBatchReportService.js";

export default {
  name: "HensBatchReportChart",
  data: () => {
    return {
      chartOptions: {
        chart: {
          type: "spline",
          height: "600px",
        },
        title: {
          text: "",
        },
        plotOptions: {
          dataLabels: {
            enabled: true,
          },
        },
        series: [],
        yAxis: [],
      },
      updateArgs: [true, true, { duration: 1000 }],
      hensBatches: [],
      hensBatch: null,
      chartTypes: [
        {
          key: 1,
          name: "Porcentaje de puesta",
          yAxis: {
            title: {
              text: "Porcentaje ( % )",
            },
          },
        },
        {
          key: 2,
          name: "Consumo de pienso",
          yAxis: {
            title: {
              text: "Consumo de pienso / ave ( g )",
            },
          },
        },
        {
          key: 3,
          name: "Consumo de agua",
          yAxis: {
            title: {
              text: "Consumo de agua / ave ( ml )",
            },
          },
        },
      ],
      chartType: null,
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0,
      },
    };
  },
  async created() {
    await this.loadHensBatches();
  },
  methods: {
    async loadHensBatches() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatches = await HensBatchService.getAll();
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    refreshPage() {
      this.$router.go(0);
    },
    async drawNewChart() {
      var hensBatchReports = await HensBatchReportService.getByHensBatchId(
        this.hensBatch.id
      );
      hensBatchReports.forEach((element) => {
        var current = this.$moment.utc(element.reportTimestamp, "x", true);
        var born = this.$moment.utc(this.hensBatch.birthTimestamp, "x", true);
        element.week = current.diff(born, "weeks") + 1;
      });
    },
  },
};
</script>