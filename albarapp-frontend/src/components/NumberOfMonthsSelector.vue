<template>
  <v-container>
    <v-radio-group v-model="months.numberOfMonths">
      <v-radio
        color="black"
        v-for="option in options"
        :key="option.label"
        :label="option.label"
        :value="option.value"
      ></v-radio>
    </v-radio-group>
  </v-container>
</template>

<script>
export default {
  props: {
    months: {
      numberOfMonths: Number,
    },
  },
  data: () => ({
    options: [
      { label: "6 meses", value: 6 },
      { label: "1 año", value: 12 },
      { label: "2 años", value: 24 },
      { label: "5 años", value: 60 },
    ],
  }),
};
</script>
