 <template>
  <div>
    <v-app>
      <WebHeader v-bind:currentState="currentRouteName" />
      <v-content>
        <v-container>
          <router-view></router-view>
        </v-container>
      </v-content>
      <WebFooter />
    </v-app>
  </div>
</template>

<script>
import WebHeader from "@/components/TheHeader";
import WebFooter from "@/components/TheFooter";

export default {
  name: "App",
  components: {
    WebHeader,
    WebFooter
  },
  data: () => ({}),
  computed: {
    currentRouteName() {
      return this.$route.meta.headerName;
    }
  }
};
</script>
