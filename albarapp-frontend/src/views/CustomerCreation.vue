<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <CustomerForm v-bind:form="form" ref="form"></CustomerForm>
      <div class="mb-3"></div>
      <CustomerPriceTable v-bind:productPrices="productPrices"></CustomerPriceTable>
      <div class="mb-10"></div>
      <v-btn :disabled="!form.valid" color="success" class="mr-4" @click="createCustomer()">Crear</v-btn>
      <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>
      <v-btn to="/customer-list/">Volver</v-btn>
      <v-snackbar v-model="snackbar">
        Cliente creado correctamente
        <v-btn color="green" text @click="snackbar = false">Cerrar</v-btn>
      </v-snackbar>
    </v-container>
  </v-content>
</template>

<script>
import CustomerForm from "@/components/CustomerForm";
import CustomerPriceTable from "@/components/CustomerPriceTable";
import CustomerService from "@/services/CustomerService.js";

export default {
  components: {
    CustomerPriceTable,
    CustomerForm
  },
  data: () => ({
    form: {
      valid: false,
      customer: {
        code: "",
        name: "",
        alias: "",
        email: "",
        fiscalId: "",
        address: "",
        province: "",
        phoneNumber: ""
      }
    },
    productPrices: [],
    snackbar: false
  }),
  methods: {
    async createCustomer() {
      await CustomerService.create(this.form.customer, this.productPrices);
      this.snackbar = true;
      this.reset();
    },
    reset() {
      this.$refs.form.reset();
      this.productPrices = [];
    }
  }
};
</script>