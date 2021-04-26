 <template>
  <div>
    <v-app>
      <WebHeader :currentStateHeaderName="currentRouteHeaderName" :currentState="currentRouteName" />
      <v-main>
        <v-container>
          <router-view :key="$route.path"></router-view>
        </v-container>
      </v-main>
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
    WebFooter,
  },
  data: () => ({}),
  computed: {
    currentRouteHeaderName() {
      return this.$route.meta.headerName;
    },
    currentRouteName() {
      return this.$route.name;
    },
  },
};
</script>
<style>
.v-btn:not(.v-btn--flat):not(.v-btn--text):not(.v-btn--outlined):focus {
  background-color: #1976d2 !important;
  color: white;
}
.v-btn:not(.v-btn--text):not(.v-btn--outlined):focus:before {
  opacity: 0;
}
</style>
