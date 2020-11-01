<template>
  <v-flex align-self-start>
    <div v-if="!errorLoading">
      <HensBatchForm v-if="form.hensBatch" :form="form"></HensBatchForm>
      <div class="mb-3"></div>
      <div class="mb-10"></div>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
          <v-btn
            :disabled="!form.valid"
            color="success"
            class="mr-4"
            @click="updateHensBatch()"
            >Actualizar</v-btn
          >
        </v-flex>
      </v-layout>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al cargar el lote, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadHensBatch()">
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
import HensBatchForm from "@/components/production/HensBatchForm";
import HensBatchService from "@/services/production/HensBatchService.js";

export default {
  name: "HensBatchUpdate",
  components: {
    HensBatchForm,
  },
  data: () => ({
    form: {
      valid: false,
      hensBatch: null,
    },
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
    hensBatchId: String,
  },
  async created() {
    this.loadHensBatch();
  },
  methods: {
    async loadHensBatch() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.form.hensBatch = await HensBatchService.get(this.hensBatchId);
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async updateHensBatch() {
      try {
        this.showSpinner();
        await HensBatchService.update(this.hensBatchId, this.form.hensBatch);
        this.snackbar = {
          show: true,
          message: "Lote actualizado correctamente",
          color: "success",
        };
      } catch (e) {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido modificar el lote, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>