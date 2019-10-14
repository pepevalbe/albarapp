import HttpClient from '@/services/HttpClient.js';
 
const RESOURCE_NAME = '/customers';

export default {
  getAll() {
    return HttpClient.get(RESOURCE_NAME)
    .then(response => {
      return response.data._embedded.customers;
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
 
  getCustomerProductPrices(resourceUrl) {
    return HttpClient.get(resourceUrl + "/customerProductPrices")
    .then(response => {
      var productPrices = [];
      var productPricesOriginal = [];
      for (
        var i = 0;
        i < response.data._embedded.customerProductPrices.length;
        i++
      ) {
        var productPrice = {
          product: {},
          price:
            response.data._embedded.customerProductPrices[i].offeredPrice,
          productPriceHref:
            response.data._embedded.customerProductPrices[i]._links.self
              .href,
          productHref:
            response.data._embedded.customerProductPrices[i]._links
              .product.href
        };
        productPrices.push(productPrice);
        productPricesOriginal.push(productPrice);
        HttpClient.get(
            response.data._embedded.customerProductPrices[i]._links
              .product.href
          )
          .then(responseProduct => {
            var index = productPrices.findIndex(function(element) {
              return element.productHref === responseProduct.config.url;
            });
            productPrices[index].product = responseProduct.data;
            productPricesOriginal[index].product =
              responseProduct.data;
          })
          .catch(function() {
            alert("Ha ocurrido un error recuperando los precios de productos");
          });          
      }
      return productPrices;
    })
    .catch(function() {
      alert("Ha ocurrido un error recuperando los precios de productos");
    });
  }
};