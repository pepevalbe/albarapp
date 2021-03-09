<template>
  <v-container>
    <v-form ref="form" v-model="form.valid">
      <v-subheader class="title ml-1">Datos de lote</v-subheader>
      <v-text-field
        v-model="form.hensBatch.name"
        :counter="40"
        :rules="nameRules"
        autocomplete="off"
        label="Nombre *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.hensBatch.breed"
        :rules="breedRules"
        autocomplete="off"
        label="Raza / Estirpe *"
        required
      ></v-text-field>
      <v-text-field
        v-model="form.hensBatch.animalQuantity"
        type="number"
        :rules="animalQuantityRules"
        autocomplete="off"
        label="Cantidad de animales *"
        required
      ></v-text-field>
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
            v-model="birthDateText"
            ref="dateText"
            label="Fecha de nacimiento *"
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
          v-model="birthDate"
          no-title
          @input="datePickedOnCalendar()"
          locale="es-ES"
          first-day-of-week="1"
        ></v-date-picker>
      </v-menu>
      <v-menu
        ref="menuDatePickerEnd"
        v-model="menuDatePickerEnd"
        :close-on-content-click="false"
        transition="scale-transition"
        offset-y
        max-width="290px"
        min-width="290px"
      >
        <template v-slot:activator="{ on }">
          <v-text-field
            v-model="endDateText"
            ref="dateTextEnd"
            label="Fecha de baja"
            hint="Formato: ddMMaaaa"
            persistent-hint
            autocomplete="off"
            @focus="$event.target.select()"
            prepend-icon="mdi-calendar"
            @blur="parseDateTextEnd()"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker
          v-model="endDate"
          no-title
          @input="datePickedOnCalendarEnd()"
          locale="es-ES"
          first-day-of-week="1"
        ></v-date-picker>
      </v-menu>
    </v-form>
  </v-container>
</template>

<script>
export default {
  name: "HensBatchForm",
  props: {
    form: {
      valid: Boolean,
      hensBatch: {
        name: String,
        breed: String,
        animalQuantity: Number,
        birthTimestamp: Number,
        endTimestamp: Number,
      },
    },
  },
  created() {
    if (this.form?.hensBatch?.birthTimestamp) {
      let moment = this.$moment.utc(
        this.form.hensBatch.birthTimestamp,
        "x",
        true
      );
      this.birthDate = moment.format("YYYY-MM-DD");
      this.birthDateText = moment.format("DD/MM/YYYY");
    }
    if (this.form?.hensBatch?.endTimestamp) {
      let moment = this.$moment.utc(
        this.form.hensBatch.endTimestamp,
        "x",
        true
      );
      this.endDate = moment.format("YYYY-MM-DD");
      this.endDateText = moment.format("DD/MM/YYYY");
    }
  },
  data: () => ({
    nameRules: [
      (v) => !!v || "El nombre es obligatorio",
      (v) =>
        (v && v.length <= 80) || "El nombre debe tener menos de 40 caracteres",
    ],
    breedRules: [(v) => !!v || "La raza es obligatoria"],
    animalQuantityRules: [
      (v) => !!v || "La cantidad de animales es obligatoria",
      (v) => (v && v >= 0) || "La cantidad de animales debe ser mayor a 0",
    ],
    birthDate: "",
    birthDateText: "",
    endDate: "",
    endDateText: "",
    menuDatePicker: false,
    menuDatePickerEnd: false,
  }),
  methods: {
    reset: function () {
      this.$refs.form.reset();
    },
    parseDateText() {
      var moment = this.$moment.utc(
        this.birthDateText,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.birthDate = moment.format("YYYY-MM-DD");
        this.birthDateText = moment.format("DD/MM/YYYY");
        this.form.hensBatch.birthTimestamp = moment.format("x");
      } else {
        this.$nextTick(this.$refs.dateText.focus);
      }
    },
    datePickedOnCalendar() {
      var moment = this.$moment.utc(this.birthDate, "YYYY-MM-DD", true);
      this.birthDateText = moment.format("DD/MM/YYYY");
      this.form.hensBatch.birthTimestamp = moment.format("x");
      this.menuDatePicker = false;
    },
    parseDateTextEnd() {
      var moment = this.$moment.utc(
        this.endDateText,
        ["DDMMYYYY", "DD/MM/YYYY"],
        true
      );
      if (moment.isValid()) {
        this.endDate = moment.format("YYYY-MM-DD");
        this.endDateText = moment.format("DD/MM/YYYY");
        this.form.hensBatch.endTimestamp = moment.format("x");
      } else {
        this.$nextTick(this.$refs.dateTextEnd.focus);
      }
    },
    datePickedOnCalendarEnd() {
      var moment = this.$moment.utc(this.endDate, "YYYY-MM-DD", true);
      this.endDateText = moment.format("DD/MM/YYYY");
      this.form.hensBatch.endTimestamp = moment.format("x");
      this.menuDatePickerEnd = false;
    },
  },
};
</script>