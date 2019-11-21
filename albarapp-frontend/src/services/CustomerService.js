import HttpClient from '@/services/HttpClient.js';

const CUSTOMER_RESOURCE = '/hateoas/customers';
const CUSTOMER_COMPLETE_RESOURCE = '/api/customers';
const CUSTOMER_PRODUCT_PRICE_RELATION = '/customerProductPrices';

export default {
  getAll() {
    return HttpClient.get(CUSTOMER_RESOURCE)
      .then(response => {
        return response.data._embedded.customers;
      })
      .catch(() => {
        alert("Ha ocurrido un error recuperando los clientes");
      });
  },
  get(id) {
    return HttpClient.get(`${CUSTOMER_RESOURCE}/${id}`)
      .then(response => {
        return response.data;
      })
      .catch(() => {
        alert("Ha ocurrido un error recuperando el cliente");
      });
  },
  getAllWithPrices() {
    return HttpClient.get(CUSTOMER_COMPLETE_RESOURCE)
      .then(response => {
        return response.data;
      })
      .catch(() => {
        alert("Ha ocurrido un error recuperando los clientes con sus precios");
      });
  },
  /*async getAllWithPrices() {
    var customers = await this.getAll();
    var promises = [];
    var service = this;
    customers.forEach(customer => {
      promises.push(service.getCustomerProductPrices(customer.id).then(response => {
        customer.customerProductPrices = response;
      }))
    })
    await Promise.all(promises);
    return customers;
  },*/
  getCustomerProductPrices(id) {
    return HttpClient.get(`${CUSTOMER_RESOURCE}/${id}${CUSTOMER_PRODUCT_PRICE_RELATION}`)
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
            offeredPrice:
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
              var index = productPrices.findIndex(function (element) {
                return element.productHref === responseProduct.config.url;
              });
              productPrices[index].product = responseProduct.data;
              productPricesOriginal[index].product =
                responseProduct.data;
            })
            .catch(() => {
              alert("Ha ocurrido un error recuperando los precios de productos");
            });
        }
        return productPrices;
      })
      .catch(() => {
        alert("Ha ocurrido un error recuperando los precios de productos");
      });
  },

  create(customer) {
    return HttpClient.post(CUSTOMER_COMPLETE_RESOURCE, customer)
      .then(response => {
        return response;
      })
      .catch(() => {
        alert("Ha ocurrido un error creando el cliente");
      });
  },

  update(id, customer) {
    return HttpClient.put(`${CUSTOMER_COMPLETE_RESOURCE}/${id}`, customer)
      .then((response) => {
        return response;
      })
      .catch(() => {
        alert("Ha ocurrido un error actualizando el cliente");
      });
  },

  createNoPrices(data) {
    return HttpClient.post(CUSTOMER_RESOURCE, data)
      .then(response => {
        return response.data;
      })
      .catch(() => {
        alert("Ha ocurrido un error creando el cliente");
      });
  },

  updateNoPrices(id, data) {
    return HttpClient.put(`${CUSTOMER_RESOURCE}/${id}`, data)
      .then(response => {
        return response.data;
      })
      .catch(() => {
        alert("Ha ocurrido un error actualizando el cliente");
      });
  }
}
