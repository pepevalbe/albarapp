import axios from "axios";

const httpClient = axios.create({
    baseURL: process.env.VUE_APP_API_URL,
    timeout: 5000,
    params: {}
});

httpClient.interceptors.request.use(function (config) {
    var token = localStorage.token
    if (token) {
        config.headers['Authorization'] = token
    }
    return config;
});

export default httpClient;