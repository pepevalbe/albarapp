<template>
  <v-content>
    <v-container class="pl-10 pr-10">
      <v-form ref="form" v-model="valid">
        <v-text-field
          v-model="code"
          type="number"
          :counter="5"
          :rules="codeRules"
          label="Código *"
          required
        ></v-text-field>

        <v-text-field v-model="alias" :counter="20" :rules="aliasRules" label="Alias *" required></v-text-field>

        <v-text-field v-model="name" :counter="40" :rules="nameRules" label="Nombre *" required></v-text-field>

        <v-text-field v-model="email" :rules="emailRules" label="E-mail"></v-text-field>

        <v-text-field v-model="idn" :rules="idnRules" label="NIF *" required></v-text-field>

        <v-text-field v-model="address" :counter="200" :rules="addressRules" label="Dirección"></v-text-field>

        <v-text-field v-model="province" :counter="20" :rules="provinceRules" label="Provincia"></v-text-field>

        <v-text-field
          v-model="telephone"
          type="number"
          :counter="15"
          :rules="telephoneRules"
          label="Teléfono"
        ></v-text-field>

        <div class="mb-3"></div>

        <v-divider></v-divider>

        <v-subheader class="title ml-1">Precios de productos</v-subheader>
        <v-row class="ml-5" justify="center">
          <v-alert
            v-if="sameProduct()"
            type="error"
          >No puedes introducir dos precios para el mismo producto.</v-alert>
        </v-row>
        <v-row class="ml-5" justify="center">
          <v-col cols="12" md="3">
            <v-select
              v-model="product"
              :items="products"
              item-text="name"
              item-value="id"
              label="Producto"
              return-object
            ></v-select>
          </v-col>
          <v-col cols="12" md="1">
            <v-text-field v-model="price" type="number" label="Precio" suffix="€"></v-text-field>
          </v-col>
          <v-col cols="12" md="1" align-self="center">
            <v-btn :disabled="!price || !product || sameProduct()" @click="addPrice">Añadir</v-btn>
          </v-col>
        </v-row>
        <v-row class="ml-5" justify="center">
          <v-col cols="12" md="5">
            <v-simple-table>
              <template v-slot:default>
                <thead>
                  <tr>
                    <th class="text-left">Producto</th>
                    <th class="text-left">Precio</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in productPrices" :key="item.productId">
                    <td>{{ item.product.name }}</td>
                    <td>{{ item.price }} €</td>
                  </tr>
                </tbody>
              </template>
            </v-simple-table>
          </v-col>
        </v-row>

        <div class="mb-10"></div>

        <v-btn :disabled="!valid" color="success" class="mr-4" @click="validate">Crear</v-btn>

        <v-btn color="error" class="mr-4" @click="reset">Cancelar</v-btn>

        <v-btn to="/customer/">Volver</v-btn>
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
    alias: "",
    aliasRules: [
      v => !!v || "El alias es obligatorio",
      v => (v && v.length <= 20) || "El alias debe tener menos de 20 caracteres"
    ],
    email: "",
    emailRules: [v => !v || /.+@.+\..+/.test(v) || "E-mail no válido"],
    idn: "",
    idnRules: [
      v => !!v || "El NIF es obligatorio",
      v =>
        /^(\d{8})([A-Z])$/.test(v) ||
        /^([ABCDEFGHJKLMNPQRSUVW])(\d{7})([0-9A-J])$/.test(v) ||
        /^[XYZ]\d{7,8}[A-Z]$/.test(v) ||
        "NIF no válido"
    ],
    address: "",
    addressRules: [
      v =>
        !v ||
        v.length <= 200 ||
        "La dirección debe tener menos de 200 caracteres"
    ],
    province: "",
    provinceRules: [
      v =>
        !v || v.length <= 20 || "La provincia debe tener menos de 20 caracteres"
    ],
    telephone: "",
    telephoneRules: [
      v => !v || v.length <= 15 || "El teléfono debe tener menos de 15 dígitos"
    ],
    products: [],
    product: undefined,
    price: 0,
    productPrices: []
  }),
  created() {
    this.listProducts();
  },
  methods: {
    validate() {
      if (this.$refs.form.validate()) {
        // Rest call to create new customer
        var customer = {
          "fiscalId": this.idn,
          "code": this.code,
          "name": this.name,
          "alias": this.alias,
          "phoneNumber": this.telephone,
          "email": this.email,
          "address": this.address,
          "province": this.province,
        }
        this.$axios
        .post('/customers', customer)
        .then(response => {
          alert('Se ha creado el cliente correctamente')
          this.reset()
        })
        .catch(function (error) {
          alert('Ha ocurrido un error creando el cliente')
        })
      }
    },
    reset() {
      this.$refs.form.reset();
      this.productPrices = [];
    },
    listProducts() {
      // Rest call to list products
      this.$axios
      .get('/products')
      .then(response => {
        this.products = response.data._embedded.products 
      })
      .catch(function (error) {
        alert('Ha ocurrido un error recuperando los productos')
      })
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
      return vm.product && vm.productPrices.some(
        product => product.product.id === vm.product.id
      );
    }
  }
};
</script>