<template>
  <v-flex align-self-start>
    <div v-if="!errorLoading">
      <CustomerForm :form="form" v-if="form.customer"></CustomerForm>
      <div class="mb-3"></div>
      <CustomerPriceTable
        v-if="form.customer && form.customer.customerProductPrices"
        :customerProductPrices="form.customer.customerProductPrices"
      ></CustomerPriceTable>
      <div class="mb-10"></div>
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
          <v-btn
            :disabled="!form.valid"
            color="success"
            class="mr-4"
            @click="updateCustomer()"
            >Actualizar</v-btn
          >
        </v-flex>
      </v-layout>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener el cliente, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadCustomer()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{ snackbar.message }}
      <v-btn text @click="snackbar.show = false">Cerrar</v-btn>
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
import ProductService from "@/services/ProductService.js";

export default {
  name: "CustomerUpdate",
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
    products: [],
    errorLoading: false,
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
  props: {
    customerId: String,
  },
  async created() {
    await this.loadProducts();
    this.loadCustomer();
  },
  methods: {
    async loadProducts() {
      this.products = await ProductService.getAll();
    },
    async loadCustomer() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.form.customer = await CustomerService.get(this.customerId);
        if (this.form.customer.customerAecocInfo) this.form.switchAecoc = true;
        for (let cpp of this.form.customer.customerProductPrices) {
          cpp.product = this.products.find(
            (product) => product.id === cpp.productId
          );
        }
        /*this.$set(
          this.form.customer,
          "customerProductPrices",
          await CustomerService.getCustomerProductPrices(this.customerId)
        );*/
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async updateCustomer() {
      try {
        this.showSpinner();
        await CustomerService.update(this.customerId, this.form.customer);
        this.loadCustomer();
        this.snackbar = {
          show: true,
          message: "Cliente actualizado correctamente",
          color: "success",
        };
      } catch (e) {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido modificar el cliente, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>