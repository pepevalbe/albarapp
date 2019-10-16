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
import DateFormatService from "@/services/DateFormatService.js";

export default {
  components: {
    DeliveryNoteForm
  },
  data: () => ({
    form: {
      valid: false,
      customer: {},
      auxDeliveryNoteNr: "",
      date: "",
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
      var today = new Date();
      var day = today
        .getDate()
        .toString()
        .padStart(2, "0");
      var month = (today.getMonth() + 1).toString().padStart(2, "0");
      var year = today
        .getFullYear()
        .toString()
        .padStart(4, "0");
      this.form.date = year + "-" + month + "-" + day;
    },
    reset() {
      this.$refs.form.reset();
    },
    createDeliveryNote() {
      var vm = this;
      var promises = [];
      var issuedTimestamp = DateFormatService.datePickToTimestamp(
        this.form.date
      );
      // Rest call to create new deliveryNote
      var deliveryNote = {
        auxDeliveryNoteNr: this.form.auxDeliveryNoteNr,
        issuedTimestamp: issuedTimestamp,
        customer: this.form.customer._links.self.href
      };

      this.$axios.post("/deliveryNotes", deliveryNote).then(response => {
        for (var i = 0; i < vm.form.deliveryNoteItems.length; i++) {
          var item = vm.form.deliveryNoteItems[i];
          var deliveryNoteItem = {
            quantity: item.quantity,
            price: item.price,
            product: item.product._links.self.href,
            deliveryNote: response.data._links.self.href
          };
          promises.push(
            this.$axios.post("/deliveryNoteItems", deliveryNoteItem)
          );
        }
        Promise.all(promises).then(function() {
          vm.reset();
        });
        this.snackbar = true;
      });
    }
  }
};
</script>