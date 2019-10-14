import HttpClient from '@/services/HttpClient.js';
 
const RESOURCE_NAME = '/customers';

export default {
  getAll() {
    return HttpClient.get(RESOURCE_NAME)
    .then(response => {
      return response.data._embedded.products;
    })
    .catch(function() {
      alert("Ha ocurrido un error recuperando los clientes");
    });
  },
 
  get(resourceUrl) {
    return HttpClient.get(resourceUrl)
    .then(response => {
      return response.data;
    })
    .catch(function() {
      alert("Ha ocurrido un error recuperando el cliente");
    });
  },
 
  create(data) {
    return HttpClient.post(RESOURCE_NAME, data)
    .then(response => {
      return response.data;
    })
    .catch(function() {
      alert("Ha ocurrido un error creando el cliente");
    });
  },
 
  update(resourceUrl, data) {
    return HttpClient.put(resourceUrl, data)    
    .then(response => {
      return response.data;
    })
    .catch(function() {
      alert("Ha ocurrido un error actualizando el cliente");
    });
  },
 
  delete(resourceUrl) {
    return HttpClient.delete(resourceUrl)
    .catch(function() {
      alert("Ha ocurrido un error borrando el cliente");
    });
  }
};