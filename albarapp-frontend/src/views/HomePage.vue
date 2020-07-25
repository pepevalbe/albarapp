<template>
  <div>
    <v-row class="justify-end mr-3 mt-2">
      <v-btn @click="openStatisticsFilter()">
        <v-icon>mdi-cogs</v-icon>
      </v-btn>
    </v-row>
    <v-row>
      <v-col
        v-if="$store.getters.isAdmin"
      >
        <MonthlyEvolutionCard />
      </v-col>
    </v-row>
    <v-row>
      <v-col
        v-if="$store.getters.isAdmin"
      >
        <StatisticsCard />
      </v-col>
      <v-col
        v-if="$store.getters.isAdmin"
      >
        <RankingCard />
      </v-col>
      <v-col>
        <TriviaCard></TriviaCard>
      </v-col>
    </v-row>
    <v-dialog v-model="dialog.show" max-width="600">
      <v-card>
        <StatisticsFilter />
        <v-card-actions class="justify-center">
          <v-btn class="mb-4" @click="closeStatisticsFilter()">Aceptar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import StatisticsCard from "@/components/StatisticsCard";
import RankingCard from "@/components/RankingCard";
import MonthlyEvolutionCard from "@/components/MonthlyEvolutionCard";
import TriviaCard from "@/components/TriviaCard";
import StatisticsFilter from "@/components/StatisticsFilter";

export default {
  name: "Home",
  components: {
    StatisticsCard,
    RankingCard,
    MonthlyEvolutionCard,
    TriviaCard,
    StatisticsFilter
  },
  data: () => {
    return {
      dialog: {
        show: false
      }
    };
  },
  methods: {
    openStatisticsFilter() {
      this.dialog.show = true;
    },
    closeStatisticsFilter() {
      this.dialog.show = false;
    }
  }
};
</script>