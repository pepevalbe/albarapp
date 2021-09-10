import HttpClient from '@/services/HttpClient.js';

const RESOURCE_NAME = '/api/products';

export default {
  getAll() {
    return HttpClient.get(RESOURCE_NAME)
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
    return HttpClient.put(`${RESOURCE_NAME}/${id}`, data)
      .then(response => {
        return response.data;
      });
  }
};