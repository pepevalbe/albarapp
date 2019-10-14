import HttpClient from '@/services/HttpClient.js';
 
const RESOURCE_NAME = '/customerProductPrices';

export default {
  getAll() {
    return HttpClient.get(RESOURCE_NAME)
    .then(response => {
      return response.data._embedded.products;
    })
    .catch(function() {
      alert("Ha ocurrido un error recuperando los precios de clientes");
    });
  },
 
  get(resourceUrl) {
    return HttpClient.get(resourceUrl)
    .then(response => {
      return response.data;
    })
    .catch(function() {
      alert("Ha ocurrido un error recuperando el precio de cliente");
    });
  },
 
  create(data) {
    return HttpClient.post(RESOURCE_NAME, data)
    .then(response => {
      return response.data;
    })
    .catch(function() {
      alert("Ha ocurrido un error creando el precio de cliente");
    });
  },
 
  update(resourceUrl, data) {
    return HttpClient.put(resourceUrl, data)    
    .then(response => {
      return response.data;
    })
    .catch(function() {
      alert("Ha ocurrido un error actualizando el precio de cliente");
    });
  },
 
  delete(resourceUrl) {
    return HttpClient.delete(resourceUrl)
    .catch(function() {
      alert("Ha ocurrido un error borrando el precio de cliente");
    });
  }
};