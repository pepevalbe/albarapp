<template>
  <v-flex align-self-start>
    <div v-if="!errorLoading">
      <HensBatchReportForm
        v-if="form.hensBatchReport && hensBatch"
        :hensBatch="hensBatch"
        :form="form"
        @nextfocus="$refs.saveButton.$el.focus()"
      ></HensBatchReportForm>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
          <v-btn
            :disabled="!form.valid"
            ref="saveButton"
            class="mr-4"
            @click="updateHensBatchReport()"
            >Actualizar</v-btn
          >
        </v-flex>
      </v-layout>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al cargar el reporte, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadHensBatchReport()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
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
  name: "HensBatchReportUpdate",
  components: {
    HensBatchReportForm,
  },
  data: () => ({
    form: {
      valid: false,
      hensBatchReport: null,
    },
    hensBatch: null,
    errorLoading: false,
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
  props: {
    hensBatchReportId: String,
  },
  async created() {
    await this.loadHensBatchReport();
    this.loadHensBatch();
  },
  methods: {
    async loadHensBatch() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatch = await HensBatchService.get(this.form.hensBatchReport.hensBatchId);
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async loadHensBatchReport() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.form.hensBatchReport = await HensBatchReportService.get(
          this.hensBatchReportId
        );
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async updateHensBatchReport() {
      try {
        this.showSpinner();
        await HensBatchReportService.update(
          this.hensBatchReportId,
          this.form.hensBatchReport
        );
        this.snackbar = {
          show: true,
          message: "Reporte diario actualizado correctamente",
          color: "success",
        };
      } catch (e) {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido modificar el reporte diario, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>