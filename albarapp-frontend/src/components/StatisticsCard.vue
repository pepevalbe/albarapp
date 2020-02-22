<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-card v-if="statisticsReady" class="mt-2">
        <v-card-title class="mb-4">
          <v-icon large class="mr-2">mdi-chart-bar</v-icon>Estadísticas Albarapp
        </v-card-title>
        <v-card-text
          v-for="(statistic, index) in statistics"
          :key="index"
        >{{ statistic.name }}: {{ statistic.value }}</v-card-text>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row
        class="mb-2"
        justify="center"
      >Error al obtener las estadísticas, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="getQuestion()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
  </v-container>
</template>

<script>
import HttpClient from "@/services/HttpClient.js";

export default {
  name: "StatisticsCard",
  data: () => {
    return {
      statistics: [],
      errorLoading: false,
      statisticsReady: false
    };
  },
  created() {
    this.getStatistics();
  },
  methods: {
    getStatistics: function() {
      this.errorLoading = false;
      HttpClient.get("api/statistics")
        .then(response => {
          this.statistics = response.data;
          this.statisticsReady = true;
        })
        .catch(() => {
          this.errorLoading = true;
        });
    }
  }
};
</script>