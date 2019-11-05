<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1">Proceso de facturación</v-subheader>
      <v-text-field
        v-model="customerCodeFrom"
        type="number"
        :counter="5"
        autofocus
        :rules="codeFromRules"
        label="Código desde *"
        required
      ></v-text-field>
      <v-text-field
        v-model="customerCodeTo"
        :counter="5"
        :rules="codeToRules"
        label="Código hasta *"
        required
      ></v-text-field>
      <v-text-field
        v-model="dateFrom"
        type="text"
        :rules="dateFromRules"
        label="Fecha desde (dd/mm/aaaa) *"
        required
      ></v-text-field>
      <v-text-field
        v-model="dateTo"
        type="text"
        :rules="dateToRules"
        label="Fecha hasta (dd/mm/aaaa) *"
        required
      ></v-text-field>
      <v-text-field
        v-model="issuedDate"
        type="text"
        :rules="issuedDateRules"
        label="Fecha emisión (dd/mm/aaaa) *"
        required
      ></v-text-field>
    </v-form>
    <v-btn ref="billButton" class="mr-4" :disabled="!form.valid" @click="createInvoices()">Guardar</v-btn>
    <v-btn to="/invoice-list/">Volver</v-btn>
  </v-container>
</template>

<script>
import InvoiceService from "@/services/InvoiceService.js";
//import DeliveryNoteService from "@/services/DeliveryNoteService.js";

export default {
  name: "BillProcess",
  data: () => ({
    form: {
      valid: false
    },
    customerCodeFrom: "",
    customerCodeTo: "",
    dateFrom: "",
    dateTo: "",
    issuedDate: "",
    codeFromRules: [
      v => !!v || "El código es obligatorio",
      v =>
        (v && v > 0 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos"
    ],
    codeToRules: [
      v => !!v || "El código es obligatorio",
      v =>
        (v && v > 0 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos"
    ],
    dateFromRules: [v => !!v || "La fecha desde es obligatoria"],
    dateToRules: [v => !!v || "La fecha hasta es obligatoria"],
    issuedDateRules: [v => !!v || "La fecha de emisión es obligatoria"]
  }),
  methods: {
    async createInvoices() {
      /*this.deliveryNotesToBill = await DeliveryNoteService.findDeliveryNotesToBill(
        this.customerCodeFrom,
        this.$moment(this.dateFrom, "DD/MM/YYYY").format("x"),
        this.$moment(this.dateTo, "DD/MM/YYYY").format("x")
      );
      var invoice = {
        issuedTimestamp: this.$moment(this.issuedDate, "DD/MM/YYYY").format("x")
      };
      await InvoiceService.create(invoice, this.deliveryNotesToBill);*/
      InvoiceService.createList(
        this.customerCodeFrom,
        this.customerCodeTo,
        this.$moment(this.dateFrom, "DD/MM/YYYY").format("x"),
        this.$moment(this.dateTo, "DD/MM/YYYY").format("x"),
        this.$moment(this.issuedDate, "DD/MM/YYYY").format("x")
      );
    }
  }
};
</script>