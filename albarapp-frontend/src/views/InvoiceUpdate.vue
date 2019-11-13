<template>
  <v-flex align-self-start>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1">Datos de factura</v-subheader>
      <v-row justify="center">
        <v-col cols="12" md="2">
          <v-text-field
            ref="customerAlias"
            v-model="form.invoice.deliveryNotes[0].customer.alias"
            type="text"
            label="Cliente"
            required
            readonly
          ></v-text-field>
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
                v-on="on"
              ></v-text-field>
            </template>
            <v-date-picker
              v-model="form.invoice.date"
              no-title
              @input="parseDatePick()"
              locale="es-ES"
              first-day-of-week="1"
            ></v-date-picker>
          </v-menu>
        </v-col>
      </v-row>
      <v-subheader class="title ml-1">Albaranes</v-subheader>
      <v-data-table
        :loading="loading"
        loading-text="Cargando... Por favor, espere"
        :headers="headers"
        :items="form.invoice.deliveryNotes"
      >
        <template v-slot:body="{ items }">
          <tbody v-if="!$vuetify.breakpoint.xsOnly">
            <tr v-for="item in items" :key="item.deliveryNoteItemsHref">
              <td>A{{item.id}}</td>
              <td>{{item.auxDeliveryNoteNr}}</td>
              <td>{{item.dateFormatted}}</td>
              <td>{{item.deliveryNoteTotal.value.toFixed(2)}} €</td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr>
              <v-card class="flex-content" outlined v-for="item in items" :key="item.id">
                <v-card-text>
                  <span class="black--text">Nº Albarán:</span>
                  A{{item.id}}
                  <br />
                  <span class="black--text">Nº Pedido:</span>
                  {{item.auxDeliveryNoteNr}}
                  <br />
                  <span class="black--text">Fecha:</span>
                  {{item.dateFormatted}}
                  <br />
                  <span class="black--text">Total:</span>
                  {{item.deliveryNoteTotal.value.toFixed(2)}} €
                  <br />
                </v-card-text>
              </v-card>
            </tr>
          </tbody>
        </template>
      </v-data-table>
    </v-form>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn
          :disabled="!form.valid"
          color="success"
          class="mr-4"
          @click="updateInvoice()"
        >Actualizar</v-btn>
        <v-btn to="/invoice-list/">Volver</v-btn>
      </v-flex>
    </v-layout>
    <v-snackbar v-model="snackbar">
      Factura actualizada correctamente
      <v-btn color="green" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
  </v-flex>
</template>

<script>
import InvoiceService from "@/services/InvoiceService.js";

export default {
  name: "InvoiceUpdate",
  data: () => {
    return {
      form: {
        valid: false,
        invoice: {
          deliveryNotes: [
            {
              customer: {
                alias: ""
              },
              deliveryNoteTotal: {
                value: 0
              }
            }
          ]
        }
      },
      headers: [
        { text: "Nº Albarán", sortable: true, value: "id" },
        {
          text: "Nº pedido",
          sortable: false,
          value: "auxDeliveryNoteNr"
        },
        { text: "Fecha", sortable: false, value: "dateFormatted" },
        { text: "Total", sortable: false, value: "total" }
      ],
      dateFormatted: "",
      menuDatePicker: false,
      loading: false,
      snackbar: false
    };
  },
  props: {
    invoiceId: Number
  },
  created() {
    this.loadInvoice();
  },
  methods: {
    async loadInvoice() {
      this.loading = true;
      this.form.invoice = await InvoiceService.getWithCustomerAndTotal(
        this.invoiceId
      );
      this.parseDatePick();
      this.loading = false;
    },
    parseDateText() {
      var moment = this.$moment(
        this.dateFormatted,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.invoice.date = moment.format("YYYY-MM-DD");
        this.dateFormatted = moment.format("DD/MM/YYYY");
        this.form.invoice.issuedTimestamp = moment.format("x");
      } else {
        this.$nextTick(this.$refs.dateText.focus);
      }
    },
    parseDatePick() {
      var moment = this.$moment(this.form.invoice.date, "YYYY-MM-DD", true);
      this.dateFormatted = moment.format("DD/MM/YYYY");
      this.form.invoice.issuedTimestamp = moment.format("x");
      this.menuDatePicker = false;
    }
  }
};
</script>