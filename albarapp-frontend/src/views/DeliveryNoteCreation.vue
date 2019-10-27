<template>
  <v-container>
    <DeliveryNoteForm v-bind:form="form" @saveClicked="createDeliveryNote" ref="form"></DeliveryNoteForm>
    <v-snackbar v-model="snackbar">
      Albarán creado correctamente
      <v-btn color="success" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
import DeliveryNoteForm from "@/components/DeliveryNoteForm";
import DeliveryNoteService from "@/services/DeliveryNoteService.js";

export default {
  components: {
    DeliveryNoteForm
  },
  data: () => ({
    form: {
      valid: false,
      create: true,
      deliveryNote: {
        customer: {},
        auxDeliveryNoteNr: "",
        issuedTimestamp: 0,
        date: "",
        deliveryNoteItems: [],
        deliveryNoteTotal: { value: 0 }
      },
      deliveryNoteItems: [],
      deliveryNoteTotal: { value: 0 }
    },
    snackbar: false
  }),
  created() {
    this.setDateToday();
  },
  methods: {
    setDateToday() {
      this.form.deliveryNote.date = this.$moment().format("YYYY-MM-DD");
      this.$refs.form.parseDatePick();
    },
    reset() {
      this.$refs.form.reset();
    },
    async createDeliveryNote() {
      await DeliveryNoteService.create(
        this.form.deliveryNote,
        this.form.deliveryNote.deliveryNoteItems
      );
      this.reset();
      this.snackbar = true;
    }
  }
};
</script>