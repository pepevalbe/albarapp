<template>
  <v-flex align-self-start>
    <DeliveryNoteForm v-bind:form="form" @saveClicked="createDeliveryNote" ref="form"></DeliveryNoteForm>
    <v-snackbar v-model="snackbar">
      Albarán creado correctamente
      <v-btn color="success" text @click="snackbar = false">Cerrar</v-btn>
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
  name: "DeliveryNoteCreation",
  components: {
    DeliveryNoteForm
  },
  data: () => ({
    form: {
      valid: false,
      create: true,
      deliveryNote: {
        customer: null,
        auxDeliveryNoteNr: "",
        issuedTimestamp: 0,
        date: "",
        deliveryNoteItems: [],
        deliveryNoteTotal: { value: 0 }
      },
      deliveryNoteItems: [],
      deliveryNoteTotal: { value: 0 }
    },
    snackbar: false,
    spinner: {
      loading: false,
      counter: 0
    }
  }),
  mounted() {
    this.setDateToday();
  },
  methods: {
    setDateToday() {
      this.form.deliveryNote.date = this.$moment.utc().format("YYYY-MM-DD");
      this.$refs.form.parseDatePick();
    },
    reset() {
      this.$refs.form.reset();
    },
    async createDeliveryNote() {
      this.showSpinner();
      await DeliveryNoteService.create(
        this.form.deliveryNote,
        this.form.deliveryNote.deliveryNoteItems
      );
      this.closeSpinner();
      this.reset();
      this.snackbar = true;
    }
  }
};
</script>