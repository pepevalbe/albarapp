<template>
  <v-container>
    <v-expansion-panels :dark="!!filter.weekFrom || !!filter.weekTo">
      <v-expansion-panel>
        <v-expansion-panel-header>
          <span class="subtitle-1 font-italic font-weight-light"
            >Filtrar por semana [{{filter.weekFrom}}, {{filter.weekTo}}]</span
          >
        </v-expansion-panel-header>
        <v-expansion-panel-content>
          <v-row>
            <v-col>
              <v-text-field
                label="Desde"
                data-vv-delay="1000"
                :value="filter.weekFrom"
                @input="debounceChangeModelWeekFrom"
              />
            </v-col>
            <v-col>
              <v-text-field
                label="Hasta"
                data-vv-delay="1000"
                :value="filter.weekTo"
                @input="debounceChangeModelWeekTo"
              />
            </v-col>
          </v-row>
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
      weekFrom: String,
      weekTo: String,
    },
  },
  created() {
    let vm = this;
    this.debounceChangeModelWeekFrom = _debounce(function (v) {
      vm.filter.weekFrom = v.trim();
    }, 1000);
    this.debounceChangeModelWeekTo = _debounce(function (v) {
      vm.filter.weekTo = v.trim();
    }, 1000);
  },
};
</script>