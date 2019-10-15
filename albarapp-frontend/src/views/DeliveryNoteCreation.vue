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
            :rules="customerCodeRules"
            required
            autofocus
            @focus="$event.target.select()"
            v-on:blur="selectCustomerByCode()"
            v-on:input="clearCustomer()"
            v-on:keypress.enter="moveToDate()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="6">
          <v-autocomplete
            v-model="customer"
            label="Alias cliente"
            :items="customers"
            item-text="alias"
            return-object
            no-data-text="Sin coincidencias"
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
                v-on:keypress.enter="moveToAuxDeliveryNoteNr()"
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
        <v-col cols="12" md="2">
          <v-text-field
            ref="auxDeliveryNoteNr"
            v-model="auxDeliveryNoteNr"
            type="number"
            label="Nº  Albarán auxiliar"
            @focus="$event.target.select()"
            v-on:keypress.enter="moveToQuantity()"
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
            v-on:keypress.enter="moveToProductCode()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="2">
          <v-text-field
            ref="productCode"
            v-model="productCode"
            type="number"
            label="Código de producto"
            @focus="$event.target.select()"
            v-on:keypress.enter="selectProductByCode()"
            v-on:blur="selectProductByCode()"
            v-on:input="clearProduct()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="4">
          <v-autocomplete
            v-model="product"
            label="Producto"
            :items="products"
            item-text="name"
            return-object
            no-data-text="Sin coincidencias"
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
            v-on:keypress.enter="moveToAddLine()"
          ></v-text-field>
        </v-col>
        <v-col cols="12" md="2">
          <v-flex text-xs-center align-center>
            <v-btn
              @click="addDeliveryNoteItem()"
              ref="addLineButton"
              v-bind:disabled="!noteItemToAdd()"
            >
              <span>Añadir línea</span>
            </v-btn>
          </v-flex>
        </v-col>
      </v-row>

      <v-row class="ml-5" justify="center">
        <v-col cols="12" md="8">
          <DeliveryNoteItemTable
            :deliveryNoteItems="deliveryNoteItems"
            :deliveryNoteTotal="deliveryNoteTotal"
          ></DeliveryNoteItemTable>
        </v-col>
      </v-row>
      <div class="mb-10"></div>
      <v-row class="ml-5" justify="center">
        <v-btn
          ref="createbutton"
          class="mr-4"
          :disabled="!deliveryNoteValid()"
          @click="createDeliveryNote()"
          @keyup.left="moveToQuantity()"
        >Guardar</v-btn>

        <v-btn color="error" class="mr-4" @click="reset()">Borrar</v-btn>

        <v-btn to="/delivery-note-list/">Volver</v-btn>
      </v-row>
    </v-form>
    <v-snackbar v-model="snackbar">
      {{snackbarMessage}}
      <v-btn :color="snackbarColor" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
  </v-container>
</template>
<script>
import DeliveryNoteItemTable from "@/components/DeliveryNoteItemTable";

export default {
  components: {
    DeliveryNoteItemTable
  },
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
    deliveryNoteTotal: { value: 0 },
    snackbar: false,
    snackbarMessage: "",
    snackbarColor: "",
    customerCodeRules: [
      v => !!v || "El código es obligatorio",
      v =>
        (v && v > 0 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos"
    ]
  }),
  created() {
    this.listCustomers();
    this.listProducts();
    this.setDateToday();
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
    setDateToday() {
      var today = new Date();
      var day = today
        .getDate()
        .toString()
        .padStart(2, "0");
      var month = (today.getMonth() + 1).toString().padStart(2, "0");
      var year = today
        .getFullYear()
        .toString()
        .padStart(4, "0");
      this.dateFormatted = day + "/" + month + "/" + year;
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
        }
      }
    },
    selectCustomerByAlias() {
      if (
        this.customer != {} &&
        this.customer != null &&
        this.customer != undefined &&
        this.customer.code != null
      ) {
        this.customerCode = this.customer.code;
        this.listCustomerPrices();
        this.moveToDate();
      }
      return false;
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
                  vm.productCode = responseProduct.data.code;
                  vm.product = responseProduct.data;
                  vm.price = vm.customerPrices[index].offeredPrice;
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
          this.moveToPrice();
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
        this.moveToPrice();
      }
    },
    selectPrice() {
      var vm = this;
      var index = -1;
      if (this.customerPrices) {
        var index = this.customerPrices.findIndex(function(element) {
          return element.productCode == vm.productCode;
        });
      }
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

      this.deliveryNoteTotal.value += itemNet;

      this.quantity = "";
      this.product = {};
      (this.productCode = ""), (this.price = "");
      this.$nextTick(() => this.$refs.createbutton.$el.focus());
    },
    moveToDate() {
      if (this.customerCode != "") {
        this.$nextTick(this.$refs.dateText.focus);
      }
    },
    moveToAuxDeliveryNoteNr() {
      this.$nextTick(this.$refs.auxDeliveryNoteNr.focus);
    },
    moveToQuantity() {
      this.$nextTick(this.$refs.quantity.focus);
    },
    moveToProductCode() {
      this.$nextTick(this.$refs.productCode.focus);
    },
    moveToPrice() {
      this.$nextTick(this.$refs.price.focus);
    },
    moveToAddLine() {
      this.$nextTick(() => this.$refs.addLineButton.$el.focus());
    },
    reset() {
      this.$refs.customerCode.reset();
      this.customer = {};
      this.customerCode = "";
      this.auxDeliveryNoteNr = "";
      this.quantity = "";
      this.product = {};
      this.productCode = "";
      this.price = "";
      this.deliveryNoteItems = [];
      this.deliveryNoteTotal.value = 0;
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
        auxDeliveryNoteNr: this.auxDeliveryNoteNr,
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
        Promise.all(promises).then(function(values) {
          vm.reset();
        });
        this.snackbar = true;
        this.snackbarColor = "success";
        this.snackbarMessage = "Albarán creado correctamente";
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
            // Wrong
            this.$nextTick(this.$refs.dateText.focus);
          }
          break;
        case 10:
          if (
            this.dateFormatted.substring(2, 3) === "/" &&
            this.dateFormatted.substring(5, 6) === "/"
          ) {
            // dd/MM/yyyy
            day = this.dateFormatted.substring(0, 2);
            month = this.dateFormatted.substring(3, 5);
            year = this.dateFormatted.substring(6, 10);
            date = year + "-" + month + "-" + day;
            this.dateFormatted = day + "/" + month + "/" + year;
          }
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
      this.menu1 = false;
      this.moveToAuxDeliveryNoteNr();
    },
    noteItemToAdd() {
      if (this.quantity && this.product && this.price) {
        return true;
      } else {
        return false;
      }
    },
    deliveryNoteValid() {
      if (
        this.customer &&
        this.dateFormatted &&
        this.deliveryNoteItems.length > 0
      ) {
        return true;
      } else {
        return false;
      }
    }
  }
};
</script>