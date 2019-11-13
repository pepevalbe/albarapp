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
      <v-card-title>
        Listado de facturas
        <div class="flex-grow-1"></div>
        <v-text-field
          v-model="search"
          append-icon="mdi-magnify"
          label="Buscar ..."
          single-line
          hide-details
        ></v-text-field>
      </v-card-title>
      <v-data-table
        :loading="!invoices || invoices.length == 0"
        loading-text="Cargando... Por favor, espere"
        :headers="headers"
        :items="invoices"
        :search="search"
        :sort-by.sync="sortBy"
        :sort-desc.sync="descending"
        :items-per-page="15"
      >
        <template v-slot:body="{ items }">
          <tbody v-if="!$vuetify.breakpoint.xsOnly">
            <tr v-for="item in items" :key="item.id">
              <td>F{{item.id}}</td>
              <td>{{item.deliveryNotes[0].customer.alias}}</td>
              <td>{{item.dateFormatted}}</td>
              <td>{{item.total.toFixed(2)}} €</td>
              <td>
                <v-btn @click="updateInvoice(item)">
                  <v-icon dark>mdi-pencil</v-icon>
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
                  {{item.deliveryNotes[0].customer.alias}}
                  <br />
                  <span class="black--text">Fecha:</span>
                  {{item.dateFormatted}}
                  <br />
                  <span class="black--text">Total:</span>
                  {{item.total.toFixed(2)}} €
                  <br />
                </v-card-text>
                <v-card-actions>
                  <v-layout text-center wrap>
                    <v-flex xs12>
                      <v-btn @click="updateInvoice(item)">
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
  </v-flex>
</template>

<script>
import InvoiceService from "@/services/InvoiceService.js";

export default {
  name: "InvoiceService",
  data: () => {
    return {
      invoices: [],
      headers: [
        { text: "Nº Factura", sortable: true, value: "id" },
        { text: "Cliente", sortable: false, value: "alias" },
        { text: "Fecha de emisión", sortable: false, value: "dateFormatted" },
        { text: "Total", sortable: false, value: "total" },
        { text: "", sortable: false, value: "update" }
      ],
      search: "",
      sortBy: "id",
      descending: false
    };
  },
  async created() {
    await this.listInvoices();
  },
  methods: {
    async listInvoices() {
      this.invoices = await InvoiceService.getAllWithCustomerAndTotal();
    },
    updateInvoice() {
      //TODO: Navigate to update-invoice
    }
  }
};
</script>