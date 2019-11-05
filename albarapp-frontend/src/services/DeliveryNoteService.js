import HttpClient from '@/services/HttpClient.js';
import moment from "moment";

const DELIVERY_NOTE_RESOURCE = '/hateoas/deliveryNotes';
const DELIVERY_NOTE_ITEM_RESOURCE = '/hateoas/deliveryNoteItems';

export default {
    getAll() {
        return HttpClient.get(DELIVERY_NOTE_RESOURCE)
            .then(response => {
                return response.data._embedded.deliveryNotes;
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
    async getAllWithCustomerAndTotal() {
        var promises = [];
        var deliveryNotes = await this.getAll();
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

        var deliveryNoteToCreate = {
            auxDeliveryNoteNr: deliveryNote.dateauxDeliveryNoteNr,
            issuedTimestamp: deliveryNote.issuedTimestamp,
            customer: deliveryNote.customer._links.self.href
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

        var deliveryNoteToUpdate = {
            auxDeliveryNoteNr: deliveryNote.auxDeliveryNoteNr,
            issuedTimestamp: deliveryNote.issuedTimestamp,
            customer: deliveryNote.customer._links.self.href
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
        return HttpClient.get(`${DELIVERY_NOTE_RESOURCE}/search/findDeliveryNotesToBill?customerCode=` +
            + customerCode + `&timestampFrom=` + timestampFrom + `&timestampTo=` + timestampTo)
            .then(response => {
                return response.data._embedded.deliveryNotes;
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando el albarán");
            });
    }
}
