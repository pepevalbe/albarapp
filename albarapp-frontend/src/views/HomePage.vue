<template>
  <div>
    <StatisticsCard v-if="token && parsedToken.roles.includes('ADMIN')"></StatisticsCard>
    <div v-if="token && parsedToken.roles.includes('ADMIN')"></div>
    <TriviaCard></TriviaCard>
  </div>
</template>

<script>
import StatisticsCard from "@/components/StatisticsCard";
import TriviaCard from "@/components/TriviaCard";

export default {
  name: "Home",
  components: {
    StatisticsCard,
    TriviaCard
  }
};
</script>