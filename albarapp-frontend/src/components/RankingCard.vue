<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-card :loading="!rankingReady" class="mt-2">
        <v-card-title class="mb-4">
          <v-icon large class="mr-2">mdi-chart-bar</v-icon>Ranking clientes
        </v-card-title>
        <v-card-text v-for="(row, index) in ranking" :key="index"
          >{{ row.customerAlias }}:
          {{ currencyFormatted(row.invoiceTotal) }}</v-card-text
        >
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener las estadísticas, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="getRanking()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
  </v-container>
</template>

<script>
import StatisticService from "@/services/StatisticService.js";

export default {
  name: "RankingCard",
  data: () => {
    return {
      ranking: [],
      errorLoading: false,
      rankingReady: false,
    };
  },
  created() {
    this.getRanking();
    this.$watch(
      "$store.getters.statisticsProductFilter",
      function () {
        this.getRanking();
      },
      { deep: true }
    );
    this.$watch(
      "$store.getters.statisticsNumberOfMonths",
      function () {
        this.getRanking();
      },
      { deep: true }
    );
  },
  methods: {
    getRanking() {
      this.errorLoading = false;
      StatisticService.getRanking(this.$store.getters.statisticsProductFilter, this.$store.getters.statisticsNumberOfMonths)
        .then((response) => {
          this.ranking = response;
          this.rankingReady = true;
        })
        .catch(() => {
          this.errorLoading = true;
        });
    },
  },
};
</script>
