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
          line-width="1"
        ></v-sparkline>
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
import HttpClient from "@/services/HttpClient.js";

export default {
  name: "MonthlyEvolutionCard",
  data: () => {
    return {
      monthlyEvolutionLabels: [],
      monthlyEvolutionValues: [],
      errorLoading: false,
      rankingReady: false
    };
  },
  created() {
    this.getMonthlyEvolution();
  },
  methods: {
    getMonthlyEvolution() {
      this.errorLoading = false;
      HttpClient.get("api/statistics/monthlyEvolution")
        .then(response => {
          this.monthlyEvolutionLabels = response.data.map(o => o.monthName);
          this.monthlyEvolutionValues = response.data.map(o => o.invoiceTotal);
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
