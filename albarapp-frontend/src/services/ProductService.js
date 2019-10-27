import HttpClient from '@/services/HttpClient.js';

const RESOURCE_NAME = '/hateoas/products';

export default {
  getAll() {
    return HttpClient.get(RESOURCE_NAME)
      .then(response => {
        return response.data._embedded.products;
      })
      .catch(() => {
        alert("Ha ocurrido un error recuperando los productos");
      });
  },

  get(id) {
    return HttpClient.get(`${RESOURCE_NAME}/${id}`)
      .then(response => {
        return response.data;
      })
      .catch(() => {
        alert("Ha ocurrido un error recuperando el producto");
      });
  },

  create(data) {
    return HttpClient.post(RESOURCE_NAME, data)
      .then(response => {
        return response.data;
      })
      .catch(() => {
        alert("Ha ocurrido un error creando el producto");
      });
  },

  update(id, data) {
    return HttpClient.put(`${RESOURCE_NAME}/${id}`, data)
      .then(response => {
        return response.data;
      })
      .catch(() => {
        alert("Ha ocurrido un error actualizando el producto");
      });
  },

  delete(id) {
    return HttpClient.delete(`${RESOURCE_NAME}/${id}`)
      .catch(() => {
        alert("Ha ocurrido un error borrando el producto");
      });
  }
};