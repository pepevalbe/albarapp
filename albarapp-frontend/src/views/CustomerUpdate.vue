<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <CustomerForm :form="form"></CustomerForm>
      <div class="mb-3"></div>
      <CustomerPriceTable :productPrices="productPrices"></CustomerPriceTable>
      <div class="mb-10"></div>
      <v-btn
        :disabled="!form.valid"
        color="success"
        class="mr-4"
        @click="updateCustomer()"
      >Actualizar</v-btn>
      <v-btn to="/customer-list/">Volver</v-btn>
      <v-snackbar v-model="snackbar">
        Cliente actualizado correctamente
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
    productPrices: [],
    productPricesOriginal: [],
    snackbar: false
  }),
  props: {
    customerId: String
  },
  async created() {
    this.form.customer = await CustomerService.get(this.customerId);
    this.productPrices = await CustomerService.getCustomerProductPrices(
      this.customerId
    );
    this.productPricesOriginal = Array.from(this.productPrices);
  },
  methods: {
    async updateCustomer() {
      await CustomerService.update(
        this.customerId,
        this.form.customer,
        this.productPrices,
        this.productPricesOriginal
      );
      this.snackbar = true;
    }
  }
};
</script>