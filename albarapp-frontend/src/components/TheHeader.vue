<template>
  <div>
    <v-app-bar>
      <v-toolbar-title class="headline text-uppercase">
        <span>AlbarApp - </span>
        <span class="font-weight-light">{{currentState}}</span>
      </v-toolbar-title>
    </v-app-bar>
  </div>
</template>

<script>
export default {
  name: "WebHeader",
  props: {
    currentState: String
  }
};
</script>