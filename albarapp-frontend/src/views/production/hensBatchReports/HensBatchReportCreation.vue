<template>
  <v-flex align-self-start>
    <HensBatchReportForm
      v-if="hensBatch"
      :form="form"
      :hensBatch="hensBatch"
      ref="form"
    ></HensBatchReportForm>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn
          :disabled="!form.valid"
          color="success"
          class="mr-4"
          @click="createHensBatchReport()"
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
import HensBatchReportForm from "@/components/production/HensBatchReportForm";
import HensBatchReportService from "@/services/production/HensBatchReportService.js";
import HensBatchService from "@/services/production/HensBatchService.js";

export default {
  name: "HensBatchReportCreation",
  components: {
    HensBatchReportForm,
  },
  props: {
    hensBatchId: String,
  },
  data: () => ({
    form: {
      valid: false,
      hensBatchReport: {
        numXL: 0,
        numL: 0,
        numM: 0,
        numS: 0,
        numXS: 0,
        dirties: 0,
        brokens: 0,
        deaths: 0,
        departures: 0,
        minTemperature: 0,
        maxTemperature: 0,
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
    async createHensBatchReport() {
      try {
        this.showSpinner();
        this.form.hensBatchReport.hensBatchId = this.hensBatch.id;
        await HensBatchReportService.create(
          this.form.hensBatchReport
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