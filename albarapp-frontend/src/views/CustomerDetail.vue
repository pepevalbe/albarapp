<template>
  <v-flex align-self-start>
    <CustomerForm :form="form" :readonly="true"></CustomerForm>
    <div class="mb-3"></div>
    <CustomerPriceTable :productPrices="productPrices" :readonly="true"></CustomerPriceTable>
    <div class="mb-10"></div>
    <v-layout text-center wrap>
      <v-flex xs12>
        <v-btn to="/customer-list/">Volver</v-btn>
      </v-flex>
    </v-layout>
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
        phoneNumber: ""
      }
    },
    productPrices: []
  }),
  props: {
    customerId: String
  },
  async created() {
    this.form.customer = await CustomerService.get(this.customerId);
    this.productPrices = await CustomerService.getCustomerProductPrices(
      this.customerId
    );
  }
};
</script>