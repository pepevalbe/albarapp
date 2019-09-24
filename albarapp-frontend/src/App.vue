<template>
  <v-app>
    <v-content>
      <WebHeader v-bind:currentState="currentRouteName"/>
      <router-view class="pb-9"></router-view>
      <WebFooter/>
    </v-content>
  </v-app>
</template>

<script>
import WebHeader from "@/components/TheHeader";
import WebFooter from "@/components/TheFooter";

export default {
  name: 'App',
  components: {
    WebHeader,
    WebFooter
  },
  data: () => ({
    
  }),
  computed: {
    currentRouteName() {
        return this.$route.name;
    }
  }
};
</script>
