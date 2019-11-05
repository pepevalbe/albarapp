import axios from 'axios';

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

httpClient.interceptors.response.use(function (response) {
    return response
}, function (error) {
    if (error.response.status === 401 ||
        error.response.status === 403) {
        localStorage.clear();
        window.location.href = "/";
    } else if (
        error.response.data != null &&
        error.response.data.errorCode != null &&
        error.response.data.errorMessage != null) {
        alert(error.response.data.errorCode + ' : ' + error.response.data.errorMessage)
    }
    return Promise.reject(error)
});
export default httpClient;