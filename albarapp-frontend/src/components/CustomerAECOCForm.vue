<template>
  <div>
    <v-text-field
      v-model="customerAecocInfo.receiverCln"
      :readonly="readonly"
      type="number"
      :counter="13"
      :rules="receiverClnRules"
      label="GLN Receptor"
    ></v-text-field>
    <v-text-field
      v-model="customerAecocInfo.buyerCln"
      :readonly="readonly"
      type="number"
      :counter="13"
      :rules="buyerClnRules"
      label="GLN Comprador"
    ></v-text-field>
    <v-text-field
      v-model="customerAecocInfo.shipCln"
      :readonly="readonly"
      type="number"
      :counter="13"
      :rules="shipClnRules"
      label="GLN Punto de entrega"
    ></v-text-field>
    <v-text-field
      v-model="customerAecocInfo.payerCln"
      :readonly="readonly"
      type="number"
      :counter="13"
      :rules="payerClnRules"
      label="GLN Pagador"
    ></v-text-field>
    <v-text-field
      v-model="customerAecocInfo.invoiceeCln"
      :readonly="readonly"
      type="number"
      :counter="13"
      :rules="invoiceeClnRules"
      label="GLN de a quién se factura"
    ></v-text-field>
  </div>
</template>

<script>
export default {
  name: "CustomerAECOCForm",
  props: {
    customerAecocInfo: {
      receiverCln: String,
      buyerCln: String,
      shipCln: String,
      payerCln: String,
      invoiceeCln: String
    },
    readonly: Boolean
  },
  data: () => ({
    receiverClnRules: [
      v => !!v || "El GLN del receptor es obligatorio",
      v =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos"
    ],
    buyerClnRules: [
      v => !!v || "El GLN del comprador es obligatorio",
      v =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos"
    ],
    shipClnRules: [
      v => !!v || "El GLN del punto de entrega es obligatorio",
      v =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos"
    ],
    payerClnRules: [
      v => !!v || "El GLN del pagador es obligatorio",
      v =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos"
    ],
    invoiceeClnRules: [
      v => !!v || "El GLN de a quién se factura es obligatorio",
      v =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos"
    ]
  })
};
</script>