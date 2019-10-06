<template>
  <v-content>
    <v-container>
      <v-layout text-right wrap class="pb-10 pt-5 mr-5">
        <v-flex xs12>
          <v-btn to="/delivery-note-creation/">
            <span>Nuevo</span>
          </v-btn>
        </v-flex>
      </v-layout>

      <v-card>
        <v-card-title>
          Listado de albaranes
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
          :loading="!deliveryNotes || deliveryNotes.length == 0"
          loading-text="Cargando... Por favor, espere"
          :headers="headers"
          :items="deliveryNotes"
          :search="search"
          :sort-by.sync="sortBy"
          :sort-desc.sync="descending"
          :items-per-page="15"
        >
          <template v-slot:body="{ items }">
            <tbody v-if="!$vuetify.breakpoint.xsOnly">
              <tr v-for="item in items" :key="item.deliveryNoteItemsHref">
                <td>{{item.deliveryNoteNr}}</td>
                <td>{{item.auxDeliveryNoteNr}}</td>
                <td>{{item.alias}}</td>
                <td>{{item.issuedTimestamp}}</td>
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
                <v-card class="flex-content" outlined v-for="item in items" :key="item.deliveryNoteNr">
                  <v-card-text>
                    <span class="black--text">Nº Albarán:</span>
                    {{item.deliveryNoteNr}}
                    <br />
                    <span class="black--text">Nº Albarán auxiliar:</span>
                    {{item.auxDeliveryNoteNr}}
                    <br />
                    <span class="black--text">Cliente:</span>
                    {{item.alias}}
                    <br />
                    <span class="black--text">Fecha:</span>
                    {{item.issuedTimestamp}}
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
      <v-layout text-center wrap class="pt-10">
        <v-flex xs12>
          <v-btn to="/">
            <span>Volver</span>
          </v-btn>
        </v-flex>
      </v-layout>
    </v-container>
  </v-content>
</template>

<script>
export default {
  name: "DeliveryNoteList",
  data: () => {
    return {
      deliveryNotes: [],
      headers: [
        { text: "Nº Albarán", sortable: true, value: "deliveryNoteNr" },
        {
          text: "Nº Albarán auxiliar",
          sortable: false,
          value: "auxDeliveryNoteNr"
        },
        { text: "Cliente", sortable: false, value: "alias" },
        { text: "Fecha", sortable: false, value: "issuedTimestamp" },
        { text: "Total", sortable: false, value: "total" },
        { text: "", sortable: false, value: "update" }
      ],
      search: "",
      sortBy: "deliveryNoteNr",
      descending: false
    };
  },
  created() {
    this.listDeliveryNotes();
  },
  methods: {
    listDeliveryNotes() {
      var vm = this;
      this.$axios
        .get("/deliveryNotes")
        .then(response => {
          this.deliveryNotes = response.data._embedded.deliveryNotes.map(
            function(item) {
              return {
                deliveryNoteNr: item.id,
                auxDeliveryNoteNr: item.auxDeliveryNoteNr,
                alias: "",
                issuedTimestamp: new Date(
                  item.issuedTimestamp
                ).toLocaleDateString("es-ES"),
                total: 0,
                update: "",
                customerHref: item._links.customer.href,
                deliveryNoteItemsHref: item._links.deliveryNoteItems.href
              };
            }
          );
          response.data._embedded.deliveryNotes.forEach(function(item) {
            vm.$axios.get(item._links.customer.href).then(response => {
              vm.mapCustomer(response);
            });
            vm.$axios.get(item._links.deliveryNoteItems.href).then(response => {
              vm.mapDeliveryNoteItems(response);
            });
          });
        })
        .catch(function(error) {
          console.log(error);
          alert("Ha ocurrido un error recuperando los clientes");
        });
    },
    mapCustomer(response) {
      var index = this.deliveryNotes.findIndex(function(element) {
        return element.customerHref === response.config.url;
      });
      this.deliveryNotes[index].alias = response.data.alias;
    },
    mapDeliveryNoteItems(response) {
      var vm = this;
      var index = this.deliveryNotes.findIndex(function(element) {
        return element.deliveryNoteItemsHref === response.config.url;
      });
      this.deliveryNotes[index].total = 0;
      response.data._embedded.deliveryNoteItems.forEach(function(item) {
        vm.deliveryNotes[index].total += item.quantity * item.price;
      });
    },
    updateDeliveryNote(item) {}
  }
};
</script>