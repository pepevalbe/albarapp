<template>
  <div>
    <WebNavDrawer v-bind:drawer="drawer" />
    <v-app-bar color="darken-3" dark app :clipped-left="$vuetify.breakpoint.mdAndUp" fixed>
      <v-app-bar-nav-icon @click="toggleNavDrawer"></v-app-bar-nav-icon>
      <v-toolbar-title class="headline text-uppercase">
        <a href="/">Albarapp</a>
        <span class="font-weight-light"> - {{currentState}}</span>
      </v-toolbar-title>
    </v-app-bar>
    <v-dialog v-if="!token" v-model="dialog" persistent max-width="600px">
      <v-card>
        <LoginForm />
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import WebNavDrawer from "@/components/TheNavDrawer";
import LoginForm from "@/components/LoginForm";

export default {
  name: "WebHeader",
  components: {
    WebNavDrawer,
    LoginForm
  },
  props: {
    currentState: String
  },
  data: () => {
    return {
      dialog: true,
      drawer: {
        value: true
      }
    };
  },
  methods: {
    toggleNavDrawer() {
      this.drawer.value = !this.drawer.value;
    }
  }
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