<template>
  <v-container>
    <div v-if="!errorLoading">
      <div v-if="!calcsDone">
        <v-progress-linear indeterminate></v-progress-linear>
      </div>
      <div v-if="calcsDone">
        <v-row>
          <v-col>
            <h3>Lote: {{ hensBatch.name }}</h3>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <WeekFilter
              v-if="hensBatch"
              :filter="filter"
              :birthTimestamp="hensBatch.birthTimestamp"
            />
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-card min-width="350px">
              <v-card-title>Consumo</v-card-title>
              <v-card-text>
                <div>
                  Consumo medio de pienso día-ave: {{ avgMashConsumption }} g
                </div>
                <div>
                  Consumo medio de agua día-ave: {{ avgWaterConsumption }} ml
                </div>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col>
            <v-card min-width="350px">
              <v-card-title>Puesta</v-card-title>
              <v-card-text>
                <div>Pico de puesta: {{ peakPercentage }} %</div>
                <div>
                  Huevos totales:
                  {{
                    totalEggs.toLocaleString("es-ES", {
                      maximumFractionDigits: 0,
                    })
                  }}
                </div>
                <div>
                  Huevos útiles totales:
                  {{
                    totalUsefulEggs.toLocaleString("es-ES", {
                      maximumFractionDigits: 0,
                    })
                  }}
                </div>
                <div>Huevos por ave alojada: {{ totalEggsByAnimalBorn }}</div>
                <div>
                  Huevos útiles por ave alojada:
                  {{ totalUsefulEggsByAnimalBorn }}
                </div>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col>
            <v-card min-width="350px">
              <v-card-title>Gastos</v-card-title>
              <v-card-text>
                <div>Gasto total actual: {{ totalCost }} €</div>
                <div>Costo del huevo en destino: {{ costByEgg }} €</div>
                <div>
                  Costo del huevo en nave: {{ costByEggNoDistribution }} €
                </div>
                <div>
                  Costo prorrateado del huevo en destino:
                  {{ totalEstimatedCostByEgg }} €
                </div>
                <div>
                  Costo prorrateado del huevo en nave:
                  {{ totalEstimatedCostByEggNoDistribution }} €
                </div>
              </v-card-text>
            </v-card>
          </v-col>
          <v-col>
            <v-card min-width="350px">
              <v-card-title>Distribución de masa</v-card-title>
              <v-card-text>
                <div>Masa promedio del huevo: {{ averageEggMass }} g</div>

                <highcharts
                  class="chart"
                  :options="chartOptions"
                  :updateArgs="updateArgs"
                ></highcharts>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
        <h5></h5>
      </div>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center">
        Error al cargar el lote y/o sus reportes, por favor vuelva a cargar.
      </v-row>
      <v-row justify="center">
        <v-btn @click="loadPageDate()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-row justify="center">
      <v-col align-self="center" cols="1">
        <v-btn class="mt-2" @click="$router.back()">Volver</v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import WeekFilter from "@/components/production/WeekFilter";
import HensBatchService from "@/services/production/HensBatchService.js";
import HensBatchReportService from "@/services/production/HensBatchReportService.js";
import HensBatchExpenseService from "@/services/production/HensBatchExpenseService.js";

let ESTIMATED_HENS_BATCH_TIME_IN_WEEKS = 70

export default {
  name: "HensBatchStatistics",
  components: {
    WeekFilter,
  },
  props: {
    hensBatchId: String,
  },
  data: () => {
    return {
      calcsDone: false,
      hensBatch: {
        name: "",
      },
      avgMashConsumption: 0,
      avgWaterConsumption: 0,
      peakPercentage: 0,
      totalEggs: 0,
      totalUsefulEggs: 0,
      totalEggsByAnimalBorn: 0,
      totalUsefulEggsByAnimalBorn: 0,
      averageEggMass: 0,
      totalCost: 0,
      costByEgg: 0,
      costByEggNoDistribution: 0,
      totalEstimatedCostByEgg: 0,
      totalEstimatedCostByEggNoDistribution: 0,
      hensBatchReports: null,
      hensBatchExpenses: null,
      filter: {
        weekFrom: "",
        weekTo: "",
      },
      chartOptions: {
        chart: {
          type: "pie",
          height: "200px",
        },
        title: {
          text: "",
        },
        plotOptions: {
          dataLabels: {
            enabled: true,
          },
        },
        series: [],
      },
      updateArgs: [true, true, { duration: 1000 }],
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0,
      },
    };
  },
  async created() {
    await this.loadURLParams();
    this.loadPageDate();
    this.$watch("filter", this.loadPageDate, { deep: true });
  },
  methods: {
    async loadPageDate() {
      await this.loadHensBatch();
      await this.loadHensBatchReports();
      await this.loadHensBatchExpenses();
      this.calcStatistics();
      this.updateURL();
    },
    async loadHensBatch() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatch = await HensBatchService.get(this.hensBatchId);
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async loadHensBatchReports() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatchReports = await HensBatchReportService.getByHensBatchId(
          this.hensBatchId
        );
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async loadHensBatchExpenses() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatchExpenses = await HensBatchExpenseService.getByHensBatchId(
          this.hensBatchId
        );
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    loadURLParams() {
      if (this.$route.query) {
        if (this.$route.query.weekFrom)
          this.filter.weekFrom = this.$route.query.weekFrom;
        if (this.$route.query.weekTo)
          this.filter.weekTo = this.$route.query.weekTo;
      }
    },
    updateURL() {
      var query = {};
      if (this.filter.weekFrom) query.weekFrom = this.filter.weekFrom;
      if (this.filter.weekTo) query.weekTo = this.filter.weekTo;
      this.$router
        .push({
          path: this.$route.path,
          query: query,
        })
        .catch(() => {});
    },
    calcStatistics() {
      let born = this.$moment.utc(this.hensBatch.birthTimestamp, "x", true);
      this.hensBatchReports.forEach((element) => {
        let current = this.$moment.utc(element.reportTimestamp, "x", true);
        element.week = current.diff(born, "weeks") + 1;
      });
      this.hensBatchExpenses.forEach((element) => {
        let current = this.$moment.utc(element.expenseTimestamp, "x", true);
        element.week = current.diff(born, "weeks") + 1;
      });

      if (this.filter.weekFrom) {
        this.hensBatchReports = this.hensBatchReports.filter(
          (element) => element.week >= this.filter.weekFrom
        );
        this.hensBatchExpenses = this.hensBatchExpenses.filter(
          (element) =>
            element.week >= this.filter.weekFrom || !element.recurrent
        );
      }
      if (this.filter.weekTo) {
        this.hensBatchReports = this.hensBatchReports.filter(
          (element) => element.week <= this.filter.weekTo
        );
        this.hensBatchExpenses = this.hensBatchExpenses.filter(
          (element) => element.week <= this.filter.weekTo || !element.recurrent
        );
      }

      let reportsByWeek = this.hensBatchReports
        .reduce((accumulator, element) => {
          if (accumulator[element.week]) {
            accumulator[element.week].numXL += element.numXL;
            accumulator[element.week].numL += element.numL;
            accumulator[element.week].numM += element.numM;
            accumulator[element.week].numS += element.numS;
            accumulator[element.week].numXS += element.numXS;
            accumulator[element.week].dirties += element.dirties;
            accumulator[element.week].brokens += element.brokens;
            accumulator[element.week].deaths += element.deaths;
            accumulator[element.week].maxTemperature += element.maxTemperature;
            accumulator[element.week].minTemperature += element.minTemperature;
            accumulator[element.week].hensPoultryMashConsumption +=
              element.hensPoultryMashConsumption;
            accumulator[element.week].hensWaterConsumption +=
              element.hensWaterConsumption;
            accumulator[element.week].numHens += element.numHens;
            accumulator[element.week].numDays += 1;
          } else {
            accumulator[element.week] = JSON.parse(JSON.stringify(element));
            accumulator[element.week].numDays = 1;
          }
          return accumulator;
        }, [])
        .filter((element) => element);

      this.avgMashConsumption = Math.round(
        (this.hensBatchReports.reduce(
          (acc, element) => (acc += element.hensPoultryMashConsumption),
          0
        ) /
          this.hensBatchReports.length) *
          1000
      );
      this.avgWaterConsumption = Math.round(
        (this.hensBatchReports.reduce(
          (acc, element) => (acc += element.hensWaterConsumption),
          0
        ) /
          this.hensBatchReports.length) *
          1000
      );

      let totalAnimals = this.hensBatch.animalQuantity;
      reportsByWeek.forEach((element, index, array) => {
        element.hensWaterConsumption /= element.numDays;
        element.hensPoultryMashConsumption /= element.numDays;
        element.numHens = element.numHens / element.numDays;
        element.numXL /= element.numDays;
        element.numL /= element.numDays;
        element.numM /= element.numDays;
        element.numS /= element.numDays;
        element.numXS /= element.numDays;
        element.dirties /= element.numDays;
        element.brokens /= element.numDays;
        element.maxTemperature /= element.numDays;
        element.minTemperature /= element.numDays;
        element.totalEggs =
          element.numXL +
          element.numL +
          element.numM +
          element.numS +
          element.numXS +
          element.dirties +
          element.brokens;
        element.percentage = (element.totalEggs / element.numHens) * 100;
        element.mortality = (element.deaths * 100) / totalAnimals;
        if (index > 0)
          element.accumulatedMoratlity =
            array[index - 1].accumulatedMoratlity + element.mortality;
        else element.accumulatedMoratlity = element.mortality;
      });

      this.peakPercentage = reportsByWeek
        .reduce((acc, el) => Math.max(acc, el.percentage), 0)
        .toFixed(2);

      let totalXL = this.hensBatchReports.reduce(
        (accumulator, element) => (accumulator += element.numXL),
        0
      );
      let totalL = this.hensBatchReports.reduce(
        (accumulator, element) => (accumulator += element.numL),
        0
      );
      let totalM = this.hensBatchReports.reduce(
        (accumulator, element) => (accumulator += element.numM),
        0
      );
      let totalS = this.hensBatchReports.reduce(
        (accumulator, element) => (accumulator += element.numS),
        0
      );
      let totalXS = this.hensBatchReports.reduce(
        (accumulator, element) => (accumulator += element.numXS),
        0
      );

      let totalDirties = this.hensBatchReports.reduce(
        (accumulator, element) => (accumulator += element.dirties),
        0
      );

      let totalBrokens = this.hensBatchReports.reduce(
        (accumulator, element) => (accumulator += element.brokens),
        0
      );

      this.totalUsefulEggs = totalXL + totalL + totalM + totalS + totalXS;
      this.totalEggs = this.totalUsefulEggs + totalDirties + totalBrokens;

      this.totalEggsByAnimalBorn = (this.totalEggs / totalAnimals).toFixed(0);
      this.totalUsefulEggsByAnimalBorn = (
        this.totalUsefulEggs / totalAnimals
      ).toFixed(0);

      this.averageEggMass = (
        (totalXL * 78 +
          totalL * 68 +
          totalM * 58 +
          totalS * 48 +
          totalXS * 38) /
        this.totalEggs
      ).toFixed(0);

      let shareXL = (totalXL / this.totalEggs) * 100;
      let shareL = (totalL / this.totalEggs) * 100;
      let shareM = (totalM / this.totalEggs) * 100;
      let shareS = (totalS / this.totalEggs) * 100;
      let shareXS = (totalXS / this.totalEggs) * 100;

      this.chartOptions.series.splice(0, this.chartOptions.series.length);
      this.chartOptions.series.push({
        name: "Tamaño del huevo",
        data: [
          ["XL", shareXL],
          ["L", shareL],
          ["M", shareM],
          ["S", shareS],
          ["XS", shareXS],
        ],
        tooltip: {
          valueDecimals: 2,
          valueSuffix: " %",
        },
      });

      let totalCost = this.hensBatchExpenses.reduce(
        (accumulator, element) => (accumulator += element.value),
        0
      );
      this.totalCost = totalCost.toFixed(2)
      this.costByEgg = (this.totalCost / this.totalEggs).toFixed(3);

      let totalCostNoDistribution = this.hensBatchExpenses
        .filter((el) => !el.distribution)
        .reduce((acc, ele) => (acc += ele.value), 0);

      this.costByEggNoDistribution = (
        totalCostNoDistribution / this.totalEggs
      ).toFixed(3);

      let uniqueCost = this.hensBatchExpenses
        .filter((el) => !el.recurrent)
        .reduce((acc, ele) => (acc += ele.value), 0);
      let estimatedUniqueCostPerWeek = uniqueCost / ESTIMATED_HENS_BATCH_TIME_IN_WEEKS;
      let totalWeeks =
        this.hensBatchReports[this.hensBatchReports.length - 1].week -
        this.hensBatchReports[0].week +
        1;
      let estimatedUniqueCost = estimatedUniqueCostPerWeek * totalWeeks;
      let variableCost = this.hensBatchExpenses
        .filter((el) => el.recurrent)
        .reduce((acc, ele) => (acc += ele.value), 0);
      this.totalEstimatedCostByEgg = (
        (variableCost + estimatedUniqueCost) /
        this.totalEggs
      ).toFixed(3);

      let uniqueCostNoDistribution = this.hensBatchExpenses
        .filter((el) => !el.recurrent && !el.distribution)
        .reduce((acc, ele) => (acc += ele.value), 0);
      let estimatedUniqueCostPerWeekNoDistribution =
        uniqueCostNoDistribution / ESTIMATED_HENS_BATCH_TIME_IN_WEEKS;
      let estimatedUniqueCostNoDistribution =
        estimatedUniqueCostPerWeekNoDistribution * totalWeeks;
      let variableCostNoDistribution = this.hensBatchExpenses
        .filter((el) => el.recurrent && !el.distribution)
        .reduce((acc, ele) => (acc += ele.value), 0);
      this.totalEstimatedCostByEggNoDistribution = (
        (variableCostNoDistribution + estimatedUniqueCostNoDistribution) /
        this.totalEggs
      ).toFixed(3);

      this.calcsDone = true;
    },
  },
};
</script>