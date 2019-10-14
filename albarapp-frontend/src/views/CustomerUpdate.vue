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
    customerHref: String
  },
  created() {
    this.$axios
      .get(this.customerHref)
      .then(response => {
        this.form.customer = response.data;
        this.$axios
          .get(response.data._links.customerProductPrices.href)
          .then(response => {
            for (
              var i = 0;
              i < response.data._embedded.customerProductPrices.length;
              i++
            ) {
              var productPrice = {
                product: {},
                price:
                  response.data._embedded.customerProductPrices[i].offeredPrice,
                productPriceHref:
                  response.data._embedded.customerProductPrices[i]._links.self
                    .href,
                productHref:
                  response.data._embedded.customerProductPrices[i]._links
                    .product.href
              };
              this.productPrices.push(productPrice);
              this.productPricesOriginal.push(productPrice);
              this.$axios
                .get(
                  response.data._embedded.customerProductPrices[i]._links
                    .product.href
                )
                .then(responseProduct => {
                  var index = this.productPrices.findIndex(function(element) {
                    return element.productHref === responseProduct.config.url;
                  });
                  this.productPrices[index].product = responseProduct.data;
                  this.productPricesOriginal[index].product =
                    responseProduct.data;
                });
            }
          });
      })
      .catch(function(error) {
        alert("Ha ocurrido un error recuperando los datos del cliente");
      });
  },
  methods: {
    updateCustomer() {
      var vm = this;
      if (this.form.valid) {
        this.$axios
          .put(this.customerHref, this.form.customer)
          .then(response => {
            this.snackbar = true;
          })
          .catch(function(error) {
            alert("Ha ocurrido un error creando el cliente");
          });
        var productPricesToDelete = [];
        productPricesToDelete = this.productPricesOriginal.filter(
          f => !this.productPrices.includes(f)
        );
        var productPricesToInsert = [];
        productPricesToInsert = this.productPrices.filter(
          f => !this.productPricesOriginal.includes(f)
        );
        for (var i = 0; i < productPricesToDelete.length; i++) {
          this.$axios
            .delete(productPricesToDelete[i].productPriceHref)
            .then(response => {})
            .catch(function(error) {
              alert("Ha ocurrido un error creando el cliente");
            });
        }
        for (var i = 0; i < productPricesToInsert.length; i++) {
          var item = productPricesToInsert[i];
          var customerProductPrice = {
            offeredPrice: item.price,
            customer: this.customerHref,
            product: item.product._links.self.href
          };
          this.$axios
            .post("/customerProductPrices", customerProductPrice)
            .then(response => {})
            .catch(function(error) {
              alert("Ha ocurrido un error creando los precios");
            });
        }
      }
    }
  }
};
</script>