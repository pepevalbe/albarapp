<template>
  <v-container>
    <ProductFilter :products="filter.products" />
  </v-container>
</template>

<script>
import ProductFilter from "@/components/ProductFilter";

export default {
  name: "StatisticsFilter",
  components: {
    ProductFilter
  },
  data: () => {
    return {
      filter: {
        products: {
          productCodes: []
        }
      }
    };
  },
  created() {
    var vm = this;
    this.filter.products.productCodes = this.$store.getters.statisticsProductFilter;
    this.$watch(
      "filter",
      function() {
        this.$store.commit("filterStatistics", vm.filter.products.productCodes);
      },
      { deep: true }
    );
  },
  methods: {}
};
</script>