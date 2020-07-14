import HttpClient from '@/services/HttpClient.js';

const MONTHLY_EVOLUTION_ENDPOINT = '/api/statistics/monthlyEvolution';
const RANKING_ENDPOINT = '/api/statistics/ranking';
const QUANTITIES_ENDPOINT = '/api/statistics';



export default {
    getMonthlyEvolution() {
        return HttpClient.get(`${MONTHLY_EVOLUTION_ENDPOINT}`)
            .then(response => {
                return response.data;
            });
    },
    getRanking() {
        return HttpClient.get(`${RANKING_ENDPOINT}`)
            .then(response => {
                return response.data;
            });
    },
    getQuantities() {
        return HttpClient.get(`${QUANTITIES_ENDPOINT}`)
            .then(response => {
                return response.data;
            });
    }
}   