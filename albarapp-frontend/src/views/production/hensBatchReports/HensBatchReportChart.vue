<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-card>
        <v-card-title class="mb-4">
          <v-icon large class="mr-2">mdi-chart-bar</v-icon>Evolución semanal
        </v-card-title>
        <highcharts
          class="chart"
          :options="chartOptions"
          :updateArgs="updateArgs"
          ref="chart"
        ></highcharts>
        <v-row class="mr-10 ml-10">
          <v-col>
            <v-card>
              <v-card-title>Añadir principal</v-card-title>
              <v-card-text>
                <v-select
                  v-model="hensBatch[0]"
                  label="Seleccione un lote"
                  :items="hensBatches"
                  item-text="name"
                  return-object
                  no-data-text="Sin coincidencias"
                ></v-select>
                <v-select
                  v-model="chartType[0]"
                  label="Seleccione un atributo"
                  :items="chartTypes"
                  item-text="name"
                  return-object
                  no-data-text="Sin coincidencias"
                ></v-select>
              </v-card-text>
              <v-card-actions>
                <v-btn
                  :disabled="!hensBatch[0] || !chartType[0]"
                  @click="drawNewChart(0)"
                  >Añadir</v-btn
                >
              </v-card-actions>
            </v-card>
          </v-col>
          <v-spacer />
          <v-col>
            <v-card>
              <v-card-title>Añadir secundario</v-card-title>
              <v-card-text>
                <v-select
                  v-model="hensBatch[1]"
                  label="Seleccione un lote"
                  :items="hensBatches"
                  item-text="name"
                  return-object
                  autocomplete="off"
                  no-data-text="Sin coincidencias"
                ></v-select>
                <v-select
                  v-model="chartType[1]"
                  label="Seleccione un atributo"
                  :items="chartTypes"
                  item-text="name"
                  return-object
                  disable-lookup="true"
                  no-data-text="Sin coincidencias"
                ></v-select>
              </v-card-text>
              <v-card-actions>
                <v-btn
                  :disabled="!hensBatch[1] || !chartType[1]"
                  @click="drawNewChart(1)"
                  >Añadir</v-btn
                >
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener las estadísticas, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="refreshPage()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-container>
</template>

<script>
import HensBatchService from "@/services/production/HensBatchService.js";
import HensBatchReportService from "@/services/production/HensBatchReportService.js";

export default {
  name: "HensBatchReportChart",
  data: () => {
    return {
      chartOptions: {
        chart: {
          type: "spline",
          height: "600px",
        },
        title: {
          text: "",
        },
        plotOptions: {
          dataLabels: {
            enabled: true,
          },
          area: {
            stacking: "percent",
          },
        },
        series: [],
        yAxis: [
          {
            key: 0,
            title: {
              text: "Porcentaje ( % )",
            },
            max: 110,
            min: 0,
            startOnTick: false,
            endOnTick: false,
            visible: false,
          },
          {
            key: 1,
            title: {
              text: "Consumo de pienso / ave ( g )",
            },
            visible: false,
          },
          {
            key: 2,
            title: {
              text: "Consumo de agua / ave ( ml )",
            },
            visible: false,
          },
          {
            key: 3,
            title: {
              text: "Temperatura ( ºC )",
            },
            visible: false,
          },
        ],
        xAxis: {
          allowDecimals: false,
        },
        tooltip: {
          shared: true,
        },
      },
      updateArgs: [true, true, true],
      hensBatches: [],
      hensBatch: [],
      chartTypes: [
        {
          key: 0,
          name: "Porcentaje de puesta",
          type: "spline",
          yAxis: {
            key: 0,
          },
        },
        {
          key: 1,
          name: "Consumo de pienso",
          type: "spline",
          yAxis: {
            key: 1,
          },
        },
        {
          key: 2,
          name: "Consumo de agua",
          type: "spline",
          yAxis: {
            key: 2,
          },
        },
        {
          key: 3,
          name: "Temperatura",
          type: "spline",
          yAxis: {
            key: 3,
          },
        },
        {
          key: 4,
          name: "Mortalidad acumulada",
          type: "spline",
          yAxis: {
            key: 0,
          },
        },
        {
          key: 5,
          name: "Tamaño del huevo",
          type: "area",
          yAxis: {
            key: 0,
          },
        },
      ],
      chartType: [],
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0,
      },
    };
  },
  async created() {
    await this.loadHensBatches();
  },
  methods: {
    async loadHensBatches() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatches = await HensBatchService.getAll();
        this.hensBatches.sort((a, b) => b.birthTimestamp - a.birthTimestamp);
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    refreshPage() {
      this.$router.go(0);
    },
    async drawNewChart(index) {
      let vm = this;
      var hensBatchReports = await HensBatchReportService.getByHensBatchId(
        this.hensBatch[index].id
      );

      hensBatchReports.forEach((element) => {
        var current = this.$moment.utc(element.reportTimestamp, "x", true);
        var born = this.$moment.utc(
          this.hensBatch[index].birthTimestamp,
          "x",
          true
        );
        element.week = current.diff(born, "weeks") + 1;
      });

      var reportsByWeek = hensBatchReports
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
        .filter((element) => {
          return element;
        });
      var totalAnimals = this.hensBatch[index].animalQuantity;
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

      switch (this.chartType[index].key) {
        case 0:
          this.changeChartType(this.chartType[index].type);

          this.chartOptions.series.push({
            name:
              this.chartType[index].name + " - " + this.hensBatch[index].name,
            data: reportsByWeek.map((element) => [
              element.week,
              element.percentage,
            ]),
            tooltip: {
              valueDecimals: 2,
              headerFormat: "<b>Semana:</b> {point.key}<br/>",
              pointFormat:
                '<b style="color: {series.color}">● % puesta:</b> {point.y} %<br/>',
            },
            yAxis: this.chartType[index].yAxis.key,
            events: {
              legendItemClick: function (e) {
                e.preventDefault();
                vm.chartOptions.series.splice(this.index, 1);
              },
              click: function (e) {
                const { href } = vm.$router.resolve({
                  name: "HensBatchStatistics",
                  params: { hensBatchId: vm.hensBatch[index].id },
                  query: {
                    weekFrom: e.point.category,
                    weekTo: e.point.category,
                  },
                });
                window.open(href, "_blank");
              },
            },
          });
          break;

        case 1:
          this.changeChartType(this.chartType[index].type);

          // Remove first and last week because not true consumptions
          reportsByWeek.pop();
          reportsByWeek.shift();
          this.chartOptions.series.push({
            name:
              this.chartType[index].name + " - " + this.hensBatch[index].name,
            data: reportsByWeek.map((element) => [
              element.week,
              Math.round(element.hensPoultryMashConsumption * 1000),
            ]),
            tooltip: {
              headerFormat: "<b>Semana:</b> {point.key}<br/>",
              pointFormat:
                '<b style="color: {series.color}">● Pienso:</b> {point.y} g<br/>',
            },
            yAxis: this.chartType[index].yAxis.key,
            events: {
              legendItemClick: function (e) {
                e.preventDefault();
                vm.chartOptions.series.splice(this.index, 1);
              },
            },
          });
          break;

        case 2:
          this.changeChartType(this.chartType[index].type);

          // Remove last week because not true consumptions
          reportsByWeek.pop();
          this.chartOptions.series.push({
            name:
              this.chartType[index].name + " - " + this.hensBatch[index].name,
            data: reportsByWeek.map((element) => [
              element.week,
              Math.round(element.hensWaterConsumption * 1000),
            ]),
            tooltip: {
              headerFormat: "<b>Semana:</b> {point.key}<br/>",
              pointFormat:
                '<b style="color: {series.color}">● Agua:</b> {point.y} ml<br/>',
            },
            yAxis: this.chartType[index].yAxis.key,
            events: {
              legendItemClick: function (e) {
                e.preventDefault();
                vm.chartOptions.series.splice(this.index, 1);
              },
            },
          });
          break;

        case 3:
          this.changeChartType(this.chartType[index].type);

          this.chartOptions.series.push({
            name:
              this.chartType[index].name +
              " Max - " +
              this.hensBatch[index].name,
            data: reportsByWeek.map((element) => [
              element.week,
              element.maxTemperature,
            ]),
            tooltip: {
              valueDecimals: 1,
              headerFormat: "<b>Semana:</b> {point.key}<br/>",
              pointFormat:
                '<b style="color: {series.color}">● Temperatura(▲):</b> {point.y} ºC<br/>',
            },
            yAxis: this.chartType[index].yAxis.key,
            events: {
              legendItemClick: function (e) {
                e.preventDefault();
                vm.chartOptions.series.splice(this.index, 1);
              },
            },
          });
          this.chartOptions.series.push({
            name:
              this.chartType[index].name +
              " Min - " +
              this.hensBatch[index].name,
            data: reportsByWeek.map((element) => [
              element.week,
              element.minTemperature,
            ]),
            tooltip: {
              valueDecimals: 1,
              headerFormat: "<b>Semana:</b> {point.key}<br/>",
              pointFormat:
                '<b style="color: {series.color}">● Temperatura(▼):</b> {point.y} ºC<br/>',
            },
            yAxis: this.chartType[index].yAxis.key,
            events: {
              legendItemClick: function (e) {
                e.preventDefault();
                vm.chartOptions.series.splice(this.index, 1);
              },
            },
          });
          break;

        case 4:
          this.changeChartType(this.chartType[index].type);

          this.chartOptions.series.push({
            name:
              this.chartType[index].name + " - " + this.hensBatch[index].name,
            data: reportsByWeek.map((element) => [
              element.week,
              element.accumulatedMoratlity,
            ]),
            tooltip: {
              valueDecimals: 1,
              headerFormat: "<b>Semana:</b> {point.key}<br/>",
              pointFormat:
                '<b style="color: {series.color}">● % mortalidad:</b> {point.y} %<br/>',
            },
            yAxis: this.chartType[index].yAxis.key,
            events: {
              legendItemClick: function (e) {
                e.preventDefault();
                vm.chartOptions.series.splice(this.index, 1);
              },
            },
          });
          break;

        case 5:
          this.removeAllCharts();
          this.changeChartType(this.chartType[index].type);

          this.addEggSize("XL", index, reportsByWeek);
          this.addEggSize("L", index, reportsByWeek);
          this.addEggSize("M", index, reportsByWeek);
          this.addEggSize("S", index, reportsByWeek);
          this.addEggSize("XS", index, reportsByWeek);
      }

      this.chartOptions.yAxis[this.chartType[index].yAxis.key].visible = true;
      if (index === 0) {
        this.chartOptions.yAxis[
          this.chartType[index].yAxis.key
        ].opposite = false;
      } else {
        this.chartOptions.yAxis[
          this.chartType[index].yAxis.key
        ].opposite = true;
      }
    },
    changeChartType(newChartType) {
      if (this.chartOptions.chart.type !== newChartType) {
        this.removeAllCharts();
      }
      this.chartOptions.chart.type = newChartType;
    },
    removeAllCharts() {
      this.chartOptions.series.splice(0, this.chartOptions.series.length);
    },
    addEggSize(size, index, reportsByWeek) {
      let vm = this;
      let attrName = "num" + size;
      this.chartOptions.series.push({
        name: size + " - " + this.hensBatch[index].name,
        data: reportsByWeek.map((element) => [element.week, element[attrName]]),
        tooltip: {
          valueDecimals: 1,
          headerFormat: "<b>Semana:</b> {point.key}<br/>",
          pointFormat:
            '<b style="color: {series.color}">● % ' +
            size +
            ":</b> {point.percentage:.1f} %<br/>",
        },
        yAxis: this.chartType[index].yAxis.key,
        events: {
          legendItemClick: function (e) {
            e.preventDefault();
            vm.chartOptions.series.splice(this.index, 1);
          },
        },
      });
    },
  },
};
</script>