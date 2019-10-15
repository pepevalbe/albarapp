import HttpClient from '@/services/HttpClient.js';

const RESOURCE_NAME = '/customerProductPrices';

export default {
    getAll() {
        return HttpClient.get(RESOURCE_NAME)
            .then(response => {
                return response.data._embedded.customers;
            })
            .catch(function () {
                alert("Ha ocurrido un error recuperando los precios de productos");
            });
    },

    get(resourceUrl) {
        return HttpClient.get(resourceUrl)
            .then(response => {
                return response.data;
            })
            .catch(function () {
                alert("Ha ocurrido un error recuperando los precios de productos");
            });
    },

    create(data) {
        return HttpClient.post(RESOURCE_NAME, data)
            .then(response => {
                return response.data;
            })
            .catch(function () {
                alert("Ha ocurrido un error creando los precios de productos");
            });
    },

    update(resourceUrl, data) {
        return HttpClient.put(resourceUrl, data)
            .then(response => {
                return response.data;
            })
            .catch(function () {
                alert("Ha ocurrido un error actualizando los precios de productos");
            });
    },

    delete(resourceUrl) {
        return HttpClient.delete(resourceUrl)
            .then(response => {
                return response.data;
            })
            .catch(function () {
                alert("Ha ocurrido un error creando el cliente");
            });
    }
};