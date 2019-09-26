<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <CustomerForm v-bind:form="form"></CustomerForm>

      <div class="mb-3"></div>

      <CustomerPriceTable v-bind:productPrices="productPrices" :key="componentKey"></CustomerPriceTable>

      <div class="mb-10"></div>

      <v-btn :disabled="!form.valid" color="success" class="mr-4" @click="validate">Actualizar</v-btn>

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
    productPricesOriginal: [],
    snackbar: false,
    snackbarMessage: ""
  }),
  props: {
    customerHref: String
  },
  created() {
    this.$axios
      .get(this.customerHref)
      .then(response => {
        this.form.code = response.data.code.toString();
        this.form.name = response.data.name;
        this.form.alias = response.data.alias;
        this.form.email = response.data.email;
        this.form.idn = response.data.fiscalId;
        this.form.address = response.data.address;
        this.form.province = response.data.province;
        this.form.telephone = response.data.phoneNumber;
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
          .put(this.customerHref, customer)
          .then(response => {
            this.snackbar = true;
            this.snackbarMessage = "Cliente actualizado correctamente";
          })
          .catch(function(error) {
            console.log(error);
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
        console.log("A eliminar: ");
        console.log(productPricesToDelete);
        console.log("A insertar: ");
        console.log(productPricesToInsert);
        for (var i = 0; i < productPricesToDelete.length; i++) {
          this.$axios
            .delete(productPricesToDelete[i].productPriceHref)
            .then(response => {
              console.log("Precio eliminado");
            })
            .catch(function(error) {
              console.log(error);
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