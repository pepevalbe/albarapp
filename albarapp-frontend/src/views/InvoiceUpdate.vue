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
        <v-col cols="12" md="1">
          <v-text-field
            ref="total"
            v-model="form.invoice.total"
            type="text"
            label="Total"
            suffix=" €"
            required
            readonly
          ></v-text-field>
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
            <tr v-for="deliveryNote in items" :key="deliveryNote.deliveryNoteItemsHref">
              <td>A{{deliveryNote.id}}</td>
              <td>{{deliveryNote.auxDeliveryNoteNr}}</td>
              <td>{{deliveryNote.dateFormatted}}</td>
              <td>
                <span v-for="noteItem in deliveryNote.deliveryNoteItems" :key="noteItem.id">
                  {{noteItem.quantity}} - {{noteItem.product.name}} - {{noteItem.price}} €
                  <br />
                </span>
              </td>
              <td>{{deliveryNote.deliveryNoteTotal.value.toFixed(2)}} €</td>
              <td justify="center">
                <div class="text-xs-center">
                  <v-btn
                    class="ma-2"
                    justify="center"
                    color="red"
                    dark
                    @click="disassociate(deliveryNote)"
                    :disabled="form.invoice.deliveryNotes.length<=1"
                  >
                    <v-icon dark>mdi-link-variant-off</v-icon>
                  </v-btn>
                </div>
              </td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr>
              <v-card
                class="flex-content"
                outlined
                v-for="deliveryNote in items"
                :key="deliveryNote.id"
              >
                <v-card-text>
                  <span class="black--text">Nº Albarán:</span>
                  A{{deliveryNote.id}}
                  <br />
                  <span class="black--text">Nº Pedido:</span>
                  {{deliveryNote.auxDeliveryNoteNr}}
                  <br />
                  <span class="black--text">Fecha:</span>
                  {{deliveryNote.dateFormatted}}
                  <br />
                  <span class="black--text">Productos:</span>
                  <br />
                  <span v-for="noteItem in deliveryNote.deliveryNoteItems" :key="noteItem.id">
                    {{noteItem.quantity}} - {{noteItem.product.name}} - {{noteItem.price}} €
                    <br />
                  </span>
                  <span class="black--text">Total:</span>
                  {{deliveryNote.deliveryNoteTotal.value.toFixed(2)}} €
                  <br />
                </v-card-text>
                <v-card-actions>
                  <v-layout text-center wrap>
                    <v-flex xs12>
                      <v-btn
                        class="ma-2"
                        justify="center"
                        color="red"
                        dark
                        @click="disassociate(deliveryNote)"
                      >
                        <v-icon dark>mdi-link-variant-off</v-icon>
                      </v-btn>
                    </v-flex>
                  </v-layout>
                </v-card-actions>
              </v-card>
            </tr>
          </tbody>
        </template>
      </v-data-table>
    </v-form>
    <v-layout text-center wrap class="pt-10">
      <v-flex xs12>
        <v-btn class="mr-4" to="/invoice-list/">Volver</v-btn>
        <v-btn
          :disabled="!form.valid"
          color="success"
          class="mr-4"
          @click="updateInvoice()"
        >Actualizar</v-btn>
      </v-flex>
    </v-layout>
    <v-snackbar v-model="snackbar">
      Factura actualizada correctamente
      <v-btn color="green" text @click="snackbar = false">Cerrar</v-btn>
    </v-snackbar>
    <v-dialog v-model="dialog.show" max-width="600">
      <v-card>
        <v-card-title class="headline">¿Desasociar albarán?</v-card-title>
        <v-card-text>Si sigue adelante deasociará el albarán A{{dialog.deliveryNote.id}} de la factura F{{dialog.invoice}}. Esto afectará a una factura ya emitida por lo que si ya ha sido presentada debería hacer una factura rectificativa.</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" text @click="dialog.show = false">Cancelar</v-btn>
          <v-btn color="red darken-1" text @click="confirmDisassociate()">Entendido</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-flex>
</template>

<script>
import InvoiceService from "@/services/InvoiceService.js";
import DeliveryNoteService from "@/services/DeliveryNoteService.js";

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
          ],
          total: 0
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
        {
          text: "Productos",
          sortable: false,
          value: "deliveryNoteItems"
        },
        { text: "Total", sortable: false, value: "total" },
        { text: "", sortable: false, value: "disassociate" }
      ],
      dateFormatted: "",
      menuDatePicker: false,
      loading: false,
      snackbar: false,
      dialog: {
        show: false,
        deliveryNote: "",
        invoice: ""
      }
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
      this.form.invoice.total = this.form.invoice.total.toFixed(2);
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
    },
    disassociate(deliveryNote) {
      this.dialog.deliveryNote = deliveryNote;
      this.dialog.invoice = this.form.invoice.id;
      this.dialog.show = true;
    },
    async confirmDisassociate() {
      await DeliveryNoteService.disassociateInvoice(
        this.dialog.deliveryNote.id
      );
      this.snackbar = true;
      this.loadInvoice(this.invoiceId);
      this.dialog.show = false;
    }
  }
};
</script>