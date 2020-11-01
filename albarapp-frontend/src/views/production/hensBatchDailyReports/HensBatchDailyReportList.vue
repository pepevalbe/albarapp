<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-layout text-right wrap class="pt-2 pb-5 mr-5">
        <v-flex xs12>
          <v-btn :to="toDailyReportCreation()" :disabled="!hensBatch">
            Nuevo
            <v-icon class="ml-2">mdi-plus-circle</v-icon>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-card>
        <v-card-title>
          Reportes diarios
          <div class="flex-grow-1"></div>
          <v-autocomplete
            v-model="hensBatch"
            label="Seleccione un lote"
            :items="hensBatches"
            item-text="name"
            return-object
            clearable
            autocomplete="off"
            no-data-text="Sin coincidencias"
            class="mr-5 ml-5"
            @change="selectHensBatchByName()"
          />
        </v-card-title>
        <v-data-table
          :loading="!hensBatchDailyReports"
          loading-text="Cargando... Por favor, espere"
          :headers="headers"
          :items="hensBatchDailyReports"
          :search="search"
          :sort-by.sync="sortBy"
          :sort-desc.sync="descending"
          :items-per-page="15"
        >
          <template v-slot:body="{ items }">
            <tbody
              v-if="!$vuetify.breakpoint.xsOnly"
              style="text-align: center"
            >
              <tr v-for="item in items" :key="item.code">
                <td>
                  {{ dateFormatted(item.dateTimestamp) }}
                </td>
                <td>{{ item.xl }}</td>
                <td>{{ item.l }}</td>
                <td>{{ item.m }}</td>
                <td>{{ item.s }}</td>
                <td>{{ item.xs }}</td>
                <td>{{ item.dirties }}</td>
                <td>{{ item.brokens }}</td>
                <td>{{ item.deaths }}</td>
                <td>
                  <v-tooltip bottom v-if="item.comments">
                    <template v-slot:activator="{ on, attrs }">
                      <v-icon color="black" dark v-bind="attrs" v-on="on">
                        mdi-comment-text-multiple
                      </v-icon>
                    </template>
                    <span>{{ item.comments }}</span>
                  </v-tooltip>
                </td>
                <td>
                  <v-btn @click="updateHensBatchDailyReport(item)">
                    <v-icon dark>mdi-pencil</v-icon>
                  </v-btn>
                </td>
                <td>
                  <v-btn
                    v-if="!item.invoiceId"
                    color="red"
                    dark
                    @click="openDeleteDialog(item)"
                  >
                    <v-icon dark>mdi-delete</v-icon>
                  </v-btn>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr>
                <v-card
                  class="flex-content"
                  outlined
                  v-for="item in items"
                  :key="item.id"
                >
                  <v-card-text>
                    <span class="black--text">Fecha:</span>
                    {{ dateFormatted(item.dateTimestamp) }}
                    <br />
                    <span class="black--text">XL:</span>
                    {{ item.xl }}
                    <br />
                    <span class="black--text">L:</span>
                    {{ item.l }}
                    <br />
                    <span class="black--text">M:</span>
                    {{ item.m }}
                    <br />
                    <span class="black--text">S:</span>
                    {{ item.s }}
                    <br />
                    <span class="black--text">XS:</span>
                    {{ item.xs }}
                    <br />
                    <span class="black--text">Sucios:</span>
                    {{ item.dirties }}
                    <br />
                    <span class="black--text">Rotos:</span>
                    {{ item.brokens }}
                    <br />
                    <span class="black--text">Muertas:</span>
                    {{ item.deaths }}
                    <br />
                    <span class="black--text">Comentarios:</span>
                    {{ item.comments }}
                    <br />
                  </v-card-text>
                  <v-card-actions>
                    <v-layout text-center wrap>
                      <v-flex xs12>
                        <v-btn @click="updateHensBatchDailyReport(item)">
                          <v-icon dark>mdi-pencil</v-icon>
                        </v-btn>
                        <v-btn color="red" dark @click="openDeleteDialog(item)">
                          <v-icon dark>mdi-delete</v-icon>
                        </v-btn>
                      </v-flex>
                    </v-layout>
                  </v-card-actions>
                </v-card>
              </tr>
            </tbody>
          </template>
        </v-data-table>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener los reportes diarios, por favor vuelva a
        cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadHensBatches()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
    <v-dialog v-model="dialogDelete.show" max-width="600" v-if="hensBatch">
      <v-card>
        <v-card-title class="headline">¿Eliminar reporte diario?</v-card-title>
        <v-card-text
          >Si sigue adelante eliminará el reporte diario con fecha
          {{ dateFormatted(dialogDelete.dailyReport.dateTimestamp) }} del lote
          {{ hensBatch.name }}. Este proceso es irreversible.</v-card-text
        >
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" text @click="dialogDelete.show = false"
            >Cancelar</v-btn
          >
          <v-btn
            color="red darken-1"
            text
            @click="deleteHensBatchDailyReport(dialogDelete.dailyReport)"
            >Confirmar</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import HensBatchService from "@/services/production/HensBatchService.js";
import HensBatchDailyReportService from "@/services/production/HensBatchDailyReportService.js";

export default {
  name: "HensBatchDailyReportList",
  data: () => {
    return {
      hensBatchDailyReports: [],
      hensBatches: [],
      hensBatch: null,
      headers: [
        {
          text: "Fecha",
          sortable: true,
          align: "center",
          value: "dateTimestamp",
        },
        { text: "XL", sortable: true, align: "center", value: "xl" },
        { text: "L", sortable: true, align: "center", value: "l" },
        { text: "M", sortable: true, align: "center", value: "m" },
        { text: "S", sortable: true, align: "center", value: "s" },
        { text: "XS", sortable: true, align: "center", value: "xs" },
        { text: "Sucios", sortable: true, align: "center", value: "dirties" },
        { text: "Rotos", sortable: true, align: "center", value: "brokens" },
        { text: "Muertas", sortable: true, align: "center", value: "deaths" },
        {
          text: "Comentarios",
          sortable: false,
          align: "center",
          value: "comments",
        },
        { text: "", sortable: false, align: "center", value: "update" },
        { text: "", sortable: false, align: "center", value: "delete" },
      ],
      search: "",
      sortBy: "dateTimestamp",
      descending: true,
      dialogDelete: {
        show: false,
        dailyReport: {},
      },
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0,
      },
    };
  },
  async created() {
    await this.loadHensBatches();
    var vm = this;
    this.hensBatch = this.hensBatches.find(function (hensBatch) {
      return hensBatch.id === vm.$store.getters.hensBatch;
    });
    if (this.hensBatch) this.selectHensBatchByName();
  },
  methods: {
    async loadHensBatches() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatches = await HensBatchService.getAll();
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async selectHensBatchByName() {
      if (this.hensBatch) {
        try {
          this.showSpinner();
          this.errorLoading = false;
          this.$store.commit(
            "filterDailyReportsByHensBatch",
            this.hensBatch.id
          );
          this.hensBatchDailyReports = await HensBatchDailyReportService.getByHensBatchId(
            this.hensBatch.id
          );
        } catch (e) {
          this.errorLoading = true;
        } finally {
          this.closeSpinner();
        }
      } else {
        this.hensBatchDailyReports = [];
        this.$store.commit("filterDailyReportsByHensBatch", null);
      }
    },
    updateHensBatchDailyReport(item) {
      this.$router.push({
        name: "HensBatchDailyReportUpdate",
        params: { hensBatchDailyReportId: item.id },
      });
    },
    toDailyReportCreation() {
      if (this.hensBatch) {
        return {
          name: "HensBatchDailyReportCreation",
          params: { hensBatchId: this.hensBatch.id },
        };
      }
    },
    openDeleteDialog(item) {
      this.dialogDelete.dailyReport = item;
      this.dialogDelete.show = true;
    },
    async deleteHensBatchDailyReport(item) {
      try {
        this.dialogDelete.show = false;
        this.showSpinner();
        await HensBatchDailyReportService.delete(item.id);
        this.snackbar = {
          show: true,
          message: "Reporte diario eliminado correctamente",
          color: "success",
        };
        this.selectHensBatchByName();
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido eliminar el reporte diario, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>
