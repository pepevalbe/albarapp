import HttpClient from '@/services/HttpClient.js';

const CUSTOMER_COMPLETE_RESOURCE = '/api/customers';

export default {
  getAll() {
    return HttpClient.get(CUSTOMER_COMPLETE_RESOURCE)
      .then(response => {
        return response.data;
      });
  },
  get(id) {
    return HttpClient.get(`${CUSTOMER_COMPLETE_RESOURCE}/${id}`)
      .then(response => {
        return response.data;
      });
  },
  getAllWithPrices() {
    return HttpClient.get(CUSTOMER_COMPLETE_RESOURCE)
      .then(response => {
        return response.data;
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
      customerProductPrice.priceIndex = customerDto.customerProductPrices.indexOf(customerProductPrice);
      customerProductPrice.productId = customerProductPrice.product.id;
    }
    return customerDto;
  }
}
