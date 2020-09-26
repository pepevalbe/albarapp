<template>
  <div>
    <v-text-field
      v-model="customerAecocInfo.receiverGln"
      :readonly="readonly"
      type="number"
      autocomplete="off"
      :counter="13"
      :rules="receiverGlnRules"
      label="GLN Receptor *"
    ></v-text-field>
    <v-text-field
      v-model="customerAecocInfo.buyerGln"
      :readonly="readonly"
      type="number"
      autocomplete="off"
      :counter="13"
      :rules="buyerGlnRules"
      label="GLN Comprador *"
    ></v-text-field>
    <v-text-field
      v-model="customerAecocInfo.shipGln"
      :readonly="readonly"
      type="number"
      autocomplete="off"
      :counter="13"
      :rules="shipGlnRules"
      label="GLN Punto de entrega *"
    ></v-text-field>
    <v-text-field
      v-model="customerAecocInfo.payerGln"
      :readonly="readonly"
      type="number"
      autocomplete="off"
      :counter="13"
      :rules="payerGlnRules"
      label="GLN Pagador *"
    ></v-text-field>
    <v-text-field
      v-model="customerAecocInfo.invoiceeGln"
      :readonly="readonly"
      type="number"
      autocomplete="off"
      :counter="13"
      :rules="invoiceeGlnRules"
      label="GLN de a quién se factura *"
    ></v-text-field>
  </div>
</template>

<script>
export default {
  name: "CustomerAECOCForm",
  props: {
    customerAecocInfo: {
      receiverGln: String,
      buyerGln: String,
      shipGln: String,
      payerGln: String,
      invoiceeGln: String,
    },
    readonly: Boolean,
  },
  data: () => ({
    receiverGlnRules: [
      (v) => !!v || "El GLN del receptor es obligatorio",
      (v) =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos",
    ],
    buyerGlnRules: [
      (v) => !!v || "El GLN del comprador es obligatorio",
      (v) =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos",
    ],
    shipGlnRules: [
      (v) => !!v || "El GLN del punto de entrega es obligatorio",
      (v) =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos",
    ],
    payerGlnRules: [
      (v) => !!v || "El GLN del pagador es obligatorio",
      (v) =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos",
    ],
    invoiceeGlnRules: [
      (v) => !!v || "El GLN de a quién se factura es obligatorio",
      (v) =>
        (v && v >= 1 && v <= 9999999999999) ||
        "El GLN debe tener un máximo de 13 dígitos",
    ],
  }),
};
</script>