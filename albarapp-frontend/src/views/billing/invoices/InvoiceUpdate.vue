<template>
  <v-flex align-self-start>
    <div v-if="!errorLoading">
      <v-form ref="form" v-model="form.valid">
        <v-subheader class="title ml-1">Datos de factura</v-subheader>
        <v-row justify="center">
          <v-col cols="12" md="2">
            <v-text-field
              ref="invoiceId"
              v-model="form.invoice.id"
              type="text"
              label="Número de factura"
              required
              readonly
              autocomplete="off"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="2">
            <v-text-field
              ref="customerAlias"
              v-model="form.invoice.customerAlias"
              type="text"
              label="Cliente"
              required
              readonly
              autocomplete="off"
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
                  v-model="dateFormattedText"
                  ref="dateText"
                  label="Fecha"
                  hint="Formato: ddMMaaaa"
                  persistent-hint
                  autocomplete="off"
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
              autocomplete="off"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-row>
          <v-col>
            <v-subheader class="title ml-1">Albaranes</v-subheader>
          </v-col>
          <v-col>
            <v-layout text-right wrap class="pt-2 mr-5">
              <v-flex xs12>
                <v-btn @click="showAssociateList()">
                  Asociar
                  <v-icon class="ml-2">mdi-link-variant-plus</v-icon>
                </v-btn>
              </v-flex>
            </v-layout>
          </v-col>
        </v-row>
        <v-data-table
          :loading="loading"
          loading-text="Cargando... Por favor, espere"
          :headers="headers"
          :items="form.deliveryNotes"
        >
          <template v-slot:body="{ items }">
            <tbody v-if="!$vuetify.breakpoint.xsOnly">
              <tr v-for="deliveryNote in items" :key="deliveryNote.id">
                <td>A{{ deliveryNote.id }}</td>
                <td>{{ deliveryNote.auxDeliveryNoteNr }}</td>
                <td>
                  {{
                    $moment
                      .utc(deliveryNote.issuedTimestamp)
                      .format("DD-MM-YYYY")
                  }}
                </td>
                <td>
                  <span
                    v-for="noteItem in deliveryNote.deliveryNoteItems"
                    :key="noteItem.id"
                  >
                    {{ noteItem.quantity }} - {{ noteItem.productName }} -
                    {{ noteItem.price }} €
                    <br />
                  </span>
                </td>
                <td>{{ deliveryNote.total.toFixed(2) }} €</td>
                <td justify="center">
                  <div class="text-xs-center">
                    <v-btn
                      class="ma-2"
                      justify="center"
                      @click="editDeliveryNote(deliveryNote)"
                    >
                      <v-icon dark>mdi-pencil</v-icon>
                    </v-btn>
                  </div>
                </td>
                <td justify="center">
                  <div class="text-xs-center">
                    <v-btn
                      class="ma-2"
                      justify="center"
                      color="red"
                      dark
                      @click="disassociate(deliveryNote)"
                      :disabled="form.deliveryNotes.length <= 1"
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
                    A{{ deliveryNote.id }}
                    <br />
                    <span class="black--text">Nº Pedido:</span>
                    {{ deliveryNote.auxDeliveryNoteNr }}
                    <br />
                    <span class="black--text">Fecha:</span>
                    {{
                      $moment
                        .utc(deliveryNote.issuedTimestamp)
                        .format("DD-MM-YYYY")
                    }}
                    <br />
                    <span class="black--text">Productos:</span>
                    <br />
                    <span
                      v-for="noteItem in deliveryNote.deliveryNoteItems"
                      :key="noteItem.id"
                    >
                      {{ noteItem.quantity }} - {{ noteItem.productName }} -
                      {{ noteItem.price }} €
                      <br />
                    </span>
                    <span class="black--text">Total:</span>
                    {{ deliveryNote.total.toFixed(2) }} €
                    <br />
                  </v-card-text>
                  <v-card-actions>
                    <v-layout text-center wrap>
                      <v-flex xs12>
                        <v-btn
                          class="ma-2"
                          justify="center"
                          @click="editDeliveryNote(deliveryNote)"
                        >
                          <v-icon dark>mdi-pencil</v-icon>
                        </v-btn>
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
          <v-btn class="mr-4" @click="$router.back()">Volver</v-btn>
          <v-btn
            :disabled="!form.valid"
            color="success"
            class="mr-4"
            @click="updateInvoice()"
            >Actualizar</v-btn
          >
        </v-flex>
      </v-layout>
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener la factura, por favor vuelva a cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadInvoice()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-dialog v-model="dialogDisassociate.show" max-width="600">
      <v-card>
        <v-card-title class="headline">¿Desasociar albarán?</v-card-title>
        <v-card-text
          >Si sigue adelante deasociará el albarán A{{
            dialogDisassociate.deliveryNote.id
          }}
          de la factura F{{ dialogDisassociate.invoice.id }}. Esto afectará a
          una factura ya emitida por lo que si ya ha sido presentada debería
          hacer una factura rectificativa.</v-card-text
        >
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" text @click="dialogDisassociate.show = false"
            >Cancelar</v-btn
          >
          <v-btn color="red darken-1" text @click="confirmDisassociate()"
            >Entendido</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogConfirmAssociate.show" max-width="600">
      <v-card>
        <v-card-title class="headline">¿Asociar albarán?</v-card-title>
        <v-card-text
          >Si sigue adelante asociará el albarán A{{
            dialogConfirmAssociate.deliveryNote.id
          }}
          a la factura F{{ dialogConfirmAssociate.invoice.id }}. Esto afectará a
          una factura ya emitida por lo que si ya ha sido presentada debería
          hacer una factura rectificativa.</v-card-text
        >
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="darken-1"
            text
            @click="dialogConfirmAssociate.show = false"
            >Cancelar</v-btn
          >
          <v-btn color="red darken-1" text @click="confirmAssociate()"
            >Entendido</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogAssociate.show" max-width="1200">
      <v-card>
        <v-data-table
          :loading="loading"
          loading-text="Cargando... Por favor, espere"
          :headers="headers"
          :items="dialogAssociate.deliveryNotes"
        >
          <template v-slot:body="{ items }">
            <tbody v-if="!$vuetify.breakpoint.xsOnly">
              <tr
                v-for="deliveryNote in items"
                :key="deliveryNote.id"
              >
                <td>A{{ deliveryNote.id }}</td>
                <td>{{ deliveryNote.auxDeliveryNoteNr }}</td>
                <td>{{ $moment.utc(deliveryNote.issuedTimestamp).format("DD-MM-YYYY") }}</td>
                <td>
                  <span
                    v-for="noteItem in deliveryNote.deliveryNoteItems"
                    :key="noteItem.id"
                  >
                    {{ noteItem.quantity }} - {{ noteItem.productName }} -
                    {{ noteItem.price }} €
                    <br />
                  </span>
                </td>
                <td>{{ deliveryNote.total.toFixed(2) }} €</td>
                <td justify="center">
                  <div class="text-xs-center">
                    <v-btn
                      class="ma-2"
                      justify="center"
                      @click="associate(deliveryNote)"
                    >
                      <v-icon dark>mdi-link-variant</v-icon>
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
                    A{{ deliveryNote.id }}
                    <br />
                    <span class="black--text">Nº Pedido:</span>
                    {{ deliveryNote.auxDeliveryNoteNr }}
                    <br />
                    <span class="black--text">Fecha:</span>
                    {{
                      $moment
                        .utc(deliveryNote.issuedTimestamp)
                        .format("DD-MM-YYYY")
                    }}
                    <br />
                    <span class="black--text">Productos:</span>
                    <br />
                    <span
                      v-for="noteItem in deliveryNote.deliveryNoteItems"
                      :key="noteItem.id"
                    >
                      {{ noteItem.quantity }} - {{ noteItem.productName }} -
                      {{ noteItem.price }} €
                      <br />
                    </span>
                    <span class="black--text">Total:</span>
                    {{ deliveryNote.total.toFixed(2) }} €
                    <br />
                  </v-card-text>
                  <v-card-actions>
                    <v-layout text-center wrap>
                      <v-flex xs12>
                        <v-btn
                          class="ma-2"
                          justify="center"
                          @click="associate(deliveryNote)"
                        >
                          <v-icon dark>mdi-link-variant</v-icon>
                        </v-btn>
                      </v-flex>
                    </v-layout>
                  </v-card-actions>
                </v-card>
              </tr>
            </tbody>
          </template>
        </v-data-table>
        <v-spacer></v-spacer>
        <v-card-actions>
          <v-layout text-center wrap>
            <v-flex xs12>
              <v-btn color="darken-1" text @click="dialogAssociate.show = false"
                >Cancelar</v-btn
              >
            </v-flex>
          </v-layout>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar v-model="snackbar.show" :color="snackbar.color">
      {{ snackbar.message }}
      <v-btn text @click="snackbar.show = false">Cerrar</v-btn>
    </v-snackbar>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
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
          total: 0,
        },
        deliveryNotes: [],
      },
      headers: [
        { text: "Nº Albarán", sortable: true, value: "id" },
        {
          text: "Nº pedido",
          sortable: false,
          value: "auxDeliveryNoteNr",
        },
        { text: "Fecha", sortable: false, value: "dateFormatted" },
        {
          text: "Productos",
          sortable: false,
          value: "deliveryNoteItems",
        },
        { text: "Total", sortable: false, value: "total" },
        { text: "", sortable: false, value: "edit" },
        { text: "", sortable: false, value: "disassociate" },
      ],
      dateFormattedText: "",
      menuDatePicker: false,
      loading: false,
      errorLoading: false,
      snackbar: false,
      dialogDisassociate: {
        show: false,
        deliveryNote: {},
        invoice: {},
      },
      dialogAssociate: {
        show: false,
        deliveryNotes: [],
      },
      dialogConfirmAssociate: {
        show: false,
        deliveryNote: {},
        invoice: {},
      },
      spinner: {
        loading: false,
        counter: 0,
      },
    };
  },
  props: {
    invoiceId: String,
  },
  created() {
    this.loadInvoice();
  },
  methods: {
    async loadInvoice() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.loading = true;
        this.form.invoice = await InvoiceService.get(this.invoiceId);
        this.form.deliveryNotes = await DeliveryNoteService.getByInvoiceId(
          this.invoiceId
        );
        this.form.invoice.date = this.$moment
          .utc(this.form.invoice.issuedTimestamp)
          .format("YYYY-MM-DD");
        this.parseDatePick();
        this.loading = false;
      } catch {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    parseDateText() {
      var moment = this.$moment.utc(
        this.dateFormattedText,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.form.invoice.date = moment.format("YYYY-MM-DD");
        this.dateFormattedText = moment.format("DD/MM/YYYY");
        this.form.invoice.issuedTimestamp = moment.format("x");
      } else {
        this.$nextTick(this.$refs.dateText.focus);
      }
    },
    parseDatePick() {
      var moment = this.$moment.utc(this.form.invoice.date, "YYYY-MM-DD", true);
      this.dateFormattedText = moment.format("DD/MM/YYYY");
      this.form.invoice.issuedTimestamp = moment.format("x");
      this.menuDatePicker = false;
    },
    async updateInvoice() {
      try {
        this.showSpinner();
        await InvoiceService.update(this.form.invoice.id, this.form.invoice);
        this.snackbar = {
          show: true,
          message: "Factura actualizada correctamente.",
          color: "success",
        };
        this.loadInvoice();
      } catch {
        this.snackbar = {
          show: true,
          message:
            "Ha ocurrido un error al intentar modificar la factura, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    disassociate(deliveryNote) {
      this.dialogDisassociate.deliveryNote = deliveryNote;
      this.dialogDisassociate.invoice = this.form.invoice;
      this.dialogDisassociate.show = true;
    },
    editDeliveryNote(deliveryNote) {
      this.$router.push({
        name: "DeliveryNoteUpdate",
        params: { deliveryNoteId: deliveryNote.id.toString() },
      });
    },
    async confirmDisassociate() {
      try {
        this.showSpinner();
        delete this.dialogDisassociate.deliveryNote.invoiceId;
        await DeliveryNoteService.update(this.dialogDisassociate.deliveryNote);
        this.dialogDisassociate.show = false;
        this.snackbar = {
          show: true,
          message: "Albarán desasociado correctamente.",
          color: "success",
        };
        this.loadInvoice();
      } catch {
        this.snackbar = {
          show: true,
          message:
            "Ha ocurrido un error al intentar desasociar el albarán de la factura, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    async showAssociateList() {
      try {
        this.showSpinner();
        this.dialogAssociate.deliveryNotes =
          await DeliveryNoteService.findDeliveryNotesToBill(
            this.form.invoice.customerId
          );
        this.dialogAssociate.show = true;
      } catch {
        this.snackbar = {
          show: true,
          message:
            "Error al consultar los albaranes pendientes, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    associate(deliveryNote) {
      this.dialogAssociate.show = false;
      this.dialogConfirmAssociate.deliveryNote = deliveryNote;
      this.dialogConfirmAssociate.invoice = this.form.invoice;
      this.dialogConfirmAssociate.show = true;
    },
    async confirmAssociate() {
      try {
        this.showSpinner();
        this.dialogConfirmAssociate.deliveryNote.invoiceId =
          this.form.invoice.id;
        await DeliveryNoteService.update(
          this.dialogConfirmAssociate.deliveryNote
        );

        this.dialogConfirmAssociate.show = false;
        this.snackbar = {
          show: true,
          message: "Albarán asociado correctamente.",
          color: "success",
        };
        this.loadInvoice();
      } catch {
        this.snackbar = {
          show: true,
          message:
            "Ha ocurrido un error al intentar asociar el albarán a la factura, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
  },
};
</script>