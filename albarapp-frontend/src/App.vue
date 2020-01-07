 <template>
  <div>
    <v-app>
      <WebHeader v-bind:currentStateHeaderName="currentRouteHeaderName" v-bind:currentState="currentRouteName" />
      <v-content>
        <v-container>
          <router-view :key="$route.fullPath"></router-view>
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
    currentRouteHeaderName() {
      return this.$route.meta.headerName;
    },
    currentRouteName() {
      return this.$route.name;
    },
  }
};
</script>
