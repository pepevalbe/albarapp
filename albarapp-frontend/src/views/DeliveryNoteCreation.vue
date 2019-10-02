<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-row>
        <v-col cols="12" md="2">
          <v-text-field
            ref="customerCode"
            v-model="customerCode"
            type="number"
            :counter="5"
            label="Código cliente"
            required
            autofocus
            @focus="$event.target.select()"
            v-on:blur="selectCustomerByCode()"
            v-on:input="clearCustomer()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="7">
          <v-autocomplete
            v-model="customer"
            label="Alias cliente"
            :items="customers"
            item-text="alias"
            return-object
            no-data-text="Sin coincidencias"
            v-on:blur="selectCustomerByAlias()"
            v-on:change="selectCustomerByAlias()"
          ></v-autocomplete>
        </v-col>
        <v-col cols="12" md="2">
          <v-menu
            ref="menu1"
            v-model="menu1"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            max-width="290px"
            min-width="290px"
          >
            <template v-slot:activator="{ on }">
              <v-text-field
                v-model="dateFormatted"
                ref="dateText"
                label="Fecha"
                hint="Formato: ddMMaaaa"
                persistent-hint
                @focus="$event.target.select()"
                prepend-icon="mdi-calendar"
                @blur="parseDateText()"
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
              v-model="date"
              no-title
              @input="parseDatePick()"
              locale="es-ES"
              first-day-of-week="1"
            ></v-date-picker>
          </v-menu>
        </v-col>
        <v-col cols="12" md="1">
          <v-text-field
            ref="auxDeliveryNoteNr"
            v-model="auxDeliveryNoteNr"
            type="number"
            label="Nº  Albarán auxiliar"
            @focus="$event.target.select()"
          ></v-text-field>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="2">
          <v-text-field
            ref="quantity"
            v-model="quantity"
            type="number"
            label="Cantidad"
            @focus="$event.target.select()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="2">
          <v-text-field
            ref="productCode"
            v-model="productCode"
            type="number"
            label="Código de producto"
            @focus="$event.target.select()"
            v-on:blur="selectProductByCode()"
            v-on:input="clearProduct()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="5">
          <v-autocomplete
            v-model="product"
            label="Producto"
            :items="products"
            item-text="name"
            return-object
            no-data-text="Sin coincidencias"
            v-on:blur="selectProductByName()"
            v-on:change="selectProductByName()"
          ></v-autocomplete>
        </v-col>
        <v-col cols="12" md="2">
          <v-text-field
            ref="price"
            v-model="price"
            type="number"
            label="Precio"
            @focus="$event.target.select()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="1">
          <v-flex text-xs-center align-center>
            <v-btn @click="addDeliveryNoteItem()">
              <span>Añadir línea</span>
            </v-btn>
          </v-flex>
        </v-col>
      </v-row>
    </v-form>
    <v-row class="ml-5" justify="center">
      <v-col cols="12" md="5">
        <v-simple-table>
          <template v-slot:default>
            <thead>
              <tr>
                <th></th>
                <th></th>
                <th></th>
                <th></th>
                <th>Albarán:</th>
                <th>{{ auxDeliveryNoteNr }}</th>
              </tr>
              <tr>
                <th class="text-left">Cantidad</th>
                <th class="text-left">Producto</th>
                <th class="text-left">Precio</th>
                <th class="text-left">Total bruto</th>
                <th class="text-left">Tipo impositivo</th>
                <th class="text-left">Total neto</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in deliveryNoteItems" :key="item.itemNumber">
                <td>{{ item.quantity }}</td>
                <td>{{ item.productName }}</td>
                <td>{{ item.price }}</td>
                <td>{{ item.gross.toFixed(2) }} €</td>
                <td>{{ item.taxRate }} %</td>
                <td>{{ item.net.toFixed(2) }} €</td>
              </tr>
              <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td>Total: {{deliveryNoteTotal.toFixed(2)}} €</td>
              </tr>
            </tbody>
          </template>
        </v-simple-table>
      </v-col>
    </v-row>
    <div class="mb-10"></div>
    <v-row class="ml-5" justify="center">
      <v-btn
        color="success"
        ref="createbutton"
        class="mr-4"
        @click="createDeliveryNote()"
        @keyup.left="backToQuantity()"
      >Guardar</v-btn>

      <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>

      <v-btn to="/delivery-note-list/">Volver</v-btn>
    </v-row>
    <v-snackbar v-model="snackbar">
      {{snackbarMessage}}
      <v-btn :color="snackbarColor" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
  </v-container>
</template>
<script>
export default {
  data: () => ({
    form: {
      valid: false
    },
    customerCode: "",
    customers: [],
    customer: {},
    auxDeliveryNoteNr: "",
    products: [],
    product: {},
    productCode: "",
    quantity: "",
    price: "",
    menu1: false,
    date: "",
    dateFormatted: "",
    deliveryNoteItems: [],
    deliveryNoteTotal: 0,
    snackbar: false,
    snackbarMessage: "",
    snackbarColor: ""
  }),
  created() {
    this.listCustomers();
    this.listProducts();
  },
  methods: {
    listCustomers() {
      this.$axios
        .get("/customers")
        .then(response => {
          this.customers = response.data._embedded.customers;
        })
        .catch(function(error) {
          alert("Ha ocurrido un error recuperando los clientes");
        });
    },
    listProducts() {
      this.$axios
        .get("/products")
        .then(response => {
          this.products = response.data._embedded.products;
        })
        .catch(function(error) {
          alert("Ha ocurrido un error recuperando los clientes");
        });
    },
    selectCustomerByCode() {
      var vm = this;
      if (this.customerCode != "" && this.customerCode != null) {
        var index = this.customers.findIndex(function(element) {
          return element.code == vm.customerCode;
        });
        if (index === -1) {
          this.snackbar = true;
          this.snackbarMessage = "No existe ningún cliente con ese código";
          this.snackbarColor = "error";
          this.$nextTick(this.$refs.customerCode.focus);
        } else {
          this.customer = this.customers[index];
          this.listCustomerPrices();
          this.$nextTick(this.$refs.dateText.focus);
        }
      }
    },
    clearCustomer() {
      this.customer = {};
    },
    listCustomerPrices() {
      var vm = this;
      this.$axios
        .get(this.customer._links.customerProductPrices.href)
        .then(response => {
          this.customerPrices = response.data._embedded.customerProductPrices;
          this.customerPrices.forEach(function(item) {
            vm.$axios
              .get(item._links.product.href)
              .then(responseProduct => {
                var index = vm.customerPrices.findIndex(function(element) {
                  return (
                    element._links.product.href === responseProduct.config.url
                  );
                });
                vm.customerPrices[index].productCode =
                  responseProduct.data.code;
              })
              .catch(function(error) {
                alert(
                  "Ha ocurrido un error recuperando los productos del precio: " +
                    error
                );
              });
          });
        })
        .catch(function(error) {
          alert("Ha ocurrido un error recuperando los precios");
        });
    },
    selectCustomerByAlias() {
      if (
        this.customer != {} &&
        this.customer != null &&
        this.customer != undefined
      ) {
        this.customerCode = this.customer.code;
        this.listCustomerPrices();
      }
      this.$nextTick(this.$refs.dateText.focus);
    },
    selectProductByCode() {
      var vm = this;
      if (this.productCode != "" && this.productCode != null) {
        var index = this.products.findIndex(function(element) {
          return element.code == vm.productCode;
        });
        if (index === -1) {
          this.snackbar = true;
          this.snackbarMessage = "No existe ningún producto con ese código";
          this.snackbarColor = "error";
          this.$nextTick(this.$refs.productCode.focus);
        } else {
          this.product = this.products[index];
          this.selectPrice();
          this.$nextTick(this.$refs.price.focus);
        }
      }
    },
    clearProduct() {
      this.product = {};
    },
    selectProductByName() {
      if (
        this.product != {} &&
        this.product != null &&
        this.product != undefined
      ) {
        this.productCode = this.product.code;
        this.selectPrice();
        this.$nextTick(this.$refs.price.focus);
      }
    },
    selectPrice() {
      var vm = this;
      var index = this.customerPrices.findIndex(function(element) {
        return element.productCode == vm.productCode;
      });
      if (index === -1) {
        this.price = this.product.factoryPrice;
      } else {
        this.price = this.customerPrices[index].offeredPrice;
      }
    },
    addDeliveryNoteItem() {
      var itemNet = this.quantity * this.price * (1 + this.product.tax / 100);
      this.deliveryNoteItems.push({
        quantity: this.quantity,
        productName: this.product.name,
        product: this.product,
        price: this.price,
        gross: this.quantity * this.price,
        taxRate: this.product.tax,
        net: this.quantity * this.price * (1 + this.product.tax / 100)
      });

      this.deliveryNoteTotal += itemNet;

      this.quantity = "";
      this.product = {};
      (this.productCode = ""), (this.price = "");
      this.$nextTick(() => this.$refs.createbutton.$el.focus());
    },
    backToQuantity() {
      this.$nextTick(this.$refs.quantity.focus);
    },
    reset() {
      this.customer = {};
      this.customerCode = "";
      this.auxDeliveryNoteNr = "";
      this.quantity = "";
      this.product = {};
      this.productCode = "";
      this.price = "";
      this.deliveryNoteItems = [];
      this.deliveryNoteTotal = 0;
      this.$nextTick(this.$refs.customerCode.focus);
    },
    createDeliveryNote() {
      var vm = this;
      var promises = [];
      var issuedTimestamp = new Date();
      issuedTimestamp.setDate(this.date.substring(8, 10));
      issuedTimestamp.setMonth(this.date.substring(5, 7) - 1);
      issuedTimestamp.setFullYear(this.date.substring(0, 4));
      // Rest call to create new deliveryNote
      var deliveryNote = {
        auxDeliveryNoteNumber: this.auxDeliveryNoteNr,
        issuedTimestamp: issuedTimestamp.getTime(),
        customer: this.customer._links.self.href
      };

      this.$axios.post("/deliveryNotes", deliveryNote).then(response => {
        for (var i = 0; i < vm.deliveryNoteItems.length; i++) {
          var item = vm.deliveryNoteItems[i];
          var deliveryNoteItem = {
            quantity: item.quantity,
            price: item.price,
            product: item.product._links.self.href,
            deliveryNote: response.data._links.self.href
          };
          promises.push(
            this.$axios.post("/deliveryNoteItems", deliveryNoteItem)
          );
        }
      });
      this.snackbar = true;
      this.snackbarColor = "success";
      this.snackbarMessage = "Albarán creado correctamente";
      Promise.all(promises).then(function(values) {
        this.reset();
      });
    },
    parseDateText() {
      var date = "";
      var day = "";
      var month = "";
      var year = "";
      switch (this.dateFormatted.length) {
        case 8:
          if (!isNaN(this.dateFormatted)) {
            // ddMMyyyy
            day = this.dateFormatted.substring(0, 2);
            month = this.dateFormatted.substring(2, 4);
            year = this.dateFormatted.substring(4, 8);
            date = year + "-" + month + "-" + day;
            this.dateFormatted = day + "/" + month + "/" + year;
          } else {
            // dd-MM-yy
            his.$nextTick(this.$refs.dateText.focus);
          }
          break;
        case 0:
          break;
        default:
          this.$nextTick(this.$refs.dateText.focus);
      }
      this.date = date;
    },
    parseDatePick() {
      var day = this.date.substring(8, 10);
      var month = this.date.substring(5, 7);
      var year = this.date.substring(0, 4);
      this.dateFormatted = day + "/" + month + "/" + year;
    }
  }
};
</script>