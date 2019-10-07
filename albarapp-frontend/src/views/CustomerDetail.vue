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
    productPricesOriginal: []
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
  }
};
</script>