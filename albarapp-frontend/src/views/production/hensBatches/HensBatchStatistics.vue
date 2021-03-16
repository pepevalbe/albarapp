<template>
  <v-container>
    <div v-if="!errorLoading">
      <div v-if="!calcsDone">
        <v-progress-linear indeterminate></v-progress-linear>
      </div>
      <div v-if="calcsDone">
        <h3>Lote: {{ hensBatch.name }}</h3>
        <h5>Consumo</h5>
        <div>Consumo medio de pienso día-ave: {{ avgMashConsumption }} g</div>
        <div>Consumo medio de agua día-ave: {{ avgWaterConsumption }} ml</div>

        <h5>Puesta</h5>
        <div>Pico de puesta: {{ peakPercentage }} %</div>
        <div>Huevos por ave alojada: {{ totalEggsByAnimalBorn }}</div>
        <div>
          Huevos útiles por ave alojada: {{ totalUsefulEggsByAnimalBorn }}
        </div>

        <h5>Distribución de masa</h5>
        <div>Masa promedio del huevo: {{ averageEggMass }} g</div>

        <v-row>
          <v-col cols="12" md="2">
            <highcharts
              class="chart"
              :options="chartOptions"
              :updateArgs="updateArgs"
            ></highcharts>
          </v-col>
        </v-row>
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
  </v-container>
</template>

<script>
import HensBatchService from "@/services/production/HensBatchService.js";
import HensBatchReportService from "@/services/production/HensBatchReportService.js";

export default {
  name: "HensBatchStatistics",
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
      totalEggsByAnimalBorn: 0,
      totalUsefulEggsByAnimalBorn: 0,
      averageEggMass: 0,
      hensBatchReports: null,
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
      spinner: {
        loading: false,
        counter: 0,
      },
    };
  },
  async created() {
    this.loadPageDate();
  },
  methods: {
    async loadPageDate() {
      await this.loadHensBatch();
      await this.loadHensBatchReports();
      this.calcStatistics();
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
    calcStatistics() {
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
      let born = this.$moment.utc(this.hensBatch.birthTimestamp, "x", true);
      this.hensBatchReports.forEach((element) => {
        let current = this.$moment.utc(element.reportTimestamp, "x", true);
        element.week = current.diff(born, "weeks") + 1;
      });

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

      this.totalEggsByAnimalBorn = (
        reportsByWeek.reduce(
          (accumulator, element) =>
            (accumulator +=
              7 *
              (element.numXL +
                element.numL +
                element.numM +
                element.numS +
                element.numXS +
                element.dirties +
                element.brokens)),
          0
        ) / totalAnimals
      ).toFixed(0);

      this.totalUsefulEggsByAnimalBorn = (
        reportsByWeek.reduce(
          (accumulator, element) =>
            (accumulator +=
              7 *
              (element.numXL +
                element.numL +
                element.numM +
                element.numS +
                element.numXS)),
          0
        ) / totalAnimals
      ).toFixed(0);

      let totalXL = reportsByWeek.reduce(
        (accumulator, element) => (accumulator += 7 * element.numXL),
        0
      );
      let totalL = reportsByWeek.reduce(
        (accumulator, element) => (accumulator += 7 * element.numL),
        0
      );
      let totalM = reportsByWeek.reduce(
        (accumulator, element) => (accumulator += 7 * element.numM),
        0
      );
      let totalS = reportsByWeek.reduce(
        (accumulator, element) => (accumulator += 7 * element.numS),
        0
      );
      let totalXS = reportsByWeek.reduce(
        (accumulator, element) => (accumulator += 7 * element.numXS),
        0
      );

      let totalEggs = totalXL + totalL + totalM + totalS + totalXS;

      this.averageEggMass = (
        (totalXL * 78 +
          totalL * 68 +
          totalM * 58 +
          totalS * 48 +
          totalXS * 38) /
        totalEggs
      ).toFixed(0);

      let shareXL = (totalXL / totalEggs) * 100;
      let shareL = (totalL / totalEggs) * 100;
      let shareM = (totalM / totalEggs) * 100;
      let shareS = (totalS / totalEggs) * 100;
      let shareXS = (totalXS / totalEggs) * 100;

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

      this.calcsDone = true;
    },
  },
};
</script>