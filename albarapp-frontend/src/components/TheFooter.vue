<template>
  <v-footer app dark color="grey darken-3">
    <div class="flex-grow-1"></div>
    <span class="text-right">&copy; {{$moment().year()}}</span>
  </v-footer>
</template>

<script>
export default {
  name: "WebFooter",
};
</script>