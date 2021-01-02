<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-layout text-right wrap class="pt-2 pb-5 mr-5">
        <v-flex xs12>
          <v-btn :to="{ name: 'HensBatchCreation' }">
            Nuevo
            <v-icon class="ml-2">mdi-plus-circle</v-icon>
          </v-btn>
        </v-flex>
      </v-layout>
      <v-card>
        <v-card-title>
          Listado de lotes
          <div class="flex-grow-1"></div>
          <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            label="Buscar ..."
            single-line
            hide-details
            autocomplete="off"
          ></v-text-field>
        </v-card-title>
        <v-data-table
          :loading="!hensBatches || hensBatches.length == 0"
          loading-text="Cargando... Por favor, espere"
          :headers="headers"
          :items="hensBatches"
          :search="search"
          :sort-by.sync="sortBy"
          :sort-desc.sync="descending"
          :items-per-page="15"
        >
          <template v-slot:body="{ items }">
            <tbody v-if="!$vuetify.breakpoint.xsOnly">
              <tr v-for="item in items" :key="item.id">
                <td>{{ item.name }}</td>
                <td>{{ item.breed }}</td>
                <td>{{ item.animalQuantity }}</td>
                <td>{{ dateFormatted(item.birthTimestamp) }}</td>
                <td>
                  <v-btn @click="updateHensBatch(item)">
                    <v-icon dark>mdi-pencil</v-icon>
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
                    <span class="black--text">Nombre:</span>
                    {{ item.name }}
                    <br />
                    <span class="black--text">Raza / Estirpe:</span>
                    {{ item.breed }}
                    <br />
                    <span class="black--text">Número de animales:</span>
                    {{ item.animalQuantity }}
                    <br />
                    <span class="black--text">Fecha de nacimiento:</span>
                    {{ dateFormatted(item.birthTimestamp) }}
                    <br />
                  </v-card-text>
                  <v-card-actions>
                    <v-layout text-center wrap>
                      <v-flex xs12>
                        <v-btn @click="updateHensBatch(item)">
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
    </div>
    <div v-if="errorLoading">
      <v-row class="mb-2" justify="center"
        >Error al obtener los lotes, por favor vuelva a cargar.</v-row
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
  </v-container>
</template>

<script>
import HensBatchService from "@/services/production/HensBatchService.js";

export default {
  name: "HensBatchList",
  data: () => {
    return {
      hensBatches: [],
      headers: [
        { text: "Nombre", sortable: true, value: "name" },
        { text: "Raza / Estirpe", sortable: true, value: "breed" },
        { text: "Número de animales", sortable: true, value: "animalQuantity" },
        {
          text: "Fecha de nacimiento",
          sortable: true,
          value: "birthTimestamp",
        },
        { text: "", sortable: false, value: "update" },
      ],
      search: "",
      sortBy: "birthTimestamp",
      descending: true,
      errorLoading: false,
      spinner: {
        loading: false,
        counter: 0,
      },
    };
  },
  async created() {
    this.loadHensBatches();
  },
  methods: {
    async loadHensBatches() {
      try {
        this.showSpinner();
        this.errorLoading = false;
        this.hensBatches = await HensBatchService.getAll();
      } catch (e) {
        this.errorLoading = true;
      } finally {
        this.closeSpinner();
      }
    },
    updateHensBatch(item) {
      this.$router.push({
        name: "HensBatchUpdate",
        params: { hensBatchId: item.id },
      });
    },
  },
};
</script>
