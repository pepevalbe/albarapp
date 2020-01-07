<template>
  <v-flex align-self-start>
    <DeliveryNoteForm :form="form" @saveClicked="updateDeliveryNote" ref="form"></DeliveryNoteForm>
    <v-snackbar v-model="snackbar">
      Albarán modificado correctamente
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
  name: "DeliveryNoteUpdate",
  components: {
    DeliveryNoteForm
  },
  data: () => ({
    form: {
      valid: false,
      create: false,
      deliveryNote: {
        customer: {},
        date: "",
        auxDeliveryNoteNr: "",
        deliveryNoteItems: [],
        deliveryNoteTotal: { value: 0 }
      }
    },
    snackbar: false,
    spinner: {
      loading: false,
      counter: 0
    }
  }),
  props: {
    deliveryNoteId: String
  },
  created() {
    this.loadDeliveryNote();
  },
  methods: {
    async loadDeliveryNote() {
      this.showSpinner();
      var deliveryNote = await DeliveryNoteService.getWithCustomerAndTotal(
        this.deliveryNoteId
      );
      this.form = {
        valid: false,
        create: false,
        deliveryNote: deliveryNote
      };
      this.deliveryNoteItemsOriginal = Array.from(
        this.form.deliveryNote.deliveryNoteItems
      );
      this.closeSpinner();
    },
    reset() {
      this.$refs.form.reset();
    },
    async updateDeliveryNote() {
      await DeliveryNoteService.update(
        this.deliveryNoteId,
        this.form.deliveryNote,
        this.form.deliveryNote.deliveryNoteItems,
        this.deliveryNoteItemsOriginal
      );
      this.snackbar = true;
      this.loadDeliveryNote();
    }
  }
};
</script>