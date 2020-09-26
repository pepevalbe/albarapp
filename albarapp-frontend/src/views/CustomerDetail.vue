<template>
  <v-flex align-self-start>
    <div v-if="!errorLoading">
      <CustomerForm :form="form" v-if="form.customer" :readonly="true"></CustomerForm>
      <div class="mb-3"></div>
      <CustomerPriceTable
        v-if="form.customer && form.customer.customerProductPrices"
        :customerProductPrices="form.customer.customerProductPrices"
        :readonly="true"
      ></CustomerPriceTable>
      <div class="mb-10"></div>
      <v-layout text-center wrap>
        <v-flex xs12>
          <v-btn @click="$router.back()">Volver</v-btn>
        </v-flex>
      </v-layout>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center">Error al obtener el cliente, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="loadCustomer()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
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
    CustomerPriceTable,
  },
  data: () => ({
    form: {
      valid: false,
      switchAecoc: false,
      customer: null,
    },
    errorLoading: false,
    spinner: {
      loading: false,
      counter: 0,
    },
  }),
  props: {
    customerId: String,
  },
  async created() {
    this.loadCustomer();
  },
  methods: {
    async loadCustomer() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.form.customer = await CustomerService.get(this.customerId);
        if (this.form.customer.customerAecocInfo) this.form.switchAecoc = true;
        this.$set(
          this.form.customer,
          "customerProductPrices",
          await CustomerService.getCustomerProductPrices(this.customerId)
        );
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>