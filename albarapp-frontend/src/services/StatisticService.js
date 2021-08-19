import HttpClient from '@/services/HttpClient.js';

const MONTHLY_EVOLUTION_ENDPOINT = '/api/statistics/monthlyEvolution';
const RANKING_ENDPOINT = '/api/statistics/ranking';
const QUANTITIES_ENDPOINT = '/api/statistics';



export default {
    getMonthlyEvolution(productCodes) {
        var params = {};
        if (productCodes && productCodes.length) params.productCodes = productCodes;
        var queryString = Object.keys(params).map(function (key) {
            return key + '=' + params[key]
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;

        return HttpClient.get(`${MONTHLY_EVOLUTION_ENDPOINT}` + queryString)
            .then(response => {
                return response.data;
            });
    },
    getRanking(productCodes) {
        var params = {};
        if (productCodes && productCodes.length) params.productCodes = productCodes;
        var queryString = Object.keys(params).map(function (key) {
            return key + '=' + params[key]
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;
        return HttpClient.get(`${RANKING_ENDPOINT}` + queryString)
            .then(response => {
                return response.data.content;
            });
    },
    getQuantities(productCodes) {
        var params = {};
        if (productCodes && productCodes.length) params.productCodes = productCodes;
        var queryString = Object.keys(params).map(function (key) {
            return key + '=' + params[key]
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;
        return HttpClient.get(`${QUANTITIES_ENDPOINT}` + queryString)
            .then(response => {
                return response.data;
            });
    }
}