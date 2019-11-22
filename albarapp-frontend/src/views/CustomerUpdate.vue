<template>
  <v-flex align-self-start>
    <CustomerForm :form="form"></CustomerForm>
    <div class="mb-3"></div>
    <CustomerPriceTable :customerProductPrices="customerProductPrices"></CustomerPriceTable>
    <div class="mb-10"></div>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn class="mr-4" to="/customer-list/">Volver</v-btn>
        <v-btn
          :disabled="!form.valid"
          color="success"
          class="mr-4"
          @click="updateCustomer()"
        >Actualizar</v-btn>
      </v-flex>
    </v-layout>
    <v-snackbar v-model="snackbar">
      Cliente actualizado correctamente
      <v-btn color="green" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-flex>
</template>

<script>
import CustomerForm from "@/components/CustomerForm";
import CustomerPriceTable from "@/components/CustomerPriceTable";
import CustomerService from "@/services/CustomerService.js";

export default {
  name: "CustomerUpdate",
  components: {
    CustomerForm,
    CustomerPriceTable
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
    customerProductPrices: [],
    productPricesOriginal: [],
    snackbar: false,
    spinner: {
      loading: false,
      counter: 0
    }
  }),
  props: {
    customerId: String
  },
  async created() {
    this.showSpinner();
    this.form.customer = await CustomerService.get(this.customerId);
    this.customerProductPrices = await CustomerService.getCustomerProductPrices(
      this.customerId
    );
    this.closeSpinner();
  },
  methods: {
    async updateCustomer() {
      this.form.customer.customerProductPrices = this.customerProductPrices;
      this.showSpinner();
      await CustomerService.update(this.customerId, this.form.customer);
      this.closeSpinner();
      this.snackbar = true;
      this.customerProductPrices = await CustomerService.getCustomerProductPrices(
        this.customerId
      );
    }
  }
};
</script>