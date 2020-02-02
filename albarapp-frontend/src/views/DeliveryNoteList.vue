<template>
  <v-flex align-self-start>
    <v-layout text-right wrap class="pt-2 pb-5 mr-5">
      <v-flex xs12>
        <v-btn to="/delivery-note-creation/">
          Nuevo
          <v-icon class="ml-2">mdi-plus-circle</v-icon>
        </v-btn>
      </v-flex>
    </v-layout>
    <v-card>
      <v-card-title>Listado de albaranes</v-card-title>
      <CustomerAndDatesFilterForm v-bind:form="filter.form" />
      <v-data-table
        :loading="loading"
        loading-text="Cargando... Por favor, espere"
        :headers="getHeaders()"
        :footer-props="footerProps"
        :items="deliveryNotes"
        :server-items-length="totalItems"
        :options.sync="options"
      >
        <template v-slot:body="{ items }">
          <tbody v-if="!$vuetify.breakpoint.xsOnly">
            <tr v-for="item in items" :key="item.deliveryNoteItemsHref">
              <td>A{{item.id}}</td>
              <td>
                <span v-if="item.invoiceId">F{{item.invoiceId}}</span>
              </td>
              <td>{{item.auxDeliveryNoteNr}}</td>
              <td>{{item.customerAlias}}</td>
              <td>{{dateFormatted(item.issuedTimestamp)}}</td>
              <td>
                <span v-for="(noteItem,index) in item.deliveryNoteItems" :key="index">
                  {{noteItem.quantity}} - {{noteItem.productName}} - {{noteItem.price}} €
                  <br />
                </span>
              </td>
              <td>{{currencyFormatted(item.total)}}</td>
              <td>
                <v-btn @click="updateDeliveryNote(item)">
                  <v-icon dark>mdi-pencil</v-icon>
                </v-btn>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr>
              <v-card class="flex-content" outlined v-for="item in items" :key="item.id">
                <v-card-text>
                  <span class="black--text">Nº Albarán:</span>
                  A{{item.id}}
                  <br />
                  <span class="black--text">Nº Factura:</span>
                  <span v-if="item.invoiceId">F{{item.invoiceId}}</span>
                  <br />
                  <span class="black--text">Nº Albarán auxiliar:</span>
                  {{item.auxDeliveryNoteNr}}
                  <br />
                  <span class="black--text">Cliente:</span>
                  {{item.customerAlias}}
                  <br />
                  <span class="black--text">Fecha:</span>
                  {{dateFormatted(item.issuedTimestamp)}}
                  <br />
                  <span v-for="(noteItem,index) in item.deliveryNoteItems" :key="index">
                    {{noteItem.quantity}} - {{noteItem.productName}} - {{noteItem.price}} €
                    <br />
                  </span>
                  <span class="black--text">Total:</span>
                  {{currencyFormatted(item.total)}}
                  <br />
                </v-card-text>
                <v-card-actions>
                  <v-layout text-center wrap>
                    <v-flex xs12>
                      <v-btn @click="updateDeliveryNote(item)">
                        <v-icon dark>mdi-pencil</v-icon>
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
import DeliveryNoteService from "@/services/DeliveryNoteService.js";
import CustomerAndDatesFilterForm from "@/components/CustomerAndDatesFilterForm";

export default {
  name: "DeliveryNoteList",
  components: {
    CustomerAndDatesFilterForm
  },
  data: () => {
    return {
      deliveryNotes: [],
      netTotal: 0,
      footerProps: {
        itemsPerPageOptions: [15, 30, 45, 60, 75],
        showFirstLastPage: true
      },
      options: {
        page: 1,
        itemsPerPage: 15,
        sortBy: [],
        sortDesc: [],
        groupBy: [],
        groupDesc: [],
        mustSort: false,
        multiSort: false
      },
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
    await this.listDeliveryNotes();
    this.$watch("options", this.listDeliveryNotes, { deep: true });
    this.$watch(
      "filter",
      function() {
        if (this.options.page != 1) this.options.page = 1;
        else this.listDeliveryNotes();
      },
      { deep: true }
    );
  },
  methods: {
    async listDeliveryNotes() {
      this.loading = true;
      this.showSpinner();
      var response = await DeliveryNoteService.getAllWithCustomerAndTotal(
        this.filter,
        this.options
      );
      this.deliveryNotes = response.deliveryNotes;
      this.netTotal = this.deliveryNotes.reduce(
        (a, b) => a + (b.total || 0),
        0
      );
      this.totalItems = response.totalElements;
      this.loading = false;
      this.closeSpinner();
      this.updateURL();
    },
    updateDeliveryNote(item) {
      this.$router.push({
        name: "DeliveryNoteUpdate",
        params: { deliveryNoteId: item.id.toString() }
      });
    },
    dateFormatted(timestamp) {
      return this.$moment.utc(timestamp, "x").format("DD/MM/YYYY");
    },
    getHeaders() {
      var headers = [
        { text: "Nº Albarán", sortable: true, value: "id" },
        { text: "Nº Factura", sortable: true, value: "invoice.id" },
        {
          text: "Nº pedido",
          sortable: false,
          value: "auxDeliveryNoteNr"
        },
        { text: "Cliente", sortable: true, value: "customer.code" },
        { text: "Fecha", sortable: true, value: "issuedTimestamp" },
        { text: "Productos", sortable: false, value: "products" },
        {
          text: "Total: " + this.currencyFormatted(this.netTotal),
          sortable: false,
          value: "total"
        },
        { text: "", sortable: false, value: "update" }
      ];
      return headers;
    },
    currencyFormatted(value) {
      return value.toLocaleString("es-ES", {
        style: "currency",
        currency: "EUR"
      });
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
        .catch(() => {});
    }
  }
};
</script>