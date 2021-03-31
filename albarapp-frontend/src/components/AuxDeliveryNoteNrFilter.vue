<template>
  <v-container>
    <v-expansion-panels :dark="!!filter.auxDeliveryNoteNr">
      <v-expansion-panel>
        <v-expansion-panel-header>
          <span class="subtitle-1 font-italic font-weight-light"
            >Filtrar por pedido</span
          >
        </v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-text-field
            data-vv-delay="1000"
            :value="filter.auxDeliveryNoteNr"
            @input="debounceChangeModel"
          />
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-container>
</template>

<script>
import _debounce from "lodash/debounce";

export default {
  name: "ProductFilter",
  props: {
    filter: {
      auxDeliveryNoteNr: String,
    },
  },
  data: () => ({
    totalProducts: [],
  }),
  created() {
    let vm = this;
    this.debounceChangeModel = _debounce(function (v) {
      vm.filter.auxDeliveryNoteNr = v.trim();
    }, 1000);
  },
};
</script>