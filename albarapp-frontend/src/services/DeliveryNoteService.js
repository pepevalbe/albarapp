import HttpClient from '@/services/HttpClient.js';
import DateFormatService from "@/services/DateFormatService.js";

const RESOURCE_NAME = '/deliveryNotes';

export default {
    getAll() {
        return HttpClient.get(RESOURCE_NAME)
            .then(response => {
                return response.data._embedded.deliveryNotes;
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando los albaranes");
            });
    },

    get(id) {
        return HttpClient.get(`${RESOURCE_NAME}/${id}`)
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
            deliveryNote.issuedDate = DateFormatService.formatFromTimestamp(deliveryNote.issuedTimestamp);
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
                deliveryNote.total += deliveryNoteItem.quantity * deliveryNoteItem.price * (1 + deliveryNoteItem.product.tax/100);
            }
        }
        return deliveryNotes;
    }
}
