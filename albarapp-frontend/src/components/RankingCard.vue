<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-card :loading="!rankingReady" class="mt-2">
        <v-card-title class="mb-4">
          <v-icon large class="mr-2">mdi-chart-bar</v-icon>Ranking clientes
        </v-card-title>
        <v-card-text
          v-for="(row, index) in ranking"
          :key="index"
        >{{ row.customerAlias }}: {{ currencyFormatted(row.invoiceTotal) }}</v-card-text>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row
        class="mb-2"
        justify="center"
      >Error al obtener las estadísticas, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="getRanking()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
  </v-container>
</template>

<script>
import HttpClient from "@/services/HttpClient.js";

export default {
  name: "RankingCard",
  data: () => {
    return {
      ranking: [],
      errorLoading: false,
      rankingReady: false
    };
  },
  created() {
    this.getRanking();
  },
  methods: {
    getRanking() {
      this.errorLoading = false;
      HttpClient.get("api/statistics/ranking")
        .then(response => {
          this.ranking = response.data;
          this.rankingReady = true;
        })
        .catch(() => {
          this.errorLoading = true;
        });

      HttpClient.get("api/statistics/monthlyEvolution")
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
