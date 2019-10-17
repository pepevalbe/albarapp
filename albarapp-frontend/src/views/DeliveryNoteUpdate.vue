<template>
  <v-container>
    <DeliveryNoteForm :form="form" @saveClicked="createDeliveryNote" ref="form"></DeliveryNoteForm>
    <v-snackbar v-model="snackbar">
      Albarán modificado correctamente
      <v-btn color="success" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
import DeliveryNoteForm from "@/components/DeliveryNoteForm";
import DateFormatService from "@/services/DateFormatService.js";
import DeliveryNoteService from "@/services/DeliveryNoteService.js";

export default {
  components: {
    DeliveryNoteForm
  },
  data: () => ({
    form: {
        customer: {},
        date : "",
        auxDeliveryNoteNr: "",
        deliveryNoteItems: [],
        deliveryNoteTotal: {value: 0}
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
        this.form = await DeliveryNoteService.getWithCustomerAndTotal(this.deliveryNoteId);
        //this.$refs.form.selectCustomerByAlias();
        //this.$refs.form.parseDatePick();
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