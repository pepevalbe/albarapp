<template>
  <div>
    <v-app-bar>
      <v-toolbar-title class="headline text-uppercase">
        <span>AlbarApp - </span>
        <span class="font-weight-light">{{currentState}}</span>
      </v-toolbar-title>
      <v-layout text-right>
        <v-flex xs12>
          <v-btn v-if="this.isLoggedIn" class="mr-4" @click="logout()">Salir</v-btn>
        </v-flex>
      </v-layout>
    </v-app-bar>
  </div>
</template>

<script>
export default {
  name: "WebHeader",
  props: {
    currentState: String
  },
  data: () => {
    return {
      isLoggedIn: false
    };
  },
  beforeMount() {
    this.isLoggedIn = localStorage.getItem("token") !== null;
  },
  beforeUpdate() {
    this.isLoggedIn = localStorage.getItem("token") !== null;
  },
  methods: {
    logout() {
      localStorage.removeItem("token");
      this.isLoggedIn = false;
    }
  }
};
</script>