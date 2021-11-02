<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-card>
        <v-card-title class="mb-4">
          <v-icon large class="mr-2">mdi-chart-bar</v-icon>Evolución mensual
        </v-card-title>
        <highcharts
          v-if="chartOptions.series[0].data.length"
          class="chart"
          :options="chartOptions"
          :updateArgs="updateArgs"
        ></highcharts>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener las estadísticas, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="getMonthlyEvolution()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
  </v-container>
</template>

<script>
import StatisticService from "@/services/StatisticService.js";

export default {
  name: "MonthlyEvolutionCard",
  data: () => {
    return {
      errorLoading: false,
      rankingReady: false,
      filter: {
        products: {
          productCodes: [],
        },
      },
      chartOptions: {
        chart: {
          type: "spline",
        },
        title: {
          text: "",
        },
        plotOptions: {
          dataLabels: {
            enabled: true,
          },
        },
        series: [
          {
            name: "Facturación",
            data: [],
            color: "#000000",
          },
        ],
        yAxis: {
          title: {
            text: "Total ( € )",
          },
        },
      },
      updateArgs: [true, true, { duration: 1000 }],
    };
  },
  created() {
    this.getMonthlyEvolution();
    this.$watch(
      "$store.getters.statisticsProductFilter",
      function() {
        this.getMonthlyEvolution();
      },
      { deep: true }
    );
    this.$watch(
      "$store.getters.statisticsNumberOfMonths",
      function() {
        this.getMonthlyEvolution();
      },
      { deep: true }
    );
  },
  methods: {
    getMonthlyEvolution() {
      this.errorLoading = false;
      StatisticService.getMonthlyEvolution(
        this.$store.getters.statisticsProductFilter,
        this.$store.getters.statisticsNumberOfMonths
      )
        .then((response) => {
          this.chartOptions.series[0].data = response.map(
            (o) => o.invoiceTotal
          );
          this.chartOptions.xAxis = {
            categories: response.map(
              (o) =>
                o.monthName.charAt(0).toUpperCase() + o.monthName.substring(1)
            ),
          };
          this.autoDraw = true;
          this.rankingReady = true;
        })
        .catch(() => {
          this.errorLoading = true;
        });
    },
  },
};
</script>
