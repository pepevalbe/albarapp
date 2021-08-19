<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1"
        >Gastos del lote {{ hensBatch.name }}</v-subheader
      >
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
            v-model="dateText"
            ref="dateText"
            label="Fecha *"
            hint="Formato: ddMMaaaa"
            persistent-hint
            autocomplete="off"
            @focus="$event.target.select()"
            prepend-icon="mdi-calendar"
            @blur="parseDateText()"
            @keypress.enter="
              $refs.description.focus();
              menuDatePicker = false;
            "
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker
          v-model="date"
          no-title
          @input="datePickedOnCalendar()"
          locale="es-ES"
          first-day-of-week="1"
        ></v-date-picker>
      </v-menu>
      <v-row class="mr-6 ml-6 mt-6">
        <v-text-field
          v-model="form.hensBatchExpense.description"
          ref="description"
          type="text"
          :rules="descriptionRules"
          autocomplete="off"
          label="Concepto *"
          required
          autofocus
          @focus="$event.target.select()"
          @keypress.enter="$refs.value.focus()"
        ></v-text-field>
      </v-row>
      <v-row class="mr-6 ml-6 mt-6">
        <v-text-field
          v-model="form.hensBatchExpense.value"
          ref="value"
          type="number"
          :rules="valueRules"
          autocomplete="off"
          label="Importe *"
          required
          @focus="$event.target.select()"
          @keypress.enter="$refs.numL.focus()"
        ></v-text-field>
      </v-row>
      <v-row class="mr-6 ml-6 mt-6">
        <v-switch
          class="mr-2"
          v-model="form.hensBatchExpense.recurrent"
          label="Recurrente"
        ></v-switch>
      </v-row>
      <v-row class="mr-6 ml-6 mt-6">
        <v-switch
          class="mr-2"
          v-model="form.hensBatchExpense.distribution"
          label="Gasto de distribución"
        ></v-switch>
      </v-row>
    </v-form>
  </v-container>
</template>

<script>
export default {
  name: "HensBatchExpenseForm",
  props: {
    form: {
      valid: Boolean,
      hensBatchExpense: {
        expenseTimestamp: Number,
        description: String,
        value: Number,
        recurrent: Boolean,
        distribution: Boolean,
      },
    },
    hensBatch: {
      id: String,
      name: String,
      breed: String,
      animalQuantity: Number,
      birthTimestamp: Number,
    },
  },
  created() {
    if (this.form?.hensBatchExpense?.expenseTimestamp) {
      let moment = this.$moment.utc(
        this.form.hensBatchExpense.expenseTimestamp,
        "x",
        true
      );
      this.date = moment.format("YYYY-MM-DD");
      this.dateText = moment.format("DD/MM/YYYY");
    } else {
      let moment = this.$moment.utc().startOf("day");
      this.date = moment.format("YYYY-MM-DD");
      this.dateText = moment.format("DD/MM/YYYY");
      this.form.hensBatchExpense.expenseTimestamp = moment.format("x");
    }
  },
  data: () => ({
    descriptionRules: [(v) => !!v || "El concepto es obligatoria"],
    valueRules: [(v) => !!v || "El importe es obligatorio"],
    date: "",
    dateText: "",
    menuDatePicker: false,
  }),
  methods: {
    reset: function () {
      this.form.hensBatchExpense.description = "";
      this.form.hensBatchExpense.value = "0";
      this.$nextTick(this.$refs.dateText.focus);
    },
    parseDateText() {
      var moment = this.$moment.utc(
        this.dateText,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.date = moment.format("YYYY-MM-DD");
        this.dateText = moment.format("DD/MM/YYYY");
        this.form.hensBatchExpense.expenseTimestamp = moment.format("x");
      } else {
        this.$nextTick(this.$refs.dateText.focus);
      }
    },
    datePickedOnCalendar() {
      var moment = this.$moment.utc(this.date, "YYYY-MM-DD", true);
      this.dateText = moment.format("DD/MM/YYYY");
      this.form.hensBatchExpense.expenseTimestamp = moment.format("x");
      this.menuDatePicker = false;
    },
  },
};
</script>