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
        :headers="headers"
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
      headers: [
        { text: "Nº Albarán", sortable: true, value: "id" },
        { text: "Nº Factura", sortable: true, value: "invoice.id" },
        {
          text: "Nº pedido",
          sortable: false,
          value: "auxDeliveryNoteNr"
        },
        { text: "Cliente", sortable: true, value: "customer.code" },
        { text: "Fecha", sortable: true, value: "issuedTimestamp" },
        { text: "Total", sortable: false, value: "total" },
        { text: "", sortable: false, value: "update" }
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
    await this.listDeliveryNotes();
  },
  watch: {
    options: {
      handler() {
        this.listDeliveryNotes();
      },
      deep: true
    },
    filter: {
      handler() {
        this.options.page = 1;
        this.listDeliveryNotes();
      },
      deep: true
    }
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
      this.totalItems = response.totalElements;
      this.loading = false;
      this.closeSpinner();
    },
    updateDeliveryNote(item) {
      this.$router.push({
        name: "DeliveryNoteUpdate",
        params: { deliveryNoteId: item.id.toString() }
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
    }
  }
};
</script>