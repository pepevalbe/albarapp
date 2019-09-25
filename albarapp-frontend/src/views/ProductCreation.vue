<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <v-form ref="form" v-model="valid">
        <v-subheader class="title ml-1">Datos de producto</v-subheader>
        <v-divider></v-divider>
        <v-text-field
          v-model="code"
          type="number"
          :counter="5"
          :rules="codeRules"
          label="Código *"
          required
        ></v-text-field>
        <v-text-field v-model="name" :counter="40" :rules="nameRules" label="Nombre *" required></v-text-field>
        <v-text-field
          v-model="factoryPrice"
          :rules="factoryPriceRules"
          label="Precio estándar *"
          required
        ></v-text-field>
        <v-text-field
          v-model="tax"
          :counter="200"
          :rules="taxRules"
          label="Tipo impositivo *"
          required
        ></v-text-field>
        <div class="mb-3"></div>
        <div class="mb-10"></div>
        <v-btn :disabled="!valid" color="success" class="mr-4" @click="validate">Crear</v-btn>
        <v-btn color="error" class="mr-4" @click="reset">Cancelar</v-btn>
        <v-btn to="/products/">Volver</v-btn>
      </v-form>
    </v-container>
  </v-content>
</template>

<script>
export default {
  data: () => ({
    valid: false,
    code: "",
    codeRules: [
      v => !!v || "El código es obligatorio",
      v => (v && v.length <= 5) || "El nombre debe tener un máximo de 5 dígitos"
    ],
    name: "",
    nameRules: [
      v => !!v || "El nombre es obligatorio",
      v =>
        (v && v.length <= 40) || "El nombre debe tener menos de 40 caracteres"
    ],
    factoryPrice: 0,
    factoryPriceRules: [
      v => !!v || "El precio es obligatorio",
      v => (v && v.length <= 20) || "El precio debe tener menos de 20 caracteres"
    ],
    tax: 0,
    taxRules: [v => !v || /.+@.+\..+/.test(v) || "Tipo impositivo no válido"],
    products: [],
    product: undefined
  }),
  created() {
    this.listProducts();
  },
  methods: {
    validate() {
      var vm = this;
      if (this.$refs.form.validate()) {
        // Rest call to create new customer
        var customer = {
          fiscalId: this.idn,
          code: this.code,
          name: this.name,
          alias: this.alias,
          phoneNumber: this.telephone,
          email: this.email,
          address: this.address,
          province: this.province
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
                .then(response => {
                  alert("Se ha creado el precio correctamente");
                })
                .catch(function(error) {
                  alert("Ha ocurrido un error creando los precios");
                });
            }
            alert("Se ha creado el cliente correctamente");
            this.reset();
          })
          .catch(function(error) {
            alert("Ha ocurrido un error creando el cliente");
          });
      }
    },
    reset() {
      this.$refs.form.reset();
      this.productPrices = [];
    },
    listProducts() {
      // Rest call to list products
      this.$axios
        .get("/products")
        .then(response => {
          this.products = response.data._embedded.products;
        })
        .catch(function(error) {
          alert("Ha ocurrido un error recuperando los productos");
        });
    },
    addPrice() {
      var vm = this;
      if (!this.sameProduct()) {
        this.productPrices.push({
          product: vm.product,
          price: vm.price
        });
        vm.product = undefined;
        vm.price = 0;
      }
    },
    sameProduct() {
      var vm = this;
      return (
        vm.product &&
        vm.productPrices.some(
          product => product.product.code === vm.product.code
        )
      );
    },
    removePrice(price) {
      this.productPrices = this.productPrices.filter(function(item) {
        return item != price;
      });
    }
  }
};
</script>