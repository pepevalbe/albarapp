<template>
  <v-container>
    <DeliveryNoteForm :form="form" @saveClicked="updateDeliveryNote" ref="form"></DeliveryNoteForm>
    <v-snackbar v-model="snackbar">
      Albarán modificado correctamente
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
      deliveryNote: {
        customer: {},
        date: "",
        auxDeliveryNoteNr: "",
        deliveryNoteItems: [],
        deliveryNoteTotal: { value: 0 }
      }
    },
    snackbar: false
  }),
  props: {
    deliveryNoteId: Number
  },
  created() {
    this.loadDeliveryNote();
  },
  methods: {
    async loadDeliveryNote() {
      var deliveryNote = await DeliveryNoteService.getWithCustomerAndTotal(
        this.deliveryNoteId
      );
      this.form = {
        valid: false,
        deliveryNote: deliveryNote
      };
      this.deliveryNoteItemsOriginal = Array.from(
        this.form.deliveryNote.deliveryNoteItems
      );
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