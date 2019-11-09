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
      <v-card-title>
        Listado de albaranes

      </v-card-title>
      <CustomerAndDatesFilterForm v-bind:form="filter.form" />
      <v-data-table
        :loading="loading"
        loading-text="Cargando... Por favor, espere"
        :headers="headers"
        :items="deliveryNotes"
        :server-items-length="totalItems"
        :options.sync="options"
      >
        <template v-slot:body="{ items }">
          <tbody v-if="!$vuetify.breakpoint.xsOnly">
            <tr v-for="item in items" :key="item.deliveryNoteItemsHref">
              <td>A{{item.id}}</td>
              <td>{{item.auxDeliveryNoteNr}}</td>
              <td>{{item.customer.alias}}</td>
              <td>{{item.dateFormatted}}</td>
              <td>{{item.total.toFixed(2)}} €</td>
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
                  <span class="black--text">Nº Albarán auxiliar:</span>
                  {{item.auxDeliveryNoteNr}}
                  <br />
                  <span class="black--text">Cliente:</span>
                  {{item.customer.alias}}
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
        {
          text: "Nº pedido",
          sortable: false,
          value: "auxDeliveryNoteNr"
        },
        { text: "Cliente", sortable: false, value: "alias" },
        { text: "Fecha", sortable: false, value: "dateFormatted" },
        { text: "Total", sortable: false, value: "total" },
        { text: "", sortable: false, value: "update" }
      ],
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
      }
    };
  },
  async mounted () {
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
        this.listDeliveryNotes();
      },
      deep: true
    }
  },
  methods: {
    async listDeliveryNotes() {
      this.loading = true;
      var response = await DeliveryNoteService.getAllWithCustomerAndTotal(
        this.filter,
        this.options
      );
      this.deliveryNotes = response.deliveryNotes;
      this.totalItems = response.page.totalElements;
      this.loading = false;
    },
    updateDeliveryNote(item) {
      this.$router.push({
        name: "DeliveryNoteUpdate",
        params: { deliveryNoteId: item.id }
      });
    }
  }
};
</script>