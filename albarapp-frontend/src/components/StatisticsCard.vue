<template>
    <v-card v-if="statisticsReady" class="mt-2">
      <v-card-title class="mb-4">
        <v-icon large class="mr-2">mdi-chart-bar</v-icon>Estadísticas Albarapp
      </v-card-title>
      <v-card-text
        v-for="(statistic, index) in statistics"
        :key="index"
      >{{ statistic.name }}: {{ statistic.value }}</v-card-text>
    </v-card>
</template>

<script>
import HttpClient from "@/services/HttpClient.js";

export default {
  name: "StatisticsCard",
  data: () => {
    return {
      statistics: [],
      statisticsReady: false
    };
  },
  created() {
    if (this.token) {
      this.getStatistics();
    }
  },
  methods: {
    getStatistics: function() {
      HttpClient.get("api/statistics")
        .then(response => {
          this.statistics = response.data;
          this.statisticsReady = true;
        })
        .catch(() => {
          alert("Ha ocurrido un error obteniendo estadísticas");
        });
    }
  }
};
</script>