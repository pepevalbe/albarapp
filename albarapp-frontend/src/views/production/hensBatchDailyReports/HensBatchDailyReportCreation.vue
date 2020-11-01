<template>
  <v-flex align-self-start>
    <HensBatchDailyReportForm
      v-if="hensBatch"
      :form="form"
      :hensBatch="hensBatch"
      ref="form"
    ></HensBatchDailyReportForm>
    <div class="mb-3"></div>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn
          :disabled="!form.valid"
          color="success"
          class="mr-4"
          @click="createHenseBatchDailyReport()"
          >Crear</v-btn
        >
        <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>
        <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
      </v-flex>
    </v-layout>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{ snackbar.message }}
      <v-btn text @click="snackbar.show = false">Cerrar</v-btn>
    </v-snackbar>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-flex>
</template>

<script>
import HensBatchDailyReportForm from "@/components/production/HensBatchDailyReportForm";
import HensBatchDailyReportService from "@/services/production/HensBatchDailyReportService.js";
import HensBatchService from "@/services/production/HensBatchService.js";

export default {
  name: "HensBatchDailyReportCreation",
  components: {
    HensBatchDailyReportForm,
  },
  props: {
    hensBatchId: String,
  },
  data: () => ({
    form: {
      valid: false,
      hensBatchDailyReport: {
        xl: "0",
        l: "0",
        m: "0",
        s: "0",
        xs: "0",
        dirties: "0",
        brokens: "0",
        deaths: "0",
        departures: "0",
      },
    },
    hensBatch: null,
    snackbar: {
      show: false,
      message: "",
      color: "",
    },
    spinner: {
      loading: false,
      counter: 0,
    },
  }),
  async created() {
    this.loadHensBatch();
  },
  methods: {
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
    async createHenseBatchDailyReport() {
      try {
        this.showSpinner();
        this.form.hensBatchDailyReport.hensBatch = this.hensBatch._links.self.href;
        this.form.hensBatchDailyReport.deaths = 0;
        this.form.hensBatchDailyReport.departures = 0;
        await HensBatchDailyReportService.create(
          this.form.hensBatchDailyReport
        );
        this.snackbar = {
          show: true,
          message: "Reporte diario creado correctamente",
          color: "success",
        };
        this.reset();
      } catch (e) {
        console.log(e);
        this.snackbar = {
          show: true,
          message:
            "No se ha podido crear el reporte diario, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    reset() {
      this.$refs.form.reset();
    },
  },
};
</script>