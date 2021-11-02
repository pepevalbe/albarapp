import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const initialToken = localStorage.getItem('token');

if (initialToken) {
    var base64Url = initialToken.split('Bearer ')[1].split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
}
const initialParsedToken = jsonPayload ? JSON.parse(jsonPayload) : null;
const initialStatisticsProductFilter = localStorage.getItem('initialStatisticsProductFilter');
const initialStatisticsNumberOfMonths = localStorage.getItem('initialStatisticsNumberOfMonths');
const initialHensBatch = localStorage.getItem('initialHensBatch');

export const store = new Vuex.Store({
    state: {
        token: initialToken,
        parsedToken: initialParsedToken,
        statisticsProductFilter: initialStatisticsProductFilter ? JSON.parse(initialStatisticsProductFilter) : [],
        statisticsNumberOfMonths: initialStatisticsNumberOfMonths ? JSON.parse(initialStatisticsNumberOfMonths) : 12,
        hensBatch: initialHensBatch
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
        filterStatistics(state, filter) {
            state.statisticsProductFilter = filter.products.productCodes;
            state.statisticsNumberOfMonths = filter.months.numberOfMonths;
            localStorage.setItem("initialStatisticsProductFilter", JSON.stringify(filter.products.productCodes));
            localStorage.setItem("initialStatisticsNumberOfMonths", JSON.stringify(filter.months.numberOfMonths));
        },
        filterReportsByHensBatch(state, hensBatchId) {
            state.hensBatch = hensBatchId;
            localStorage.setItem("initialHensBatch", hensBatchId);
        }
    },
    getters: {
        token: state => state.token,
        authenticated: state => state.token,
        isAdmin: state => state.token && state.parsedToken.roles.includes('ADMIN'),
        isBillingUser: state => state.token && (state.parsedToken.roles.includes('BILLING_USER') || state.parsedToken.roles.includes('ADMIN')),
        isHensBatchUser: state => state.token && (state.parsedToken.roles.includes('HENS_BATCH_USER') || state.parsedToken.roles.includes('ADMIN')),
        parsedToken: state => state.parsedToken,
        statisticsProductFilter: state => state.statisticsProductFilter,
        statisticsNumberOfMonths: state => state.statisticsNumberOfMonths,
        hensBatch: state => state.hensBatch
    }
})