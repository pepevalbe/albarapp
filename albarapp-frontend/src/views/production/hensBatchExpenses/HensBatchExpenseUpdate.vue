<template>
  <v-flex align-self-start>
    <div v-if="!errorLoading">
      <HensBatchExpenseForm
        v-if="form.hensBatchExpense && hensBatch"
        :hensBatch="hensBatch"
        :form="form"
        @nextfocus="$refs.saveButton.$el.focus()"
      ></HensBatchExpenseForm>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
          <v-btn
            :disabled="!form.valid"
            ref="saveButton"
            class="mr-4"
            @click="updateHensBatchExpense()"
            >Actualizar</v-btn
          >
        </v-flex>
      </v-layout>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al cargar el gasto, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadHensBatchExpense()">
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
import HensBatchExpenseForm from "@/components/production/HensBatchExpenseForm";
import HensBatchExpenseService from "@/services/production/HensBatchExpenseService.js";
import HensBatchService from "@/services/production/HensBatchService.js";

export default {
  name: "HensBatchExpenseUpdate",
  components: {
    HensBatchExpenseForm,
  },
  data: () => ({
    form: {
      valid: false,
      hensBatchExpense: null,
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
    hensBatchExpenseId: String,
  },
  async created() {
    await this.loadHensBatchExpense();
    this.loadHensBatch();
  },
  methods: {
    async loadHensBatch() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatch = await HensBatchService.get(
          this.form.hensBatchExpense.hensBatchId
        );
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async loadHensBatchExpense() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.form.hensBatchExpense = await HensBatchExpenseService.get(
          this.hensBatchExpenseId
        );
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async updateHensBatchExpense() {
      try {
        this.showSpinner();
        await HensBatchExpenseService.update(
          this.hensBatchExpenseId,
          this.form.hensBatchExpense
        );
        this.snackbar = {
          show: true,
          message: "Gasto actualizado correctamente",
          color: "success",
        };
      } catch (e) {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido modificar el gasto, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>