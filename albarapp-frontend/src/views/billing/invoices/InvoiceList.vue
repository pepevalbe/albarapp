<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-layout text-right wrap class="pt-2 pb-5 mr-5">
        <v-flex xs12>
          <v-btn :disabled="filter.mode!=='P'" @click="downloadCsv()" class="ml-2 mt-2">
            Exportar a CSV
            <v-icon class="ml-2">mdi-google-spreadsheet</v-icon>
          </v-btn>
          <v-btn
            @click="downloadPdfMultiple()"
            :disabled="!selectedInvoices.length"
            class="ml-2 mt-2"
          >
            Descargar seleccionadas
            <v-icon class="ml-2">mdi-download-multiple</v-icon>
          </v-btn>
          <v-btn :to="{name: 'BillProcess'}" class="ml-2 mt-2">
            Facturar albaranes
            <v-icon class="ml-2">mdi-animation</v-icon>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-card>
        <v-card-title>Listado de facturas</v-card-title>
        <v-row justify="center">
          <v-chip-group
            active-class="primary--text"
            class="ml-6 mr-6"
            column
            v-model="filter.mode"
            mandatory
          >
            <v-chip
              large
              v-for="mode in filterModes"
              :key="mode.key"
              :value="mode.key"
            >{{mode.name}}</v-chip>
          </v-chip-group>
        </v-row>
        <v-row v-if="filter.mode==='P'">
          <v-col cols="12" md="9">
            <CustomerAndDatesFilterForm :form="filter.form" />
          </v-col>
          <v-col cols="12" md="3">
            <ProductFilter :products="filter.products" />
          </v-col>
        </v-row>
        <v-row justify="center" v-if="filter.mode==='F'">
          <v-col cols="12" md="6">
            <InvoiceIdFilter :invoiceFilter="filter.form.invoiceFilter" />
          </v-col>
        </v-row>
        <v-data-table
          :loading="loading"
          loading-text="Cargando... Por favor, espere"
          show-select
          v-model="selectedInvoices"
          :headers="getHeaders()"
          :footer-props="footerProps"
          :items="invoices"
          item-key="id"
          :server-items-length="totalItems"
          :options.sync="options"
          multi-sort
        >
          <template v-slot:body="{ items }">
            <tbody v-if="!$vuetify.breakpoint.xsOnly">
              <tr v-for="item in items" :key="item.id">
                <td>
                  <v-checkbox
                    v-model="selectedInvoices"
                    :value="item"
                    style="margin:0px;padding:0px"
                    hide-details
                    color="grey"
                  />
                </td>
                <td>F{{item.id}}</td>
                <td>{{item.customerAlias}}</td>
                <td>{{dateFormatted(item.issuedTimestamp)}}</td>
                <td>{{currencyFormatted(item.total)}}</td>
                <td>
                  <v-btn @click="updateInvoice(item)">
                    <v-icon dark>mdi-pencil</v-icon>
                  </v-btn>
                </td>
                <td>
                  <v-btn @click="downloadPdf(item)">
                    <v-icon dark>mdi-file-pdf-box</v-icon>
                  </v-btn>
                </td>
                <td>
                  <v-btn v-if="item.isEdiInvoice" @click="downloadEdi(item)">
                    <v-icon dark>mdi-xml</v-icon>
                  </v-btn>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr>
                <v-card class="flex-content" outlined v-for="item in items" :key="item.id">
                  <v-card-text>
                    <v-row colspan="12">
                      <v-col cols="3">
                        <v-checkbox
                          v-model="selectedInvoices"
                          :value="item"
                          style="margin:0px;padding:0px"
                          hide-details
                          color="grey"
                          class="mb-1"
                        />
                      </v-col>
                      <v-col cols="9">
                        <span class="black--text">Nº Factura:</span>
                        F{{item.id}}
                        <br />
                        <span class="black--text">Cliente:</span>
                        {{item.customerAlias}}
                        <br />
                        <span class="black--text">Fecha:</span>
                        {{dateFormatted(item.issuedTimestamp)}}
                        <br />
                        <span class="black--text">Total:</span>
                        {{currencyFormatted(item.total)}}
                        <br />
                      </v-col>
                    </v-row>
                  </v-card-text>
                  <v-card-actions>
                    <v-layout text-center wrap>
                      <v-flex xs12>
                        <v-btn class="mr-3" @click="updateInvoice(item)">
                          <v-icon dark>mdi-pencil</v-icon>
                        </v-btn>
                        <v-btn class="mr-3" @click="downloadPdf(item)">
                          <v-icon dark>mdi-file-pdf-box</v-icon>
                        </v-btn>
                        <v-btn v-if="item.isCustomerAecoc" class="mr-3" @click="downloadEdi(item)">
                          <v-icon dark>mdi-xml</v-icon>
                        </v-btn>
                      </v-flex>
                    </v-layout>
                  </v-card-actions>
                </v-card>
              </tr>
            </tbody>
          </template>
        </v-data-table>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center">Error al obtener las facturas, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="listInvoices()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{snackbar.message}}
      <v-btn text @click="snackbar.show=false">Cerrar</v-btn>
    </v-snackbar>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-container>
</template>

<script>
import InvoiceService from "@/services/InvoiceService.js";
import CustomerAndDatesFilterForm from "@/components/CustomerAndDatesFilterForm";
import ProductFilter from "@/components/ProductFilter";
import InvoiceIdFilter from "@/components/InvoiceIdFilter";

export default {
  name: "InvoiceList",
  components: {
    CustomerAndDatesFilterForm,
    ProductFilter,
    InvoiceIdFilter,
  },
  data: () => {
    return {
      invoices: [],
      netTotal: 0,
      selectedInvoices: [],
      footerProps: {
        itemsPerPageOptions: [15, 30, 45, 60, 75],
        showFirstLastPage: true,
      },
      options: {
        page: 1,
        itemsPerPage: 15,
        sortBy: [],
        sortDesc: [],
        groupBy: [],
        groupDesc: [],
        mustSort: false,
        multiSort: true,
      },
      loading: true,
      errorLoading: false,
      totalItems: 0,
      filterModes: [
        { key: "P", name: "Filtrar por parámetros" },
        { key: "F", name: "Buscar por número de factura" },
      ],
      filter: {
        mode: "P",
        form: {
          valid: true,
          customerCode: "",
          dateFrom: "",
          dateTo: "",
          invoiceFilter: {
            enabled: false,
            idFrom: 0,
            idTo: 0,
          },
        },
        products: {
          productCodes: [],
        },
      },
      spinner: {
        loading: false,
        counter: 0,
      },
      snackbar: {
        show: false,
        message: "",
        color: "",
      },
    };
  },
  async created() {
    if (this.$route.query) {
      if (this.$route.query.customerCode)
        this.filter.form.customerCode = this.$route.query.customerCode;
      if (this.$route.query.from)
        this.filter.form.dateFrom = this.$route.query.from;
      if (this.$route.query.to) this.filter.form.dateTo = this.$route.query.to;
      if (this.$route.query.productCodes) {
        var productCodes = this.$route.query.productCodes.split(",");
        productCodes.forEach((productCode) => {
          this.filter.products.productCodes.push(Number(productCode));
        });
      }
      if (this?.$route?.query?.idFrom) {
        this.filter.mode = "F";
        this.filter.form.invoiceFilter.idFrom = this.$route.query.idFrom;
        this.filter.form.invoiceFilter.idTo = this.$route.query.idTo;
      }
      if (this.$route.query.page)
        this.options.page = Number(this.$route.query.page);
      if (this.$route.query.itemsPerPage)
        this.options.itemsPerPage = Number(this.$route.query.itemsPerPage);
      if (this.$route.query.sortBy)
        this.options.sortBy = this.$route.query.sortBy.split(',');
      if (this.$route.query.sortDesc)
        this.options.sortDesc = this.$route.query.sortDesc.split(',').map(e => e === 'true');
    }
    await this.listInvoices();
    this.$watch("options", this.listInvoices, { deep: true });
    this.$watch(
      "filter",
      function () {
        if (this.options.page != 1) this.options.page = 1;
        else this.listInvoices();
      },
      { deep: true }
    );
  },
  methods: {
    async listInvoices() {
      try {
        this.loading = true;
        this.errorLoading = false;
        this.showSpinner();
        var response;
        if (this.filter.mode === "F") {
          response = await InvoiceService.getIntervalWithCustomerAndTotal(
            this.filter,
            this.options
          );
        } else {
          response = await InvoiceService.getAllWithCustomerAndTotal(
            this.filter,
            this.options
          );
        }
        this.invoices = response.invoices;
        this.netTotal = this.invoices.reduce((a, b) => a + (b.total || 0), 0);
        this.selectedInvoices = [];
        this.totalItems = response.totalElements;
        this.loading = false;
        this.updateURL();
      } catch {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    updateInvoice(item) {
      this.$router.push({
        name: "InvoiceUpdate",
        params: { invoiceId: item.id.toString() },
      });
    },
    getHeaders() {
      var headers = [
        { text: "Nº Factura", sortable: true, value: "id" },
        { text: "Cliente", sortable: true, value: "customer.code" },
        { text: "Fecha de emisión", sortable: true, value: "issuedTimestamp" },
        {
          text: "Total: " + this.currencyFormatted(this.netTotal),
          sortable: false,
          value: "total",
        },
        { text: "", sortable: false, value: "update" },
        { text: "", sortable: false, value: "downloadPdf" },
        { text: "", sortable: false, value: "downloadEdi" },
      ];
      return headers;
    },
    async downloadPdf(item) {
      try {
        this.showSpinner();
        await InvoiceService.downloadPdf(item.id);
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido descargar la factura, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    async downloadEdi(item) {
      try {
        this.showSpinner();
        await InvoiceService.downloadEdi(item.id);
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido descargar la factura, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    async downloadPdfMultiple() {
      try {
        this.showSpinner();
        await InvoiceService.downloadPdfMultiple(
          this.selectedInvoices.map((dto) => dto.id)
        );
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido descargar las facturas, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    updateURL() {
      var query = {};
      if (this.filter.mode === "F") {
        query.idFrom = this.filter.form.invoiceFilter.idFrom;
        query.idTo = this.filter.form.invoiceFilter.idTo;
      } else if (this.filter.mode === "P") {
        if (this.filter.form.customerCode)
          query.customerCode = this.filter.form.customerCode;
        if (this.filter.form.dateFrom) query.from = this.filter.form.dateFrom;
        if (this.filter.form.dateTo) query.to = this.filter.form.dateTo;
        if (
          this.filter.products.productCodes &&
          this.filter.products.productCodes.length
        )
          query.productCodes = this.filter.products.productCodes.toString();
      }
      if (this.options.page) query.page = this.options.page;
      if (this.options.itemsPerPage)
        query.itemsPerPage = this.options.itemsPerPage;
      if (this.options.sortBy) query.sortBy = this.options.sortBy[0];
      if (this.options.sortDesc) query.sortDesc = this.options.sortDesc[0];
      this.$router
        .push({
          path: this.$route.path,
          query: query,
        })
        .catch(() => {});
    },
    async downloadCsv() {
      try {
        this.showSpinner();
        await InvoiceService.downloadCsv(this.filter, this.options);
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido descargar el informe, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>