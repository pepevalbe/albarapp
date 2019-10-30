<template>
  <div>
    <WebNavDrawer />
    <v-app-bar color="darken-3" dark app :clipped-left="$vuetify.breakpoint.mdAndUp" fixed>
      <v-toolbar-title class="headline text-uppercase">
        <span>AlbarApp - </span>
        <span class="font-weight-light">{{currentState}}</span>
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
      dialog: true
    };
  }
};
</script>