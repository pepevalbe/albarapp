<template>
  <v-expansion-panels :dark="!!filter.weekFrom || !!filter.weekTo">
    <v-expansion-panel>
      <v-expansion-panel-header>
        <v-row>
          <v-col>
            <span class="subtitle-1 font-italic font-weight-light">
              Filtrar por semana [{{ filter.weekFrom }}, {{ filter.weekTo }}]
            </span>
          </v-col>
          <v-spacer />
          <v-col class="text-right mr-3">
            <span class="subtitle-1 font-italic font-weight-light">
              {{ calcDateFromWeek() }}
            </span>
          </v-col>
        </v-row>
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
    birthTimestamp: Number,
  },
  data: () => {
    return {
      dateFrom: "",
      dateTo: "",
    };
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
  methods: {
    calcDateFromWeek() {
      let born = this.$moment.utc(this.birthTimestamp, "x", true);
      let dateFrom = "N/A";
      let dateTo = "N/A";
      if (this.filter.weekFrom) {
        dateFrom = born
          .add(this.filter.weekFrom - 1, "weeks")
          .format("DD/MM/yyyy");
      }
      if (this.filter.weekTo) {
        dateTo = born.add(this.filter.weekTo - 1, "weeks").format("DD/MM/yyyy");
      }
      return `De ${dateFrom} a ${dateTo}`;
    },
  },
};
</script>