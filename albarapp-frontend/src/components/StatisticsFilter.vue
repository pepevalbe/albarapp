<template>
  <v-container>
    <v-row>
      <v-col>
        <ProductFilter :products="filter.products" />
      </v-col>
      <v-col>
        <NumberOfMonthsSelector :months="filter.months" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import ProductFilter from "@/components/ProductFilter";
import NumberOfMonthsSelector from "@/components/NumberOfMonthsSelector";

export default {
  name: "StatisticsFilter",
  components: {
    ProductFilter,
    NumberOfMonthsSelector,
  },
  data: () => {
    return {
      filter: {
        products: {
          productCodes: [],
        },
        months: {
          numberOfMonths: 12,
        },
      },
    };
  },
  created() {
    var vm = this;
    this.filter.products.productCodes = this.$store.getters.statisticsProductFilter;
    this.filter.months.numberOfMonths = this.$store.getters.statisticsNumberOfMonths;
    this.$watch(
      "filter",
      function() {
        this.$store.commit("filterStatistics", vm.filter);
      },
      { deep: true }
    );
  },
  methods: {},
};
</script>
