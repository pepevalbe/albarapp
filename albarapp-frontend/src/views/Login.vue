<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="4">
        <v-card>
          <LoginForm />
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import LoginForm from "@/components/LoginForm";

export default {
  name: "Login",
  components: {
    LoginForm,
  },
};
</script>