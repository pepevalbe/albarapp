import HttpClient from '@/services/HttpClient.js';

const RESOURCE_NAME = '/hateoas/hensBatchReports';
const RESOURCE_NAME_BY_BATCH = '/hateoas/hensBatchReports/search/findByHensBatchId';
const CREATE_ASSOCIATED_TO_BATCH = '/hateoas/hensBatchs/{hensBatchId}/hensBatchReport';

export default {
  getAll() {
    return HttpClient.get(RESOURCE_NAME)
      .then(response => {
        return response.data._embedded.hensBatchReports;
      });
  },

  getByHensBatchId(hensBatchId) {
    return HttpClient.get(`${RESOURCE_NAME_BY_BATCH}?hensBatchId=${hensBatchId}`)
      .then(response => {
        return response.data._embedded.hensBatchReports;
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

  createAssociatedToBatch(data, hensBatchId) {
    return HttpClient.put(CREATE_ASSOCIATED_TO_BATCH.replace("{hensBatchId}", hensBatchId), data)
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