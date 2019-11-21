import HttpClient from '@/services/HttpClient.js';
import moment from "moment";

const CUSTOMER_RESOURCE = 'hateoas/customers';
const DELIVERY_NOTE_RESOURCE = '/hateoas/deliveryNotes';
const DELIVERY_NOTE_ITEM_RESOURCE = '/hateoas/deliveryNoteItems';

export default {
    getAll(filter, options) {
        var params = {};
        if (filter && filter.form) {
            if (filter.form.customer && filter.form.customer.code)
                params.customerCode = filter.form.customer.code;
            if (filter.form.dateFrom) params.timestampFrom = moment(filter.form.dateFrom, "YYYY-MM-DD").format('x');
            if (filter.form.dateTo) params.timestampTo = moment(filter.form.dateTo, "YYYY-MM-DD").format('x');
        }
        if (options) {
            if (options.page) params.page = options.page - 1;
            if (options.itemsPerPage) params.size = options.itemsPerPage;
        }

        var queryString = Object.keys(params).map(function (key) {
            return key + '=' + params[key]
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;

        return HttpClient.get(DELIVERY_NOTE_RESOURCE + '/search/findDeliveryNotes' + queryString)
            .then(response => {
                return {
                    deliveryNotes: response.data._embedded.deliveryNotes,
                    page: response.data.page
                };
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando los albaranes");
            });
    },
    get(id) {
        return HttpClient.get(`${DELIVERY_NOTE_RESOURCE}/${id}`)
            .then(response => {
                return response.data;
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando el albarán");
            });
    },

    getCustomer(deliveryNote) {
        return HttpClient.get(deliveryNote._links.customer.href)
            .then(response => {
                deliveryNote.customer = response.data;
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando el cliente del albarán");
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
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando las líneas de albarán");
            });
    },
    getProducts(deliveryNoteItem) {
        return HttpClient.get(deliveryNoteItem._links.product.href)
            .then(response => {
                deliveryNoteItem.product = response.data;
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando los productos de las líneas de albarán");
            });
    },
    async getAllWithCustomerAndTotal(filter, options) {
        var promises = [];
        var response = await this.getAll(filter, options);
        var deliveryNotes = response.deliveryNotes;
        for (const deliveryNote of deliveryNotes) {
            deliveryNote.dateFormatted = moment(deliveryNote.issuedTimestamp, "x").format("DD/MM/YYYY");
            deliveryNote.date = moment(deliveryNote.issuedTimestamp, "x").format("YYYY-MM-DD");
            promises.push(this.getCustomer(deliveryNote));
            promises.push(this.getInvoice(deliveryNote));
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
        return response;
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
                deliveryNote.date = moment(deliveryNote.issuedTimestamp, "x").format("YYYY-MM-DD");
                deliveryNote.valid = false;
                deliveryNote.deliveryNoteTotal = { value: 0 };
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando el albarán");
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

        var promises = [];

        var customerHref = `${CUSTOMER_RESOURCE}/${deliveryNote.customer.id}`;

        var deliveryNoteToCreate = {
            auxDeliveryNoteNr: deliveryNote.dateauxDeliveryNoteNr,
            issuedTimestamp: deliveryNote.issuedTimestamp,
            customer: customerHref
        };

        var promisePost = HttpClient.post(DELIVERY_NOTE_RESOURCE, deliveryNoteToCreate).then(response => {
            for (var i = 0; i < deliveryNoteItems.length; i++) {
                var item = deliveryNoteItems[i];
                var deliveryNoteItem = {
                    quantity: item.quantity,
                    price: item.price,
                    product: item.product._links.self.href,
                    deliveryNote: response.data._links.self.href
                };
                promises.push(
                    HttpClient.post(DELIVERY_NOTE_ITEM_RESOURCE, deliveryNoteItem)
                );
            }
        });

        await promisePost;
        return Promise.all(promises);
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
                    promises.push(HttpClient.delete(element._links.self.href)
                        .catch(() => {
                            alert("Ha ocurrido un error actualizando las líneas de albarán");
                        }));
                });
                itemsToInsert.forEach(element => {
                    var deliveryNoteItem = {
                        quantity: element.quantity,
                        price: element.price,
                        product: element.product._links.self.href,
                        deliveryNote: deliveryNote._links.self.href
                    };
                    promises.push(HttpClient.post(DELIVERY_NOTE_ITEM_RESOURCE, deliveryNoteItem)
                        .catch(() => {
                            alert("Ha ocurrido un error actualizando líneas de albarán");
                        }));
                });
            })
            .catch(() => {
                alert("Ha ocurrido un error actualizando el albarán");
            })
        await promisePut;
        return Promise.all(promises);
    },
    findDeliveryNotesToBill(customerCode, timestampFrom, timestampTo) {

        var params = {};
        if (customerCode) {
            params.customerCode = customerCode;
        }
        if (timestampFrom) {
            params.timestampFrom = timestampFrom;
        }
        if (timestampTo) {
            params.timestampTo = timestampTo;
        }
        params.page = 0;
        params.size = 1000;

        var queryString = Object.keys(params).map(function (key) {
            return key + '=' + params[key]
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;

        return HttpClient.get(DELIVERY_NOTE_RESOURCE + '/search/findDeliveryNotesToBill' + queryString)
            .then(response => {
                return response.data._embedded.deliveryNotes;
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando el albarán");
            });
    },
    async findDeliveryNotesToBillWithCustomerAndTotal(customerCode, timestampFrom, timestampTo) {

        var deliveryNotes = await this.findDeliveryNotesToBill(customerCode, timestampFrom, timestampTo);
        var promises = [];

        for (const deliveryNote of deliveryNotes) {
            deliveryNote.dateFormatted = moment(deliveryNote.issuedTimestamp, "x").format("DD/MM/YYYY");
            deliveryNote.date = moment(deliveryNote.issuedTimestamp, "x").format("YYYY-MM-DD");
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
