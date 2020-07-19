<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-card :loading="!rankingReady" class="mt-2">
        <v-card-title class="mb-4">
          <v-icon large class="mr-2">mdi-chart-bar</v-icon>Evolución mensual
        </v-card-title>
        <v-sparkline
          :value="monthlyEvolutionValues"
          :labels="monthlyEvolutionLabels"
          show-labels
          smooth
          padding="12"
          :auto-draw="autoDraw"
          label-size="3"
          line-width="1"
        >
          <template v-slot:label="item">
            <tspan dx="0" dy="-1.2em">{{ item.value.split("|")[0] }}</tspan>
            <tspan :dx="-item.value.length*0.68" dy="1.2em">{{ item.value.split("|")[1] }}</tspan>
          </template>
        </v-sparkline>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row
        class="mb-2"
        justify="center"
      >Error al obtener las estadísticas, por favor vuelva a cargar.</v-row>
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
      monthlyEvolutionLabels: [],
      monthlyEvolutionValues: [],
      autoDraw: false,
      errorLoading: false,
      rankingReady: false,
      filter: {
        products: {
          productCodes: []
        }
      }
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
  },
  methods: {
    getMonthlyEvolution() {
      this.errorLoading = false;
      StatisticService.getMonthlyEvolution(this.$store.getters.statisticsProductFilter)
        .then(response => {
          this.monthlyEvolutionLabels = response.map(
            o =>
              o.monthName.charAt(0).toUpperCase() +
              o.monthName.substring(1) +
              "|" +
              this.currencyFormatted(o.invoiceTotal)
          );
          this.monthlyEvolutionValues = response.map(o => o.invoiceTotal);
          this.autoDraw = true;
          this.rankingReady = true;
        })
        .catch(() => {
          this.errorLoading = true;
        });
    },
    currencyFormatted(value) {
      return value.toLocaleString("es-ES", {
        style: "currency",
        currency: "EUR"
      });
    }
  }
};
</script>
