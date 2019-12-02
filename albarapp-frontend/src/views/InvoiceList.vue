<template>
  <v-flex align-self-start>
    <v-layout text-right wrap class="pt-2 pb-5 mr-5">
      <v-flex xs12>
        <v-btn to="/invoice-bill-process/">
          Facturar albaranes
          <v-icon class="ml-2">mdi-animation</v-icon>
        </v-btn>
        <v-btn to="/" class="ml-2">
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
        :headers="headers"
        :footer-props="footerProps"
        :items="invoices"
        :server-items-length="totalItems"
        :options.sync="options"
      >
        <template v-slot:body="{ items }">
          <tbody v-if="!$vuetify.breakpoint.xsOnly">
            <tr v-for="item in items" :key="item.id">
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
                  <v-icon dark>mdi-cloud-download</v-icon>
                </v-btn>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr>
              <v-card class="flex-content" outlined v-for="item in items" :key="item.id">
                <v-card-text>
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
                </v-card-text>
                <v-card-actions>
                  <v-layout text-center wrap>
                    <v-flex xs12>
                      <v-btn class="mr-3" @click="updateInvoice(item)">
                        <v-icon dark>mdi-pencil</v-icon>
                      </v-btn>
                      <v-btn class="mr-3" @click="download(item)">
                        <v-icon dark>mdi-cloud-download</v-icon>
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
      headers: [
        { text: "Nº Factura", sortable: true, value: "id" },
        { text: "Cliente", sortable: false, value: "customerAlias" },
        { text: "Fecha de emisión", sortable: false, value: "dateFormatted" },
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
          valid: false,
          customer: {},
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
  async mounted() {
    await this.listInvoices();
  },
  watch: {
    options: {
      handler() {
        this.listInvoices();
      },
      deep: true
    },
    filter: {
      handler() {
        this.options.page = 1;
        this.listInvoices();
      },
      deep: true
    }
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
      this.totalItems = response.totalElements;
      this.loading = false;
      this.closeSpinner();
    },
    updateInvoice(item) {
      this.$router.push({
        name: "InvoiceUpdate",
        params: { invoiceId: item.id }
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
    }
  }
};
</script>