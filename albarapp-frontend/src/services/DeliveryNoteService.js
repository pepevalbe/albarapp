import HttpClient from '@/services/HttpClient.js';
import moment from "moment";

const DELIVERY_NOTES_COMPLETE_ENDPOINT = '/api/deliveryNotes';
const INVOICE_COMPLETE_ENDPOINT = '/api/invoices'

export default {
    get(id) {
        return HttpClient.get(`${DELIVERY_NOTES_COMPLETE_ENDPOINT}/${id}`)
            .then(response => {
                return response.data;
            });
    },
    delete(deliveryNote) {
        return HttpClient.delete(`${DELIVERY_NOTES_COMPLETE_ENDPOINT}`, { data: deliveryNote })
            .then(response => {
                return response.data;
            });
    },
    getAllWithCustomerAndTotal(filter, options) {
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

    create(deliveryNote, deliveryNoteItems) {

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

    update(deliveryNote) {

        var deliveryNoteDto = {
            id: deliveryNote.id,
            auxDeliveryNoteNr: deliveryNote.auxDeliveryNoteNr,
            issuedTimestamp: deliveryNote.issuedTimestamp,
            customerId: deliveryNote.customer?.id || deliveryNote.customerId,
            invoiceId: deliveryNote.invoiceId,
            deliveryNoteItems: []
        };

        for (const deliveryNoteItem of deliveryNote.deliveryNoteItems) {
            var deliveryNoteItemDto = {
                quantity: deliveryNoteItem.quantity,
                price: deliveryNoteItem.price,
                productId: deliveryNoteItem.product?.id || deliveryNoteItem.productId
            };
            deliveryNoteDto.deliveryNoteItems.push(deliveryNoteItemDto);
        }

        return HttpClient.post(DELIVERY_NOTES_COMPLETE_ENDPOINT, deliveryNoteDto);
    },

    getByInvoiceId(invoiceId) {
        return HttpClient.get(`${INVOICE_COMPLETE_ENDPOINT}/${invoiceId}/delivery-notes`)
            .then(response => {
                return response.data;
            });
    },

    findDeliveryNotesToBill(customerId) {
        return HttpClient.get(`${DELIVERY_NOTES_COMPLETE_ENDPOINT}/to-bill?customerId=${customerId}`)
            .then(response => {
                return response.data;
            });
    }
}
