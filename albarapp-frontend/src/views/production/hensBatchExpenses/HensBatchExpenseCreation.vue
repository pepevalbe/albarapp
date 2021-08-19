<template>
  <v-flex align-self-start>
    <HensBatchExpenseForm
      v-if="hensBatch"
      :form="form"
      :hensBatch="hensBatch"
      ref="form"
      @nextfocus="$refs.saveButton.$el.focus()"
    ></HensBatchExpenseForm>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn
          :disabled="!form.valid"
          ref="saveButton"
          class="mr-4"
          @click="createHensBatchExpense()"
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
import HensBatchExpenseForm from "@/components/production/HensBatchExpenseForm";
import HensBatchExpenseService from "@/services/production/HensBatchExpenseService.js";
import HensBatchService from "@/services/production/HensBatchService.js";

export default {
  name: "HensBatchExpenseCreation",
  components: {
    HensBatchExpenseForm,
  },
  props: {
    hensBatchId: String,
  },
  data: () => ({
    form: {
      valid: false,
      hensBatchExpense: {
        description: "",
        value: "0",
        recurrent: false,
        distribution: false,
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
    async createHensBatchExpense() {
      try {
        this.showSpinner();
        this.form.hensBatchExpense.hensBatchId = this.hensBatch.id;
        await HensBatchExpenseService.create(this.form.hensBatchExpense);
        this.snackbar = {
          show: true,
          message: "Gasto creado correctamente",
          color: "success",
        };
        this.reset();
      } catch (e) {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido crear el gasto, por favor vuelva a intentarlo.",
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