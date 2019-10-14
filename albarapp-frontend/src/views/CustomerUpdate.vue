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
import CustomerProductPriceService from "@/services/CustomerProductPriceService.js";

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
    customerHref: String
  },
  async created() {
    this.form.customer = await CustomerService.get(this.customerHref);
    this.productPrices = await CustomerService.getCustomerProductPrices(
      this.customerHref
    );
    this.productPricesOriginal = Array.from(this.productPrices);
  },
  methods: {
    async updateCustomer() {
      var vm = this;
      if (this.form.valid) {
        await CustomerService.update(
          this.customerHref,
          this.form.customer
        ).then(() => {
          this.snackbar = true;
        });
        var productPricesToDelete = [];
        productPricesToDelete = this.productPricesOriginal.filter(
          f => !this.productPrices.includes(f)
        );
        var productPricesToInsert = [];
        productPricesToInsert = this.productPrices.filter(
          f => !this.productPricesOriginal.includes(f)
        );
        productPricesToDelete.forEach(element => {
          CustomerProductPriceService.delete(element.productPriceHref);
        });
        productPricesToInsert.forEach(element => {
          var customerProductPrice = {
            offeredPrice: element.price,
            customer: this.customerHref,
            product: element.product._links.self.href
          };
          CustomerProductPriceService.create(customerProductPrice);
        });
      }
    }
  }
};
</script>