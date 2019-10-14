<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <CustomerForm :form="form" :readonly="true"></CustomerForm>
      <div class="mb-3"></div>
      <CustomerPriceTable :productPrices="productPrices" :readonly="true"></CustomerPriceTable>
      <div class="mb-10"></div>
      <v-btn to="/customer-list/">Volver</v-btn>
    </v-container>
  </v-content>
</template>

<script>
import CustomerForm from "@/components/CustomerForm";
import CustomerPriceTable from "@/components/CustomerPriceTable";
import CustomerService from "@/services/CustomerService.js";

export default {
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
    productPrices: []
  }),
  props: {
    customerHref: String
  },
  async created() {
    this.form.customer = await CustomerService.get(this.customerHref);
    this.productPrices = await CustomerService.getCustomerProductPrices(
      this.customerHref
    );
  }
};
</script>