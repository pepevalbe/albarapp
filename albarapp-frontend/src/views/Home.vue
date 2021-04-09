<template>
  <v-container>
    <v-row justify="center">
      <div class="text-h3 text-md-h2 text-center mb-10 mt-5">Software de gestión de explotaciones ganaderas</div>
    </v-row>
    <v-row justify="center">
      <v-btn
        height="150px"
        width="150px"
        class="ml-2 mr-2 mt-2 mb-2"
        outlined
        tile
        x-large
        :to="{ name: 'DeliveryNoteList' }"
      >
        <v-col cols="12">
          <v-row
            width="100%"
            style="place-content: center; margin-bottom: 12px"
          >
            <v-icon large>mdi-basket</v-icon>
          </v-row>
          <v-row width="100%" style="place-content: center">
            <span>Albaranes</span>
          </v-row>
        </v-col>
      </v-btn>

      <v-btn
        height="150px"
        width="150px"
        class="ml-2 mr-2 mt-2 mb-2"
        outlined
        tile
        x-large
        :to="{ name: 'InvoiceList' }"
      >
        <v-col cols="12">
          <v-row
            width="100%"
            style="place-content: center; margin-bottom: 12px"
          >
            <v-icon large>mdi-cash</v-icon>
          </v-row>
          <v-row width="100%" style="place-content: center">
            <span>Facturas</span>
          </v-row>
        </v-col>
      </v-btn>

      <v-btn
        height="150px"
        width="150px"
        class="ml-2 mr-2 mt-2 mb-2"
        outlined
        tile
        x-large
        :to="{ name: 'HensBatchReportList' }"
      >
        <v-col cols="12">
          <v-row
            width="100%"
            style="place-content: center; margin-bottom: 12px"
          >
            <v-icon large>mdi-clipboard-list-outline</v-icon>
          </v-row>
          <v-row width="100%" style="place-content: center">
            <span>Reportes</span>
          </v-row>
        </v-col>
      </v-btn>

      <v-btn
        height="150px"
        width="150px"
        class="ml-2 mr-2 mt-2 mb-2"
        outlined
        tile
        x-large
        :to="{ name: 'HensBatchReportChart' }"
      >
        <v-col cols="12">
          <v-row
            width="100%"
            style="place-content: center; margin-bottom: 12px"
          >
            <v-icon large>mdi-chart-bell-curve-cumulative</v-icon>
          </v-row>
          <v-row width="100%" style="place-content: center">
            <span>Gráficas</span>
          </v-row>
        </v-col>
      </v-btn>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: "Home",
};
</script>