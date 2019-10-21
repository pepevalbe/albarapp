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
import DateFormatService from "@/services/DateFormatService.js";
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
      /*
      var vm = this;
      var promises = [];
      // Rest call to create new deliveryNote

      var deliveryNote = {
        auxDeliveryNoteNr: this.form.deliveryNote.auxDeliveryNoteNr,
        issuedTimestamp: this.form.deliveryNote.issuedTimestamp,
        customer: this.form.deliveryNote.customer._links.self.href
      };

      this.$axios
        .put(this.form.deliveryNote._links.self.href, deliveryNote)
        .then(response => {
          var itemsToDelete = this.deliveryNoteItemsOriginal.filter(
            f => !vm.form.deliveryNote.deliveryNoteItems.includes(f)
          );
          var itemsToInsert = vm.form.deliveryNote.deliveryNoteItems.filter(
            f => !this.deliveryNoteItemsOriginal.includes(f)
          );

          itemsToInsert.forEach(item => {
            var deliveryNoteItem = {
              quantity: item.quantity,
              price: item.price,
              product: item.product._links.self.href,
              deliveryNote: response.data._links.self.href
            };
            promises.push(
              this.$axios.post("/deliveryNoteItems", deliveryNoteItem)
            );
          });

          itemsToDelete.forEach(element => {
            promises.push(
              this.$axios.delete(element._links.self.href).catch(() => {
                alert("Ha ocurrido un error actualizando el albarán");
              })
            );
          });
          Promise.all(promises).then(function() {
            vm.snackbar = true;
          });
        });
        */
    }
  }
};
</script>