<template>
  <v-flex align-self-start>
    <CustomerForm :form="form" ref="form"></CustomerForm>
    <div class="mb-3"></div>
    <CustomerPriceTable
      v-if="form.customer.customerProductPrices"
      :customerProductPrices="form.customer.customerProductPrices"
    ></CustomerPriceTable>
    <div class="mb-10"></div>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
        <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>
        <v-btn :disabled="!form.valid" color="success" class="mr-4" @click="createCustomer()">Crear</v-btn>
      </v-flex>
    </v-layout>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{snackbar.message}}
      <v-btn text @click="snackbar.show=false">Cerrar</v-btn>
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
  name: "CustomerCreation",
  components: {
    CustomerPriceTable,
    CustomerForm,
  },
  data: () => ({
    form: {
      valid: false,
      switchAecoc: false,
      customer: {
        code: "",
        name: "",
        alias: "",
        email: "",
        fiscalId: "",
        address: "",
        province: "",
        phoneNumber: "",
        customerAecocInfo: null,
        customerProductPrices: [],
      },
    },
    snackbar: {
      show: false,
      message: "",
      color: "",
    },
    spinner: {
      loading: false,
      counter: 0,
    },
  }),
  methods: {
    async createCustomer() {
      try {
        this.showSpinner();
        await CustomerService.create(this.form.customer);
        this.snackbar = {
          show: true,
          message: "Cliente creado correctamente",
          color: "success",
        };
        this.reset();
      } catch (e) {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido crear el cliente, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    reset() {
      this.$refs.form.reset();
      this.form.customer.customerProductPrices = [];
    },
  },
};
</script>