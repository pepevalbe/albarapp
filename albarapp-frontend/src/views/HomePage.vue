<template>
  <div>
    <v-row>
      <v-col v-if="$store.getters.authenticated && $store.getters.parsedToken.roles.includes('ADMIN')">
        <MonthlyEvolutionCard />
      </v-col>
    </v-row>
    <v-row>
      <v-col v-if="$store.getters.authenticated && $store.getters.parsedToken.roles.includes('ADMIN')">
        <StatisticsCard />
      </v-col>
      <v-col v-if="$store.getters.authenticated && $store.getters.parsedToken.roles.includes('ADMIN')">
        <RankingCard />
      </v-col>
      <v-col>
        <TriviaCard></TriviaCard>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import StatisticsCard from "@/components/StatisticsCard";
import RankingCard from "@/components/RankingCard";
import MonthlyEvolutionCard from "@/components/MonthlyEvolutionCard";
import TriviaCard from "@/components/TriviaCard";

export default {
  name: "Home",
  components: {
    StatisticsCard,
    RankingCard,
    MonthlyEvolutionCard,
    TriviaCard
  }
};
</script>