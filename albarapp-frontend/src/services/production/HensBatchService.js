import HttpClient from '@/services/HttpClient.js';

const RESOURCE_NAME = '/api/hens-batch';
const RESOURCE_NAME_ACTIVES = '/api/active-hens-batch';

export default {
  getAll() {
    return HttpClient.get(RESOURCE_NAME)
      .then(response => {
        return response.data;
      });
  },

  getActives() {
    return HttpClient.get(RESOURCE_NAME_ACTIVES)
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
        return response.data;
      });
  },

  update(id, data) {
    return HttpClient.post(`${RESOURCE_NAME}`, data)
      .then(response => {
        return response.data;
      });
  }
};