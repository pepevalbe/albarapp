import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const initialToken = localStorage.getItem('token');
var initialParsedToken;
if (initialToken) {
    var base64Url = initialToken.split('Bearer ')[1].split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    initialParsedToken = JSON.parse(jsonPayload);
}

export const store = new Vuex.Store({
    state: {
        token: initialToken,
        parsedToken: initialParsedToken,
        chartProductFilter: []
    },
    mutations: {
        login(state, token) {
            state.token = token;
            localStorage.setItem("token", token);
            var base64Url = state.token.split('Bearer ')[1].split('.')[1];
            var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            var jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            state.parsedToken = JSON.parse(jsonPayload);
        },
        logout(state) {
            state.token = null;
            localStorage.clear();
        },
        filterChart(state, products) {
            state.chartProductFilter = products;
        }
    },
    getters: {
        token: state => state.token,
        authenticated: state => state.token != null,
        parsedToken: state => state.parsedToken,
        chartProductFilter: state => state.chartProductFilter
    }
})