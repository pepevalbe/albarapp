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
            v-model="form.customer"
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
            ref="menuDatePicker"
            v-model="menuDatePicker"
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
              v-model="form.date"
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
            v-model="form.auxDeliveryNoteNr"
            type="number"
            label="Nº Pedido"
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
            :deliveryNoteItems="form.deliveryNoteItems"
            :deliveryNoteTotal="form.deliveryNoteTotal"
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
      <v-btn color="error" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
import DeliveryNoteItemTable from "@/components/DeliveryNoteItemTable";
import DateFormatService from "@/services/DateFormatService.js";

export default {
  name: "DeliveryNoteForm",
  components: {
    DeliveryNoteItemTable
  },
  data: () => ({
    customerCode: "",
    customers: [],
    dateFormatted: "",
    auxDeliveryNoteNr: "",
    products: [],
    product: {},
    productCode: "",
    quantity: "",
    price: "",
    menuDatePicker: false,
    snackbar: false,
    snackbarMessage: "",
    customerCodeRules: [
      v => !!v || "El código es obligatorio",
      v =>
        (v && v > 0 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos"
    ]
  }),
  props: {
    form: Object
  },
  watch: {
    form: function() {
      this.selectCustomerByAlias();
      this.parseDatePick();
      this.$nextTick(this.$refs.customerCode.focus);
    }
  },
  created() {
    this.listCustomers();
    this.listProducts();
    this.parseDatePick();
  },
  methods: {
    listCustomers() {
      this.$axios
        .get("/customers")
        .then(response => {
          this.customers = response.data._embedded.customers;
        })
        .catch(function() {
          alert("Ha ocurrido un error recuperando los clientes");
        });
    },
    listProducts() {
      this.$axios
        .get("/products")
        .then(response => {
          this.products = response.data._embedded.products;
        })
        .catch(function() {
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
          this.$nextTick(this.$refs.customerCode.focus);
        } else {
          this.form.customer = this.customers[index];
          this.listCustomerPrices();
        }
      }
    },
    selectCustomerByAlias() {
      if (
        this.form.customer != {} &&
        this.form.customer != null &&
        this.form.customer != undefined &&
        this.form.customer.code != null
      ) {
        this.customerCode = this.form.customer.code;
        this.listCustomerPrices();
        this.moveToDate();
      }
      return false;
    },
    clearCustomer() {
      this.form.customer = {};
    },
    listCustomerPrices() {
      var vm = this;
      this.$axios
        .get(this.form.customer._links.customerProductPrices.href)
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
        .catch(function() {
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
        index = this.customerPrices.findIndex(function(element) {
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
      this.form.deliveryNoteItems.push({
        quantity: this.quantity,
        productName: this.product.name,
        product: this.product,
        price: this.price,
        gross: this.quantity * this.price,
        taxRate: this.product.tax,
        net: this.quantity * this.price * (1 + this.product.tax / 100)
      });

      this.form.deliveryNoteTotal.value += itemNet;

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
      this.form.customer = {};
      this.form.deliveryNoteItems = [];
      this.form.deliveryNoteTotal.value = 0;
      this.form.auxDeliveryNoteNr = "";
      this.customerCode = "";
      this.quantity = "";
      this.product = {};
      this.productCode = "";
      this.price = "";
      this.$nextTick(this.$refs.customerCode.focus);
    },
    createDeliveryNote() {
      this.$emit("saveClicked");
    },
    parseDateText() {
      var resultDateFormat = DateFormatService.formatDateText(
        this.dateFormatted
      );
      if (resultDateFormat.format) {
        this.form.date = resultDateFormat.date;
        this.dateFormatted = resultDateFormat.dateFormatted;
      } else {
        this.$nextTick(this.$refs.dateText.focus);
      }
    },
    parseDatePick() {
      this.dateFormatted = DateFormatService.formatDatePick(this.form.date);
      this.menuDatePicker = false;
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
        this.form.customer &&
        this.form.customer.code &&
        this.dateFormatted &&
        this.form.deliveryNoteItems.length > 0
      ) {
        return true;
      } else {
        return false;
      }
    }
  }
};
</script>