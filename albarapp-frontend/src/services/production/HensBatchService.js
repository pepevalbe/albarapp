import HttpClient from '@/services/HttpClient.js';

const RESOURCE_NAME = '/hateoas/hensBatches';
const RESOURCE_NAME_ACTIVES = '/hateoas/hensBatches/search/findByEndTimestampIsNull';

export default {
  getAll() {
    return HttpClient.get(RESOURCE_NAME)
      .then(response => {
        return response.data._embedded.hensBatches;
      });
  },

  getActives() {
    return HttpClient.get(RESOURCE_NAME_ACTIVES)
      .then(response => {
        return response.data._embedded.hensBatches;
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
  },

  delete(id) {
    return HttpClient.delete(`${RESOURCE_NAME}/${id}`);
  }
};