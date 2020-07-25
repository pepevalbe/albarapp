import axios from 'axios';
import { store } from '../store/store';

const httpClient = axios.create({
    baseURL: process.env.VUE_APP_API_URL,
    timeout: 10000,
    params: {}
});

httpClient.interceptors.request.use(function (config) {
    var token = store.getters.token;
    if (token) {
        config.headers['Authorization'] = token
    }
    return config;
});

httpClient.interceptors.response.use(function (response) {
    return response
}, function (error) {
    if (error?.response?.status === 401 ||
        error?.response?.status === 403) {
        localStorage.clear();
        if (!window.location.hash.includes("#/login")) window.location.href = "/#/login?destinationURL=" + window.location.hash.split("#")[1];
    } else if (
        error?.response?.data?.errorCode &&
        error?.response?.data?.errorMessage &&
        error.response.data.errorCode !== '002') {
        return Promise.reject(error);
    }
    return Promise.reject(error);
});
export default httpClient;