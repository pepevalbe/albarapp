<template>
  <v-flex align-self-start>
    <HensBatchForm :form="form" ref="form"></HensBatchForm>
    <div class="mb-3"></div>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
        <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>
        <v-btn
          :disabled="!form.valid"
          color="success"
          class="mr-4"
          @click="createHenseBatch()"
          >Crear</v-btn
        >
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
import HensBatchForm from "@/components/production/HensBatchForm";
import HensBatchService from "@/services/production/HensBatchService.js";

export default {
  name: "HensBatchCreation",
  components: {
    HensBatchForm,
  },
  data: () => ({
    form: {
      valid: false,
      hensBatch: {},
    },
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
  methods: {
    async createHenseBatch() {
      try {
        this.showSpinner();
        await HensBatchService.create(this.form.hensBatch);
        this.snackbar = {
          show: true,
          message: "Lote creado correctamente",
          color: "success",
        };
        this.reset();
      } catch (e) {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido crear el lote, por favor vuelva a intentarlo.",
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