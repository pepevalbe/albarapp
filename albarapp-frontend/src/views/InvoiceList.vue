<template>
  <v-flex align-self-start>
    <v-layout text-right wrap class="pt-2 pb-5 mr-5">
      <v-flex xs12>
        <v-btn @click="downloadList()" :disabled="!selectedInvoices.length" class="ml-2 mt-2">
          Descargar seleccionadas
          <v-icon class="ml-2">mdi-download-multiple</v-icon>
        </v-btn>
        <v-btn to="/invoice-bill-process/" class="ml-2 mt-2">
          Facturar albaranes
          <v-icon class="ml-2">mdi-animation</v-icon>
        </v-btn>
        <v-btn to="/" class="ml-2 mt-2">
          Nuevo
          <v-icon class="ml-2">mdi-plus-circle</v-icon>
        </v-btn>
      </v-flex>
    </v-layout>
    <v-card>
      <v-card-title>Listado de facturas</v-card-title>
      <CustomerAndDatesFilterForm v-bind:form="filter.form" />
      <v-data-table
        :loading="loading"
        loading-text="Cargando... Por favor, espere"
        show-select
        v-model="selectedInvoices"
        :headers="headers"
        :footer-props="footerProps"
        :items="invoices"
        item-key="id"
        :server-items-length="totalItems"
        :options.sync="options"
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
                <v-btn @click="download(item)">
                  <v-icon dark>mdi-file-pdf</v-icon>
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
                      <v-btn class="mr-3" @click="download(item)">
                        <v-icon dark>mdi-file-pdf</v-icon>
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
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
  </v-flex>
</template>

<script>
import InvoiceService from "@/services/InvoiceService.js";
import CustomerAndDatesFilterForm from "@/components/CustomerAndDatesFilterForm";

export default {
  name: "InvoiceList",
  components: {
    CustomerAndDatesFilterForm
  },
  data: () => {
    return {
      invoices: [],
      selectedInvoices: [],
      headers: [
        { text: "Nº Factura", sortable: true, value: "id" },
        { text: "Cliente", sortable: true, value: "customer.code" },
        { text: "Fecha de emisión", sortable: true, value: "issuedTimestamp" },
        { text: "Total", sortable: false, value: "total" },
        { text: "", sortable: false, value: "update" },
        { text: "", sortable: false, value: "download" }
      ],
      footerProps: {
        itemsPerPageOptions: [10, 20, 30, 40, 50],
        showFirstLastPage: true
      },
      options: {},
      loading: true,
      totalItems: 0,
      filter: {
        form: {
          valid: true,
          customerCode: "",
          dateFrom: "",
          dateTo: ""
        }
      },
      spinner: {
        loading: false,
        counter: 0
      }
    };
  },
  async created() {
    if (this.$route.query) {
      if (this.$route.query.customerCode)
        this.filter.form.customerCode = this.$route.query.customerCode;
      if (this.$route.query.from)
        this.filter.form.dateFrom = this.$route.query.from;
      if (this.$route.query.to) this.filter.form.dateTo = this.$route.query.to;
      if (this.$route.query.page)
        this.options.page = Number(this.$route.query.page);
      if (this.$route.query.itemsPerPage)
        this.options.itemsPerPage = Number(this.$route.query.itemsPerPage);
      if (this.$route.query.sortBy)
        this.options.sortBy[0] = this.$route.query.sortBy;
      if (this.$route.query.sortDesc)
        this.options.sortDesc[0] = this.$route.query.sortDesc;
    }
    await this.listInvoices();
    this.$watch("options", this.listInvoices, { deep: true });
    this.$watch("filter", this.listInvoices, { deep: true });
  },
  methods: {
    async listInvoices() {
      this.loading = true;
      this.showSpinner();
      var response = await InvoiceService.getAllWithCustomerAndTotal(
        this.filter,
        this.options
      );
      this.invoices = response.invoices;
      this.selectedInvoices = [];
      this.totalItems = response.totalElements;
      this.loading = false;
      this.closeSpinner();
      this.updateURL();
    },
    updateInvoice(item) {
      this.$router.push({
        name: "InvoiceUpdate",
        params: { invoiceId: item.id.toString() }
      });
    },
    dateFormatted(timestamp) {
      return this.$moment(timestamp, "x").format("DD/MM/YYYY");
    },
    currencyFormatted(value) {
      return value.toLocaleString("es-ES", {
        style: "currency",
        currency: "EUR"
      });
    },
    download(item) {
      InvoiceService.download(item.id);
    },
    async downloadList() {
      this.showSpinner();
      await InvoiceService.downloadList(
        this.selectedInvoices.map(dto => dto.id)
      );
      this.closeSpinner();
    },
    updateURL() {
      var query = {};
      if (this.filter.form.customerCode)
        query.customerCode = this.filter.form.customerCode;
      if (this.filter.form.dateFrom) query.from = this.filter.form.dateFrom;
      if (this.filter.form.dateTo) query.to = this.filter.form.dateTo;
      if (this.options.page) query.page = this.options.page;
      if (this.options.itemsPerPage)
        query.itemsPerPage = this.options.itemsPerPage;
      if (this.options.sortBy) query.sortBy = this.options.sortBy[0];
      if (this.options.sortDesc) query.sortDesc = this.options.sortDesc[0];
      this.$router
        .push({
          path: this.$route.path,
          query: query
        })
        .catch(err => {});
    }
  }
};
</script>