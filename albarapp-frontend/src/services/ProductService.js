import Repository from '@/services/Repository.js';
 
const RESOURCE_NAME = '/products';

export default {
  getAll() {
    return Repository.get(RESOURCE_NAME)
    .then(response => {
      return response.data._embedded.products;
    })
    .catch(function() {
      alert("Ha ocurrido un error recuperando los productos");
    });
  },
 
  get(id) {
    return Repository.get(`${RESOURCE_NAME}/${id}`);
  },
 
  create(data) {
    return Repository.post(RESOURCE_NAME, data);
  },
 
  update(id, data) {
    return Repository.put(`${RESOURCE_NAME}/${id}`, data);
  },
 
  delete(id) {
    return Repository.delete(`${RESOURCE_NAME}/${id}`);
  }
};