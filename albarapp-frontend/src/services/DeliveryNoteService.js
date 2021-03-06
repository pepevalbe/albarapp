import HttpClient from '@/services/HttpClient.js';
import moment from "moment";

const CUSTOMER_RESOURCE = 'hateoas/customers';
const DELIVERY_NOTE_RESOURCE = '/hateoas/deliveryNotes';
const DELIVERY_NOTES_COMPLETE_ENDPOINT = '/api/deliveryNotes';
const DELIVERY_NOTE_ITEM_RESOURCE = '/hateoas/deliveryNoteItems';

export default {
    get(id) {
        return HttpClient.get(`${DELIVERY_NOTE_RESOURCE}/${id}`)
            .then(response => {
                return response.data;
            });
    },
    getCustomer(deliveryNote) {
        return HttpClient.get(deliveryNote._links.customer.href)
            .then(response => {
                deliveryNote.customer = response.data;
            });
    },
    getInvoice(deliveryNote) {
        return HttpClient.get(deliveryNote._links.invoice.href)
            .then(response => {
                deliveryNote.invoice = response.data;
            })
            .catch((error) => {
                if (error.response.data.errorCode === '002') {
                    return Promise.resolve();
                } else {
                    return Promise.reject(error);
                }
            });
    },
    getDeliveryNoteItems(deliveryNote) {
        return HttpClient.get(deliveryNote._links.deliveryNoteItems.href)
            .then(response => {
                deliveryNote.deliveryNoteItems = response.data._embedded.deliveryNoteItems;
            });
    },
    getProducts(deliveryNoteItem) {
        return HttpClient.get(deliveryNoteItem._links.product.href)
            .then(response => {
                deliveryNoteItem.product = response.data;
            });
    },
    delete(deliveryNote) {
        return HttpClient.delete(`${DELIVERY_NOTES_COMPLETE_ENDPOINT}`, { data: deliveryNote })
            .then(response => {
                return response.data;
            });
    },
    async getAllWithCustomerAndTotal(filter, options) {
        var params = {};
        if (filter?.form?.customerCode) params.customerCode = filter.form.customerCode;
        if (filter?.form?.dateFrom) params.timestampFrom = moment.utc(filter.form.dateFrom, "YYYY-MM-DD").format('x');
        if (filter?.form?.dateTo) params.timestampTo = moment.utc(filter.form.dateTo, "YYYY-MM-DD").format('x');
        if (filter?.auxDeliveryNoteNr) params.auxDeliveryNoteNr = filter.auxDeliveryNoteNr;
        if (filter?.products?.productCodes?.length) params.productCodes = filter.products.productCodes;
        if (options?.page) params.page = options.page - 1;
        if (options?.itemsPerPage) params.size = options.itemsPerPage;
        if (options?.sortBy?.length) {
            params.sort = [];
            for (let [index, sort] of options.sortBy.entries()) {
                var direction = options.sortDesc[index] ? 'desc' : 'asc';
                params.sort.push(sort + ',' + direction);
            }
            params.sort.push('id,asc');
        }

        var queryString = Object.keys(params).map(function (key) {
            if (Array.isArray(params[key])) {
                let finalParam = [];
                for (let param of params[key]) {
                    finalParam.push(key + '=' + param);
                }
                return finalParam.join('&');
            } else {
                return key + '=' + params[key]
            }
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;

        return HttpClient.get(DELIVERY_NOTES_COMPLETE_ENDPOINT + queryString)
            .then(response => {
                return {
                    deliveryNotes: response.data.content,
                    totalElements: response.data.totalElements
                };
            });
    },
    async getWithCustomerAndTotal(id) {
        var promises = [];
        var deliveryNote = {};
        await HttpClient.get(`${DELIVERY_NOTE_RESOURCE}/${id}`)
            .then(response => {
                deliveryNote = response.data;
                promises.push(this.getCustomer(deliveryNote));
                deliveryNote.deliveryNoteItems = [];
                promises.push(this.getDeliveryNoteItems(deliveryNote));
                deliveryNote.date = moment.utc(deliveryNote.issuedTimestamp, "x").format("YYYY-MM-DD");
                deliveryNote.valid = false;
                deliveryNote.deliveryNoteTotal = { value: 0 };
            });
        await Promise.all(promises);
        for (const deliveryNoteItem of deliveryNote.deliveryNoteItems) {
            promises.push(this.getProducts(deliveryNoteItem));
        }
        await Promise.all(promises);
        deliveryNote.total = 0
        for (const deliveryNoteItem of deliveryNote.deliveryNoteItems) {
            deliveryNoteItem.gross = deliveryNoteItem.quantity * deliveryNoteItem.price;
            deliveryNoteItem.taxRate = deliveryNoteItem.product.tax;
            deliveryNoteItem.net = deliveryNoteItem.quantity * deliveryNoteItem.price * (1 + deliveryNoteItem.product.tax / 100)
            deliveryNote.deliveryNoteTotal.value += deliveryNoteItem.net;
        }
        return deliveryNote;
    },

    async create(deliveryNote, deliveryNoteItems) {

        var deliveryNoteDto = {
            auxDeliveryNoteNr: deliveryNote.auxDeliveryNoteNr,
            issuedTimestamp: deliveryNote.issuedTimestamp,
            customerId: deliveryNote.customer.id,
            deliveryNoteItems: []
        };

        for (const deliveryNoteItem of deliveryNoteItems) {
            var deliveryNoteItemDto = {
                quantity: deliveryNoteItem.quantity,
                price: deliveryNoteItem.price,
                productId: deliveryNoteItem.product.id
            };
            deliveryNoteDto.deliveryNoteItems.push(deliveryNoteItemDto);
        }

        return HttpClient.post(DELIVERY_NOTES_COMPLETE_ENDPOINT, deliveryNoteDto);
    },

    async update(id, deliveryNote, deliveryNoteItems, deliveryNoteItemsOriginal) {
        var promises = [];
        var customerHref = `${CUSTOMER_RESOURCE}/${deliveryNote.customer.id}`;

        var deliveryNoteToUpdate = {
            auxDeliveryNoteNr: deliveryNote.auxDeliveryNoteNr,
            issuedTimestamp: deliveryNote.issuedTimestamp,
            customer: customerHref
        };

        var promisePut = HttpClient.patch(`${DELIVERY_NOTE_RESOURCE}/${id}`, deliveryNoteToUpdate)
            .then(() => {
                var itemsToDelete = deliveryNoteItemsOriginal.filter(
                    f => !deliveryNoteItems.includes(f)
                );
                var itemsToInsert = deliveryNoteItems.filter(
                    f => !deliveryNoteItemsOriginal.includes(f)
                );
                itemsToDelete.forEach(element => {
                    promises.push(HttpClient.delete(element._links.self.href));
                });
                itemsToInsert.forEach(element => {
                    var deliveryNoteItem = {
                        quantity: element.quantity,
                        price: element.price,
                        product: element.product._links.self.href,
                        deliveryNote: deliveryNote._links.self.href
                    };
                    promises.push(HttpClient.post(DELIVERY_NOTE_ITEM_RESOURCE, deliveryNoteItem));
                });
            });
        await promisePut;
        return Promise.all(promises);
    },

    findDeliveryNotesToBill(customerCode) {
        var params = {};
        if (customerCode) {
            params.customerCode = customerCode;
        }
        params.page = 0;
        params.size = 1000;

        var queryString = Object.keys(params).map(function (key) {
            return key + '=' + params[key]
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;

        return HttpClient.get(DELIVERY_NOTE_RESOURCE + '/search/findByCustomerCodeAndInvoiceIsNull' + queryString)
            .then(response => {
                return response.data._embedded.deliveryNotes;
            });
    },
    async findDeliveryNotesToBillWithCustomerAndTotal(customerCode) {

        var deliveryNotes = await this.findDeliveryNotesToBill(customerCode);
        var promises = [];

        for (const deliveryNote of deliveryNotes) {
            deliveryNote.dateFormatted = moment.utc(deliveryNote.issuedTimestamp, "x").format("DD/MM/YYYY");
            deliveryNote.date = moment.utc(deliveryNote.issuedTimestamp, "x").format("YYYY-MM-DD");
            promises.push(this.getCustomer(deliveryNote));
            promises.push(this.getDeliveryNoteItems(deliveryNote));
        }
        await Promise.all(promises);
        for (const deliveryNote of deliveryNotes) {
            for (const deliveryNoteItem of deliveryNote.deliveryNoteItems) {
                promises.push(this.getProducts(deliveryNoteItem));
            }
        }
        await Promise.all(promises);
        for (const deliveryNote of deliveryNotes) {
            deliveryNote.total = 0;
            for (const deliveryNoteItem of deliveryNote.deliveryNoteItems) {
                deliveryNote.total += deliveryNoteItem.quantity * deliveryNoteItem.price * (1 + deliveryNoteItem.product.tax / 100);
            }
        }

        return deliveryNotes;
    },
    disassociateInvoice(id) {
        var deliveryNote = { invoice: "" };
        return HttpClient.patch(`${DELIVERY_NOTE_RESOURCE}/${id}`, deliveryNote);
    },
    associateInvoice(id, invoice) {
        var deliveryNote = { invoice: invoice._links.self.href };
        return HttpClient.patch(`${DELIVERY_NOTE_RESOURCE}/${id}`, deliveryNote);
    }
}
