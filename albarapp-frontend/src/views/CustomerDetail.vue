<template>
  <v-flex align-self-start>
    <CustomerForm :form="form" :readonly="true"></CustomerForm>
    <div class="mb-3"></div>
    <CustomerPriceTable
      v-if="form.customer.customerProductPrices"
      :customerProductPrices="form.customer.customerProductPrices"
      :readonly="true"
    ></CustomerPriceTable>
    <div class="mb-10"></div>
    <v-layout text-center wrap>
      <v-flex xs12>
        <v-btn to="/customer-list/">Volver</v-btn>
      </v-flex>
    </v-layout>
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
  name: "CustomerDetail",
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
        phoneNumber: "",
        customerProductPrices: []
      }
    },

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
    this.$set(
      this.form.customer,
      "customerProductPrices",
      await CustomerService.getCustomerProductPrices(this.customerId)
    );
    this.closeSpinner();
  }
};
</script>