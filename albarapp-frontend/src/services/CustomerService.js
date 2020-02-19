import HttpClient from '@/services/HttpClient.js';

const CUSTOMER_RESOURCE = '/hateoas/customers';
const CUSTOMER_COMPLETE_RESOURCE = '/api/customers';
const CUSTOMER_PRODUCT_PRICE_RELATION = '/customerProductPrices';

export default {
  getAll() {
    return HttpClient.get(CUSTOMER_RESOURCE)
      .then(response => {
        return response.data._embedded.customers;
      });
  },
  get(id) {
    return HttpClient.get(`${CUSTOMER_RESOURCE}/${id}`)
      .then(response => {
        return response.data;
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
  fetchProduct(item) {
    return HttpClient.get(item._links.product.href)
      .then(response => {
        item.product = response.data;
      });
  },
  async getCustomerProductPrices(id) {
    var promises = [];
    var response = await HttpClient.get(`${CUSTOMER_RESOURCE}/${id}${CUSTOMER_PRODUCT_PRICE_RELATION}`);
    var customerProductPrices = response.data._embedded.customerProductPrices;

    for (const customerProductPrice of customerProductPrices) {
      promises.push(this.fetchProduct(customerProductPrice));
    }
    return Promise.all(promises).then(function () {
      return customerProductPrices;
    });
  },
  create(customer) {
    var customerDto = this.mapCustomerToDto(customer);
    return HttpClient.post(CUSTOMER_COMPLETE_RESOURCE, customerDto)
      .then(response => {
        return response;
      });
  },
  update(id, customer) {
    var customerDto = this.mapCustomerToDto(customer);
    return HttpClient.put(`${CUSTOMER_COMPLETE_RESOURCE}/${id}`, customerDto)
      .then((response) => {
        return response;
      });
  },
  mapCustomerToDto(customer) {
    var customerDto = Object.assign({}, customer);
    for (const customerProductPrice of customerDto.customerProductPrices) {
      customerProductPrice.productId = customerProductPrice.product.id;
    }
    return customerDto;
  }
}
