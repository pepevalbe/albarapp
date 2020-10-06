<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1">Datos de cliente</v-subheader>
      <v-divider></v-divider>
      <v-text-field
        v-model="form.customer.code"
        :readonly="readonly"
        type="number"
        autocomplete="off"
        :counter="5"
        :rules="codeRules"
        :autofocus="!readonly"
        @focus="$event.target.select()"
        label="Código *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.customer.alias"
        :readonly="readonly"
        :counter="80"
        :rules="aliasRules"
        autocomplete="off"
        label="Nombre Comercial *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.customer.name"
        :readonly="readonly"
        :counter="80"
        :rules="nameRules"
        autocomplete="off"
        label="Razón social *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.customer.email"
        :readonly="readonly"
        :rules="emailRules"
        autocomplete="off"
        label="E-mail"
      ></v-text-field>
      <v-text-field
        v-model="form.customer.fiscalId"
        :readonly="readonly"
        :rules="fiscalIdRules"
        autocomplete="off"
        label="NIF *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.customer.address"
        :readonly="readonly"
        :counter="200"
        :rules="addressRules"
        autocomplete="off"
        label="Dirección"
      ></v-text-field>
      <v-text-field
        v-model="form.customer.province"
        :readonly="readonly"
        :counter="20"
        :rules="provinceRules"
        autocomplete="off"
        label="Provincia"
      ></v-text-field>
      <v-text-field
        v-model="form.customer.phoneNumber"
        :readonly="readonly"
        type="number"
        :counter="15"
        :rules="phoneNumberRules"
        autocomplete="off"
        label="Teléfono"
      ></v-text-field>
      <v-text-field
        v-model="form.customer.accountingId"
        :readonly="readonly"
        :counter="20"
        :rules="accountingIdRules"
        autocomplete="off"
        label="Cuenta contable"
      ></v-text-field>
      <v-switch
        @change="createDeleteAecoc"
        :readonly="readonly"
        v-model="form.switchAecoc"
        label="Incluir información para facturación EDI AECOC"
      ></v-switch>
      <CustomerAECOCForm
        v-if="form.switchAecoc"
        :customerAecocInfo="form.customer.customerAecocInfo"
        :readonly="readonly"
      />
    </v-form>
  </v-container>
</template>

<script>
import CustomerAECOCForm from "@/components/CustomerAECOCForm";
export default {
  name: "CustomerForm",
  components: {
    CustomerAECOCForm,
  },
  props: {
    form: {
      valid: Boolean,
      switchAecoc: Boolean,
      customer: {
        code: Number,
        name: String,
        alias: String,
        email: String,
        fiscalId: String,
        address: String,
        province: String,
        phoneNumber: String,
        accountingId: String,
        customerAecocInfo: Object,
      },
    },
    readonly: Boolean,
  },
  data: () => ({
    codeRules: [
      (v) => !!v || "El código es obligatorio",
      (v) =>
        (v && v >= 1 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos",
    ],
    nameRules: [
      (v) => !!v || "El nombre es obligatorio",
      (v) =>
        (v && v.length <= 80) || "El nombre debe tener menos de 80 caracteres",
    ],
    aliasRules: [
      (v) => !!v || "El alias es obligatorio",
      (v) =>
        (v && v.length <= 80) ||
        "El nombre comercial debe tener menos de 80 caracteres",
    ],
    emailRules: [(v) => !v || /.+@.+\..+/.test(v) || "E-mail no válido"],
    fiscalIdRules: [
      (v) => !!v || "El NIF es obligatorio",
      (v) =>
        /^(\d{8})([A-Z])$/.test(v) ||
        /^([ABCDEFGHJKLMNPQRSUVW])(\d{7})([0-9A-J])$/.test(v) ||
        /^[XYZ]\d{7,8}[A-Z]$/.test(v) ||
        "NIF no válido",
    ],
    addressRules: [
      (v) =>
        !v ||
        v.length <= 200 ||
        "La dirección debe tener menos de 200 caracteres",
    ],
    provinceRules: [
      (v) =>
        !v ||
        v.length <= 20 ||
        "La provincia debe tener menos de 20 caracteres",
    ],
    phoneNumberRules: [
      (v) =>
        !v || v.length <= 15 || "El teléfono debe tener menos de 15 dígitos",
    ],
    accountingIdRules: [
      (v) =>
        !v ||
        v.length <= 20 ||
        "La cuenta contable debe tener menos de 20 caracteres",
    ],
  }),
  methods: {
    reset: function () {
      this.$refs.form.reset();
    },
    createDeleteAecoc: function () {
      if (this.form.switchAecoc) {
        this.form.customer.customerAecocInfo = {
          receiverCln: "",
          buyerCln: "",
          shipCln: "",
          payerCln: "",
          invoiceeCln: "",
        };
      } else {
        this.form.customer.customerAecocInfo = null;
      }
    },
  },
};
</script>