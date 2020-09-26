<template>
  <v-container>
    <div v-if="!errorLoading">
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
              autocomplete="off"
              @focus="$event.target.select()"
              @blur="selectCustomerByCode()"
              @input="clearCustomer()"
              @keypress.enter="moveToDate()"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="6">
            <v-autocomplete
              v-model="form.deliveryNote.customer"
              label="Alias cliente"
              :items="customers"
              item-text="alias"
              return-object
              autocomplete="off"
              no-data-text="Sin coincidencias"
              @change="selectCustomerByAlias()"
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
                  v-model="dateFormattedText"
                  ref="dateText"
                  label="Fecha"
                  hint="Formato: ddMMaaaa"
                  persistent-hint
                  autocomplete="off"
                  @focus="$event.target.select()"
                  prepend-icon="mdi-calendar"
                  @blur="parseDateText()"
                  @keypress.enter="moveToAuxDeliveryNoteNr()"
                  v-on="on"
                ></v-text-field>
              </template>
              <v-date-picker
                v-model="form.deliveryNote.date"
                no-title
                @input="datePickedOnCalendar()"
                locale="es-ES"
                first-day-of-week="1"
              ></v-date-picker>
            </v-menu>
          </v-col>
          <v-col cols="12" md="2">
            <v-text-field
              ref="auxDeliveryNoteNr"
              v-model="form.deliveryNote.auxDeliveryNoteNr"
              type="number"
              autocomplete="off"
              label="Nº Pedido"
              @focus="$event.target.select()"
              @keypress.enter="moveToQuantity()"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="12" md="2">
            <v-text-field
              ref="quantity"
              v-model="quantity"
              type="number"
              autocomplete="off"
              label="Cantidad"
              @focus="$event.target.select()"
              @keypress.enter="moveToProductCode()"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="2">
            <v-text-field
              ref="productCode"
              v-model="productCode"
              type="number"
              autocomplete="off"
              label="Código de producto"
              @focus="$event.target.select()"
              @keypress.enter="selectProductByCode()"
              @blur="selectProductByCode()"
              @input="clearProduct()"
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
              @change="selectProductByName()"
            ></v-autocomplete>
          </v-col>
          <v-col cols="12" md="2">
            <v-text-field
              ref="price"
              v-model="price"
              type="number"
              autocomplete="off"
              label="Precio"
              @focus="$event.target.select()"
              @keypress.enter="moveToAddLine()"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="2">
            <v-flex text-xs-center align-center>
              <v-btn
                @click="addDeliveryNoteItem()"
                ref="addLineButton"
                :disabled="!noteItemToAdd()"
              >
                <span>Añadir línea</span>
              </v-btn>
            </v-flex>
          </v-col>
        </v-row>
        <v-row class="ml-5" justify="center">
          <v-col cols="12" md="8">
            <DeliveryNoteItemTable
              :deliveryNoteItems="form.deliveryNote.deliveryNoteItems"
              :deliveryNoteTotal="form.deliveryNote.deliveryNoteTotal"
            ></DeliveryNoteItemTable>
          </v-col>
        </v-row>
        <div class="mb-10"></div>
        <v-layout text-center wrap class="pt-10">
          <v-flex xs12>
            <v-row class="ml-5" justify="center">
              <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
              <v-btn color="error" v-if="form.create" class="mr-4" @click="reset()">Borrar</v-btn>
              <v-btn
                ref="createbutton"
                class="mr-4"
                :disabled="!deliveryNoteValid()"
                @click="createDeliveryNote()"
                @keyup.left="moveToQuantity()"
              >Guardar</v-btn>
            </v-row>
          </v-flex>
        </v-layout>
      </v-form>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center">Error al cargar la página, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="loadView()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-snackbar v-model="snackbar">
      {{snackbarMessage}}
      <v-btn color="error" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-container>
</template>

<script>
import DeliveryNoteItemTable from "@/components/DeliveryNoteItemTable";
import CustomerService from "@/services/CustomerService.js";
import ProductService from "@/services/ProductService.js";

export default {
  name: "DeliveryNoteForm",
  components: {
    DeliveryNoteItemTable,
  },
  data: () => ({
    customerCode: "",
    customers: [],
    dateFormattedText: "",
    products: [],
    product: {},
    productCode: "",
    quantity: "",
    price: "",
    menuDatePicker: false,
    snackbar: false,
    snackbarMessage: "",
    customerCodeRules: [
      (v) => !!v || "El código es obligatorio",
      (v) =>
        (v && v > 0 && v <= 99999) ||
        "El código debe tener un máximo de 5 dígitos",
    ],
    errorLoading: false,
    spinner: {
      loading: false,
      counter: 0,
    },
  }),
  props: {
    form: Object,
  },
  mounted() {
    this.loadView();
  },
  methods: {
    async loadView() {
      try {
        this.errorLoading = false;
        this.showSpinner();
        this.customers = await CustomerService.getAllWithPrices();
        for (var customer of this.customers) {
          customer.alias = customer.alias + " - " + customer.name;
        }
        if (this.form.deliveryNote.customer) {
          this.customerCode = this.form.deliveryNote.customer.code;
          this.selectCustomerByCode();
        }
        this.products = await ProductService.getAll();
        this.parseDatePick();
      } catch {
        this.errorLoading = true;
      } finally {
        this.$nextTick(this.$refs.customerCode.focus);
        this.closeSpinner();
      }
    },
    selectCustomerByCode() {
      var vm = this;
      if (this.customerCode) {
        var index = this.customers.findIndex(function (element) {
          return element.code == vm.customerCode;
        });
        if (index === -1) {
          this.snackbar = true;
          this.snackbarMessage = "No existe ningún cliente con ese código";
          this.$nextTick(this.$refs.customerCode.focus);
        } else {
          this.form.deliveryNote.customer = this.customers[index];
          this.selectCustomerPrices();
        }
      }
    },
    selectCustomerByAlias() {
      if (
        this.form.deliveryNote.customer &&
        this.form.deliveryNote.customer.code
      ) {
        this.customerCode = this.form.deliveryNote.customer.code;
        this.selectCustomerPrices();
        this.moveToDate();
      }
      return false;
    },
    clearCustomer() {
      this.form.deliveryNote.customer = {};
    },
    async selectCustomerPrices() {
      this.customerPrices = this.form.deliveryNote.customer.customerProductPrices;
    },
    selectProductByCode() {
      var vm = this;
      if (this.productCode) {
        var index = this.products.findIndex(function (element) {
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
        index = this.customerPrices.findIndex(function (element) {
          return element.productId == vm.product.id;
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
      this.form.deliveryNote.deliveryNoteItems.push({
        quantity: this.quantity,
        productName: this.product.name,
        product: this.product,
        price: this.price,
        gross: this.quantity * this.price,
        taxRate: this.product.tax,
        net: this.quantity * this.price * (1 + this.product.tax / 100),
      });

      this.form.deliveryNote.deliveryNoteTotal.value += itemNet;

      this.quantity = "";
      this.product = {};
      this.productCode = "";
      this.price = "";
      this.$nextTick(() => this.$refs.createbutton.$el.focus());
    },
    moveToDate() {
      if (this.customerCode != "") {
        this.$nextTick(this.$refs.dateText.focus);
      }
    },
    moveToAuxDeliveryNoteNr() {
      this.$nextTick(this.$refs.auxDeliveryNoteNr.focus);
      this.menuDatePicker = false;
    },
    moveToQuantity() {
      // We load default product and productPrice for this customer
      // We load the customerProductPrice of index depending on the number of lines
      // the deliveryNote already has
      if (this.customerPrices && this.customerPrices.length > 0) {
        var vm = this;
        var indexCustomerProductPrice = this.form.deliveryNote.deliveryNoteItems
          .length;
        if (indexCustomerProductPrice >= this.customerPrices.length)
          indexCustomerProductPrice = 0;
        var index = this.products.findIndex(function (element) {
          return (
            element.id == vm.customerPrices[indexCustomerProductPrice].productId
          );
        });
        vm.productCode = vm.products[index].code;
        vm.product = vm.products[index];
        vm.price = vm.customerPrices[indexCustomerProductPrice].offeredPrice;
      }
      this.$nextTick(this.$refs.quantity.focus);
    },
    moveToProductCode() {
      if (this.quantity) {
        this.$nextTick(this.$refs.productCode.focus);
      }
    },
    moveToPrice() {
      this.$nextTick(this.$refs.price.focus);
    },
    moveToAddLine() {
      this.$nextTick(() => this.$refs.addLineButton.$el.focus());
    },
    reset() {
      this.$refs.customerCode.reset();
      this.form.deliveryNote.customer = {};
      this.form.deliveryNote.deliveryNoteItems = [];
      this.form.deliveryNote.deliveryNoteTotal.value = 0;
      this.form.deliveryNote.auxDeliveryNoteNr = "";
      this.customerCode = "";
      this.quantity = "";
      this.product = {};
      this.productCode = "";
      this.price = "";
      this.$nextTick(this.$refs.customerCode.focus);
    },
    createDeliveryNote() {
      this.$emit("saveClicked");
      this.$nextTick(this.$refs.customerCode.focus);
    },
    parseDateText() {
      var moment = this.$moment.utc(
        this.dateFormattedText,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.deliveryNote.date = moment.format("YYYY-MM-DD");
        this.dateFormattedText = moment.format("DD/MM/YYYY");
        this.form.deliveryNote.issuedTimestamp = moment.format("x");
      } else {
        this.$nextTick(this.$refs.dateText.focus);
      }
    },
    datePickedOnCalendar() {
      this.parseDatePick();
      this.moveToAuxDeliveryNoteNr();
    },
    parseDatePick() {
      var moment = this.$moment.utc(
        this.form.deliveryNote.date,
        "YYYY-MM-DD",
        true
      );
      this.dateFormattedText = moment.format("DD/MM/YYYY");
      this.form.deliveryNote.issuedTimestamp = moment.format("x");
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
        this.form.deliveryNote.customer?.code &&
        this.dateFormattedText &&
        this.form.deliveryNote.deliveryNoteItems.length > 0
      ) {
        return true;
      } else {
        return false;
      }
    },
  },
};
</script>
