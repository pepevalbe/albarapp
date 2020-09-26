<template>
  <v-container>
    <div v-if="!errorLoading">
      <v-card v-if="questionReady" class="mt-2">
        <v-card-title class="mb-4">
          <v-icon large class="mr-2">mdi-comment-question</v-icon>
          {{ question }}
        </v-card-title>
        <v-card-text v-for="(answer, index) in answers" :key="index">
          <v-btn v-if="answerStatus[index] == 0" @click="checkAnswer(index)">{{ answer }}</v-btn>
          <v-btn v-if="answerStatus[index] == 1" text>
            {{ answer }}
            <v-icon color="error" class="ml-4">mdi-close-circle</v-icon>
          </v-btn>
          <v-btn v-if="answerStatus[index] == 2">
            {{ answer }}
            <v-icon color="success" class="ml-4">mdi-check-circle</v-icon>
          </v-btn>
        </v-card-text>
      </v-card>
    </div>
    <div v-if="errorLoading">
      <v-row
        class="mb-2"
        justify="center"
      >Error al obtener las preguntas, por favor vuelva a cargar.</v-row>
      <v-row justify="center">
        <v-btn @click="getQuestion()">
          <v-icon dark>mdi-refresh</v-icon>
        </v-btn>
      </v-row>
    </div>
  </v-container>
</template>

<script>
import HttpClient from "@/services/HttpClient.js";

export default {
  name: "TriviaCard",
  data: () => {
    return {
      question: String,
      rightAnswer: String,
      errorLoading: false,
      answers: [],
      answerStatus: [],
      questionReady: false,
    };
  },
  created() {
    this.getQuestion();
  },
  methods: {
    getQuestion: function () {
      this.errorLoading = false;
      HttpClient.get("api/trivia")
        .then((response) => {
          var answers = [
            decodeURIComponent(response.data.results[0].correct_answer),
          ];
          response.data.results[0].incorrect_answers.forEach(function (item) {
            answers.push(decodeURIComponent(item));
          });
          this.shuffleArray(answers);
          this.question = decodeURIComponent(response.data.results[0].question);
          this.rightAnswer = decodeURIComponent(
            response.data.results[0].correct_answer
          );
          this.answers = answers;
          this.answerStatus = [0, 0, 0, 0];
          this.questionReady = true;
        })
        .catch(() => {
          this.errorLoading = true;
        });
    },
    checkAnswer: function (index) {
      if (this.answers[index] == this.rightAnswer) {
        this.$set(this.answerStatus, index, 2);
        var vm = this;
        setTimeout(function () {
          vm.getQuestion();
        }, 1000);
      } else {
        this.$set(this.answerStatus, index, 1);
      }
    },
    shuffleArray: function (array) {
      for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
      }
    },
  },
};
</script>