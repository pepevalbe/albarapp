<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-layout text-right wrap class="pt-2 pb-5 mr-5">
        <v-flex xs12>
          <v-btn :to="toExpenseCreation()" :disabled="!hensBatch">
            Nuevo
            <v-icon class="ml-2">mdi-plus-circle</v-icon>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-card>
        <v-card-title>
          Gastos
          <div class="flex-grow-1"></div>
          <v-select
            v-model="hensBatch"
            label="Seleccione un lote"
            :items="hensBatches"
            item-text="name"
            return-object
            no-data-text="Sin coincidencias"
            class="mr-5 ml-5"
            @change="selectHensBatchByName()"
          ></v-select>
        </v-card-title>
        <v-data-table
          :loading="!hensBatchExpenses"
          loading-text="Cargando... Por favor, espere"
          :headers="headers"
          :items="hensBatchExpenses"
          :search="search"
          :sort-by.sync="sortBy"
          :sort-desc.sync="descending"
          :items-per-page="15"
        >
          <template v-slot:body="{ items }">
            <tbody
              v-if="!$vuetify.breakpoint.xsOnly"
              style="text-align: center"
            >
              <tr v-for="item in items" :key="item.code">
                <td>
                  {{ dateFormatted(item.expenseTimestamp) }}
                </td>
                <td>{{ item.description }}</td>
                <td>{{ item.value }}</td>
                <td>{{ booleanFormatted(item.recurrent) }}</td>
                <td>{{ booleanFormatted(item.distribution) }}</td>
                <td>
                  <v-btn @click="updateHensBatchExpense(item)">
                    <v-icon dark>mdi-pencil</v-icon>
                  </v-btn>
                </td>
                <td>
                  <v-btn
                    v-if="!item.invoiceId"
                    color="red"
                    dark
                    @click="openDeleteDialog(item)"
                  >
                    <v-icon dark>mdi-delete</v-icon>
                  </v-btn>
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr>
                <v-card
                  class="flex-content"
                  outlined
                  v-for="item in items"
                  :key="item.id"
                >
                  <v-card-text>
                    <span class="black--text">Fecha:</span>
                    {{ dateFormatted(item.expenseTimestamp) }}
                    <br />
                    <span class="black--text">Concepto:</span>
                    {{ item.description }}
                    <br />
                    <span class="black--text">Valor:</span>
                    {{ item.value }}
                    <br />
                    <span class="black--text">Recurrente:</span>
                    {{ booleanFormatted(item.recurrent) }}
                    <br />
                    <span class="black--text">Distribución:</span>
                    {{ booleanFormatted(item.distribution) }}
                    <br />
                  </v-card-text>
                  <v-card-actions>
                    <v-layout text-center wrap>
                      <v-flex xs12>
                        <v-btn @click="updateHensBatchExpense(item)">
                          <v-icon dark>mdi-pencil</v-icon>
                        </v-btn>
                        <v-btn color="red" dark @click="openDeleteDialog(item)">
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
      <v-row class="mb-2" justify="center"
        >Error al obtener los reportes diarios, por favor vuelva a
        cargar.</v-row
      >
      <v-row justify="center">
        <v-btn @click="loadHensBatches()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
    <v-overlay v-if="spinner.loading" :value="true">
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-overlay>
    <v-dialog v-model="dialogDelete.show" max-width="600" v-if="hensBatch">
      <v-card>
        <v-card-title class="headline">¿Eliminar reporte diario?</v-card-title>
        <v-card-text
          >Si sigue adelante eliminará el reporte diario con fecha
          {{ dateFormatted(dialogDelete.expense.expenseTimestamp) }} del lote
          {{ hensBatch.name }} y concepto
          {{ dialogDelete.expense.description }}. Este proceso es
          irreversible.</v-card-text
        >
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="darken-1" text @click="dialogDelete.show = false"
            >Cancelar</v-btn
          >
          <v-btn
            color="red darken-1"
            text
            @click="deleteHensBatchExpense(dialogDelete.expense)"
            >Confirmar</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import HensBatchService from "@/services/production/HensBatchService.js";
import HensBatchExpenseService from "@/services/production/HensBatchExpenseService.js";

export default {
  name: "HensBatchExpenseList",
  data: () => {
    return {
      hensBatchExpenses: [],
      hensBatches: [],
      hensBatch: null,
      headers: [
        {
          text: "Fecha",
          sortable: true,
          align: "center",
          value: "expenseTimestamp",
        },
        {
          text: "Concepto",
          sortable: false,
          align: "center",
          value: "description",
        },
        { text: "Valor", sortable: false, align: "center", value: "value" },
        {
          text: "Recurrente",
          sortable: false,
          align: "center",
          value: "recurrent",
        },
        {
          text: "Distribución",
          sortable: false,
          align: "center",
          value: "distribution",
        },
        { text: "", sortable: false, align: "center", value: "update" },
        { text: "", sortable: false, align: "center", value: "delete" },
      ],
      search: "",
      sortBy: "expenseTimestamp",
      descending: true,
      dialogDelete: {
        show: false,
        expense: {},
      },
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0,
      },
    };
  },
  async created() {
    await this.loadHensBatches();
    var vm = this;
    this.hensBatch = this.hensBatches.find(function (hensBatch) {
      return hensBatch.id === vm.$store.getters.hensBatch;
    });
    if (this.hensBatch) this.selectHensBatchByName();
  },
  methods: {
    async loadHensBatches() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatches = await HensBatchService.getAll();
        this.hensBatches.sort((a, b) => b.birthTimestamp - a.birthTimestamp);
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    async selectHensBatchByName() {
      if (this.hensBatch) {
        try {
          this.showSpinner();
          this.errorLoading = false;
          this.$store.commit("filterReportsByHensBatch", this.hensBatch.id);
          this.hensBatchExpenses =
            await HensBatchExpenseService.getByHensBatchId(this.hensBatch.id);
        } catch (e) {
          this.errorLoading = true;
        } finally {
          this.closeSpinner();
        }
      } else {
        this.hensBatchExpenses = [];
        this.$store.commit("filterReportsByHensBatch", null);
      }
    },
    updateHensBatchExpense(item) {
      this.$router.push({
        name: "HensBatchExpenseUpdate",
        params: {
          hensBatchExpenseId: item.id,
        },
      });
    },
    toExpenseCreation() {
      if (this.hensBatch) {
        return {
          name: "HensBatchExpenseCreation",
          params: { hensBatchId: this.hensBatch.id },
        };
      }
    },
    openDeleteDialog(item) {
      this.dialogDelete.expense = item;
      this.dialogDelete.show = true;
    },
    async deleteHensBatchExpense(item) {
      try {
        this.dialogDelete.show = false;
        this.showSpinner();
        await HensBatchExpenseService.delete(item.id);
        this.snackbar = {
          show: true,
          message: "Gasto eliminado correctamente",
          color: "success",
        };
        this.selectHensBatchByName();
      } catch {
        this.snackbar = {
          show: true,
          message:
            "No se ha podido eliminar el reporte diario, por favor vuelva a intentarlo.",
          color: "error",
        };
      } finally {
        this.closeSpinner();
      }
    },
    calculateWeek(timestamp) {
      var current = this.$moment.utc(timestamp, "x", true);
      var born = this.$moment.utc(this.hensBatch.birthTimestamp, "x", true);
      return current.diff(born, "weeks") + 1;
    },
  },
};
</script>
