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
      this.form.deliveryNote.date = year + "-" + month + "-" + day;
      this.form = {
        valid: false,
        create: true,
        deliveryNote: {
          customer: {},
          auxDeliveryNoteNr: "",
          issuedTimestamp: 0,
          date: this.form.deliveryNote.date,
          deliveryNoteItems: [],
          deliveryNoteTotal: { value: 0 }
        },
        customer: {},
        auxDeliveryNoteNr: "",
        date: this.form.date,
        deliveryNoteItems: [],
        deliveryNoteTotal: { value: 0 }
      };
    },
    reset() {
      this.$refs.form.reset();
    },
    createDeliveryNote() {
      var vm = this;
      var promises = [];

      // Rest call to create new deliveryNote
      var deliveryNote = {
        auxDeliveryNoteNr: this.form.deliveryNote.dateauxDeliveryNoteNr,
        issuedTimestamp: this.form.deliveryNote.issuedTimestamp,
        customer: this.form.deliveryNote.customer._links.self.href
      };

      this.$axios.post("/deliveryNotes", deliveryNote).then(response => {
        for (var i = 0; i < vm.form.deliveryNote.deliveryNoteItems.length; i++) {
          var item = vm.form.deliveryNote.deliveryNoteItems[i];
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