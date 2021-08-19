import HttpClient from '@/services/HttpClient.js';

const RESOURCE_NAME = '/api/hens-batch-expense';

export default {

    getByHensBatchId(hensBatchId) {
        return HttpClient.get(`${RESOURCE_NAME}?hensBatchId=${hensBatchId}`)
            .then(response => {
                return response.data;
            });
    },

    get(id) {
        return HttpClient.get(`${RESOURCE_NAME}/${id}`)
            .then(response => {
                return response.data;
            });
    },

    create(data) {
        return HttpClient.post(RESOURCE_NAME, data)
            .then(response => {
                return response;
            });
    },

    update(id, data) {
        return HttpClient.put(`${RESOURCE_NAME}`, data)
            .then(response => {
                return response.data;
            });
    },

    delete(id) {
        return HttpClient.delete(`${RESOURCE_NAME}/${id}`);
    }

};