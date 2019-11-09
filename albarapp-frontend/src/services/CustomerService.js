import HttpClient from '@/services/HttpClient.js';

const CUSTOMER_RESOURCE = '/hateoas/customers';
const CUSTOMER_PRODUCT_PRICE_RELATION = '/customerProductPrices';
const CUSTOMER_PRODUCT_PRICE_RESOURCE = '/hateoas/customerProductPrices';

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
  async getAllWithPrices() {
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
  },
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

  create(customer, productPrices) {
    return HttpClient.post(CUSTOMER_RESOURCE, customer)
      .then(response => {
        productPrices.forEach(function (item) {
          var customerProductPrice = {
            offeredPrice: item.price,
            customer: response.data._links.self.href,
            product: item.product._links.self.href
          };
          HttpClient.post(CUSTOMER_PRODUCT_PRICE_RESOURCE, customerProductPrice)
            .catch(() => {
              alert("Ha ocurrido un error creando los precios");
            })
        })
      })
      .catch(() => {
        alert("Ha ocurrido un error creando el cliente");
      });
  },

  async update(id, customer, productPrices, productPricesOriginal) {
    var promises = [];
    var promisePut = HttpClient.put(`${CUSTOMER_RESOURCE}/${id}`, customer)
      .then(() => {
        var productPricesToDelete = productPricesOriginal.filter(
          f => !productPrices.includes(f)
        );
        var productPricesToInsert = productPrices.filter(
          f => !productPricesOriginal.includes(f)
        );
        productPricesToDelete.forEach(element => {
          promises.push(HttpClient.delete(element.productPriceHref)
            .catch(() => {
              alert("Ha ocurrido un error actualizando los precios");
            }));
        });
        productPricesToInsert.forEach(element => {
          var customerProductPrice = {
            offeredPrice: element.price,
            customer: customer._links.self.href,
            product: element.product._links.self.href
          };
          promises.push(HttpClient.post(CUSTOMER_PRODUCT_PRICE_RESOURCE, customerProductPrice)
            .catch(() => {
              alert("Ha ocurrido un error actualizando los precios");
            }));
        });
      })
      .catch(() => {
        alert("Ha ocurrido un error actualizando el cliente");
      })
    await promisePut;
    return Promise.all(promises);
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
