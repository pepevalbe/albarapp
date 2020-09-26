<template>
  <v-container>
    <v-form v-model="valid" @submit.prevent>
      <v-row class="ml-2 mr-2" align="center" justify="center">
        <v-col>
          <v-text-field
            :rules="fromRules"
            class="mr-5"
            v-model="idFrom"
            type="number"
            autocomplete="off"
            label="Desde"
          ></v-text-field>
        </v-col>
        <v-col>
          <v-text-field
            :rules="toRules"
            class="mr-5"
            v-model="idTo"
            type="number"
            autocomplete="off"
            label="Hasta"
          ></v-text-field>
        </v-col>
        <v-col>
          <v-btn type="submit" :disabled="!valid" @click="filter">
            <v-icon>mdi-magnify</v-icon>
          </v-btn>
        </v-col>
      </v-row>
    </v-form>
  </v-container>
</template>

<script>
export default {
  name: "InvoiceIdFilter",
  props: {
    invoiceFilter: {
      idFrom: Number,
      idTo: Number,
    },
  },
  data: () => {
    return {
      idFrom: 0,
      idTo: 0,
      valid: true,
      fromRules: [(v) => !!v || "Dato obligatorio"],
      toRules: [(v) => !!v || "Dato obligatorio"],
    };
  },
  created() {
    this.idFrom = this.invoiceFilter.idFrom;
    this.idTo = this.invoiceFilter.idTo;
  },
  methods: {
    filter() {
      this.invoiceFilter.idFrom = this.idFrom;
      this.invoiceFilter.idTo = this.idTo;
    },
  },
};
</script>