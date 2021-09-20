<template>
  <v-flex align-self-start>
    <div v-if="!errorLoading">
      <DeliveryNoteForm
        v-if="form.deliveryNote"
        :form="form"
        @saveClicked="updateDeliveryNote"
        ref="form"
      ></DeliveryNoteForm>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener el albarán, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadDeliveryNote()">
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
import DeliveryNoteForm from "@/components/DeliveryNoteForm";
import DeliveryNoteService from "@/services/DeliveryNoteService.js";

export default {
  name: "DeliveryNoteUpdate",
  components: {
    DeliveryNoteForm,
  },
  data: () => ({
    form: {
      valid: false,
      create: false,
      deliveryNote: null,
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
    deliveryNoteId: String,
  },
  created() {
    this.loadDeliveryNote();
  },
  methods: {
    async loadDeliveryNote() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        var deliveryNote = await DeliveryNoteService.get(this.deliveryNoteId);
        this.form.deliveryNote = deliveryNote;
      } catch (e) {
        console.log(e);
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    reset() {
      this.$refs.form.reset();
    },
    async updateDeliveryNote() {
      try {
        this.showSpinner();
        await DeliveryNoteService.update(this.form.deliveryNote);
        this.snackbar = {
          show: true,
          message: "Albarán actualizado correctamente",
          color: "success",
        };
        await this.loadDeliveryNote();
        this.$refs.form.loadView();
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido modificar el albarán, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>