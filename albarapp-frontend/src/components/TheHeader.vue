<template>
  <div>
    <WebNavDrawer v-if="$store.getters.authenticated" :drawer="drawer" />
    <v-app-bar color="darken-3" dark app :clipped-left="$vuetify.breakpoint.mdAndUp" fixed>
      <v-app-bar-nav-icon @click="toggleNavDrawer"></v-app-bar-nav-icon>
      <v-toolbar-title class="headline text-uppercase">
        <router-link to="/">Albarapp</router-link>
        <span class="font-weight-light"> - {{currentStateHeaderName}}</span>
      </v-toolbar-title>
    </v-app-bar>
  </div>
</template>
<script>
import WebNavDrawer from "@/components/TheNavDrawer";

export default {
  name: "WebHeader",
  components: {
    WebNavDrawer,
  },
  props: {
    currentStateHeaderName: String,
    currentState: String,
  },
  data: () => {
    return {
      dialog: true,
      drawer: {
        value: true,
      },
    };
  },
  methods: {
    toggleNavDrawer() {
      this.drawer.value = !this.drawer.value;
    },
  },
};
</script>
<style scoped>
a {
  color: white;
  text-decoration: none;
}
a:visited {
  color: white;
  text-decoration: none;
}
a:link {
  color: white;
  text-decoration: none;
}
</style>