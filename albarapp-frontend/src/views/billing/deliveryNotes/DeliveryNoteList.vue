<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-layout text-right wrap class="pt-2 pb-5 mr-5">
        <v-flex xs12>
          <v-btn :to="{name: 'DeliveryNoteCreation'}">
            Nuevo
            <v-icon class="ml-2">mdi-plus-circle</v-icon>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-card>
        <v-card-title>Listado de albaranes</v-card-title>
        <v-row>
          <v-col cols="12" md="9">
            <CustomerAndDatesFilterForm :form="filter.form" />
          </v-col>
          <v-col cols="12" md="3">
            <AuxDeliveryNoteNrFilter v-if="filter" :filter="filter" />
          </v-col>
        </v-row>
        <v-data-table
          :loading="loading"
          loading-text="Cargando... Por favor, espere"
          :headers="getHeaders()"
          :footer-props="footerProps"
          :items="deliveryNotes"
          :server-items-length="totalItems"
          :options.sync="options"
          multi-sort
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
                <td>
                  <v-btn v-if="!item.invoiceId" color="red" dark @click="openDeleteDialog(item)">
                    <v-icon dark>mdi-delete</v-icon>
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
                        <v-btn
                          v-if="!item.invoiceId"
                          color="red"
                          dark
                          @click="openDeleteDialog(item)"
                        >
                          <v-icon dark>mdi-delete</v-icon>
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
      <v-row
        class="mb-2"
        justify="center"
      >Error al obtener los albaranes, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="listDeliveryNotes()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{snackbar.message}}
      <v-btn text @click="snackbar.show=false">Cerrar</v-btn>
    </v-snackbar>
    <v-dialog v-model="dialogDelete.show" max-width="600">
      <v-card>
        <v-card-title class="headline">¿Eliminar albarán?</v-card-title>
        <v-card-text>Si sigue adelante eliminará el albarán A{{dialogDelete.deliveryNote.id}}. Este proceso es irreversible.</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" text @click="dialogDelete.show = false">Cancelar</v-btn>
          <v-btn
            color="red darken-1"
            text
            @click="deleteDeliveryNote(dialogDelete.deliveryNote)"
          >Confirmar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import DeliveryNoteService from "@/services/DeliveryNoteService.js";
import CustomerAndDatesFilterForm from "@/components/CustomerAndDatesFilterForm";
import AuxDeliveryNoteNrFilter from "@/components/AuxDeliveryNoteNrFilter";

export default {
  name: "DeliveryNoteList",
  components: {
    CustomerAndDatesFilterForm,
    AuxDeliveryNoteNrFilter,
  },
  data: () => {
    return {
      deliveryNotes: [],
      netTotal: 0,
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
      snackbar: {
        show: false,
        message: "",
        color: "",
      },
      totalItems: 0,
      filter: {
        form: {
          valid: true,
          customerCode: "",
          dateFrom: "",
          dateTo: "",
        },
        auxDeliveryNoteNr: "",
      },
      spinner: {
        loading: false,
        counter: 0,
      },
      dialogDelete: {
        show: false,
        deliveryNote: {},
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
      if (this.$route.query.auxNr) this.filter.auxDeliveryNoteNr = this.$route.query.auxNr;
      if (this.$route.query.page)
        this.options.page = Number(this.$route.query.page);
      if (this.$route.query.itemsPerPage)
        this.options.itemsPerPage = Number(this.$route.query.itemsPerPage);
      if (this.$route.query.sortBy)
        this.options.sortBy = this.$route.query.sortBy.split(",");
      if (this.$route.query.sortDesc)
        this.options.sortDesc = this.$route.query.sortDesc
          .split(",")
          .map((e) => e === "true");
    }
    await this.listDeliveryNotes();
    this.$watch("options", this.listDeliveryNotes, { deep: true });
    this.$watch(
      "filter",
      function () {
        if (this.options.page != 1) this.options.page = 1;
        else this.listDeliveryNotes();
      },
      { deep: true }
    );
  },
  methods: {
    async listDeliveryNotes() {
      try {
        this.loading = true;
        this.errorLoading = false;
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
        this.updateURL();
      } catch {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    updateDeliveryNote(item) {
      this.$router.push({
        name: "DeliveryNoteUpdate",
        params: { deliveryNoteId: item.id.toString() },
      });
    },
    openDeleteDialog(item) {
      this.dialogDelete.deliveryNote = item;
      this.dialogDelete.show = true;
    },
    async deleteDeliveryNote(item) {
      try {
        this.dialogDelete.show = false;
        this.showSpinner();
        await DeliveryNoteService.delete(item);
        this.snackbar = {
          show: true,
          message: "Albarán eliminado correctamente",
          color: "success",
        };
        this.listDeliveryNotes();
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido eliminar el albarán, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    getHeaders() {
      var headers = [
        { text: "Nº Albarán", sortable: true, value: "id" },
        { text: "Nº Factura", sortable: true, value: "invoice.id" },
        {
          text: "Nº pedido",
          sortable: true,
          value: "auxDeliveryNoteNr",
        },
        { text: "Cliente", sortable: true, value: "customer.code" },
        { text: "Fecha", sortable: true, value: "issuedTimestamp" },
        { text: "Productos", sortable: false, value: "products" },
        {
          text: "Total: " + this.currencyFormatted(this.netTotal),
          sortable: false,
          value: "total",
        },
        { text: "", sortable: false, value: "update" },
        { text: "", sortable: false, value: "delete" },
      ];
      return headers;
    },
    updateURL() {
      var query = {};
      if (this.filter.form.customerCode)
        query.customerCode = this.filter.form.customerCode;
      if (this.filter.form.dateFrom) query.from = this.filter.form.dateFrom;
      if (this.filter.form.dateTo) query.to = this.filter.form.dateTo;
      if (this.filter.auxDeliveryNoteNr) query.auxNr = this.filter.auxDeliveryNoteNr;
      if (this.options.page) query.page = this.options.page;
      if (this.options.itemsPerPage)
        query.itemsPerPage = this.options.itemsPerPage;
      if (this?.options?.sortBy?.length)
        query.sortBy = this.options.sortBy.join(",");
      if (this?.options?.sortDesc?.length)
        query.sortDesc = this.options.sortDesc.join(",");
      this.$router
        .push({
          path: this.$route.path,
          query: query,
        })
        .catch(() => {});
    },
  },
};
</script>