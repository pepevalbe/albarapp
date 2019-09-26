<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <CustomerForm v-bind:form="form"></CustomerForm>

      <div class="mb-3"></div>

      <CustomerPriceTable v-bind:productPrices="productPrices" :key="componentKey"></CustomerPriceTable>

      <div class="mb-10"></div>

      <v-btn :disabled="!form.valid" color="success" class="mr-4" @click="validate">Crear</v-btn>

      <v-btn color="error" class="mr-4" @click="reset">Borrar</v-btn>

      <v-btn to="/customer-list/">Volver</v-btn>

      <v-snackbar v-model="snackbar">
        {{snackbarMessage}}
        <v-btn color="green" text @click="snackbar = false">Cerrar</v-btn>
      </v-snackbar>
    </v-container>
  </v-content>
</template>

<script>
import CustomerPriceTable from "@/components/CustomerPriceTable";
import CustomerForm from "@/components/CustomerForm";

export default {
  components: {
    CustomerPriceTable,
    CustomerForm
  },
  data: () => ({
    form: {
      valid: false,
      code: "",
      name: "",
      alias: "",
      email: "",
      idn: "",
      address: "",
      province: "",
      telephone: ""
    },
    componentKey: 0,
    products: [],
    product: undefined,
    price: 0,
    productPrices: [],
    snackbar: false,
    snackbarMessage: ""
  }),
  methods: {
    validate() {
      var vm = this;
      if (this.form.valid) {
        // Rest call to create new customer
        var customer = {
          fiscalId: this.form.idn,
          code: this.form.code,
          name: this.form.name,
          alias: this.form.alias,
          phoneNumber: this.form.telephone,
          email: this.form.email,
          address: this.form.address,
          province: this.form.province
        };
        this.$axios
          .post("/customers", customer)
          .then(response => {
            for (var i = 0; i < vm.productPrices.length; i++) {
              var item = vm.productPrices[i];
              var customerProductPrice = {
                offeredPrice: item.price,
                customer: response.data._links.self.href,
                product: item.product._links.self.href
              };
              this.$axios
                .post("/customerProductPrices", customerProductPrice)
                .then(response => {})
                .catch(function(error) {
                  alert("Ha ocurrido un error creando los precios");
                });
            }
            this.snackbar = true;
            this.snackbarMessage = "Cliente creado correctamente";
            this.reset();
          })
          .catch(function(error) {
            alert("Ha ocurrido un error creando el cliente");
          });
      }
    },
    reset() {
      this.form = {
        valid: false,
        code: "",
        name: "",
        alias: "",
        email: "",
        idn: "",
        address: "",
        province: "",
        telephone: ""
      };
      this.componentKey += 1;
      this.productPrices = [];
    }
  }
};
</script>