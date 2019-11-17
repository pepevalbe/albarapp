import HttpClient from '@/services/HttpClient.js';
import moment from "moment";
import DeliveryNoteService from "@/services/DeliveryNoteService.js";

const RESOURCE_NAME = '/hateoas/invoices';
const INVOICE_DOWNLOAD_ENDPOINT = '/api/invoice/download';

export default {
    getAll() {
        return HttpClient.get(RESOURCE_NAME)
            .then(response => {
                return response.data._embedded.invoices;
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando las facturas");
            });
    },

    get(id) {
        return HttpClient.get(`${RESOURCE_NAME}/${id}`)
            .then(response => {
                return response.data;
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando la factura");
            });
    },
    async getWithCustomerAndTotal(id) {
        var invoice = await this.get(id);
        await this.getDeliveryNotes(invoice);
        var promises = [];
        for (const deliveryNote of invoice.deliveryNotes) {
            promises.push(this.getDeliveryNoteItemsAndCustomer(deliveryNote));
        }
        await Promise.all(promises);
        invoice.total = 0;
        invoice.dateFormatted = moment(invoice.issuedTimestamp, "x").format("DD/MM/YYYY");
        invoice.date = moment(invoice.issuedTimestamp, "x").format("YYYY-MM-DD");
        for (const deliveryNote of invoice.deliveryNotes) {
            invoice.total += deliveryNote.deliveryNoteTotal.value;
            deliveryNote.date = moment(deliveryNote.issuedTimestamp, "x").format("YYYY-MM-DD");
            deliveryNote.dateFormatted = moment(deliveryNote.issuedTimestamp, "x").format("DD/MM/YYYY");
        }
        return invoice;
    },
    getDeliveryNotes(invoice) {
        return HttpClient.get(invoice._links.deliveryNotes.href)
            .then(response => {
                invoice.deliveryNotes = response.data._embedded.deliveryNotes;
            })
            .catch(() => {
                alert("Ha ocurrido un error recuperando los albaranes de las facturas");
            });
    },

    async getDeliveryNoteItemsAndCustomer(deliveryNote) {
        Object.assign(deliveryNote, await DeliveryNoteService.getWithCustomerAndTotal(deliveryNote.id));
    },

    async getAllWithCustomerAndTotal() {
        var promises = [];
        var invoices = await this.getAll();
        for (const invoice of invoices) {
            invoice.dateFormatted = moment(invoice.issuedTimestamp, "x").format("DD/MM/YYYY");
            invoice.date = moment(invoice.issuedTimestamp, "x").format("YYYY-MM-DD");
            promises.push(this.getDeliveryNotes(invoice));
        }
        await Promise.all(promises);
        for (const invoice of invoices) {
            for (const deliveryNote of invoice.deliveryNotes) {
                promises.push(this.getDeliveryNoteItemsAndCustomer(deliveryNote));
            }
        }
        await Promise.all(promises);
        for (const invoice of invoices) {
            invoice.total = 0;
            for (const deliveryNote of invoice.deliveryNotes) {
                invoice.total += deliveryNote.deliveryNoteTotal.value;
            }
        }
        return invoices;
    },
    async create(invoice, deliveryNotes) {

        var promises = [];

        var invoiceToCreate = {
            issuedTimestamp: invoice.issuedTimestamp
        };

        // Refactorizar usando DeliveryNoteService
        var promisePost = HttpClient.post(RESOURCE_NAME, invoiceToCreate).then(response => {
            for (var i = 0; i < deliveryNotes.length; i++) {
                var item = deliveryNotes[i];
                item.invoice = response.data._links.self.href;
                promises.push(
                    HttpClient.patch(item._links.self.href, item)
                );
            }
        });

        await promisePost;
        return Promise.all(promises);
    },
    async createList(customerCodeFrom, customerCodeTo, timestampFrom, timestampTo, issuedTimestamp) {
        var promises = [];
        for (var i = customerCodeFrom; i <= customerCodeTo; i++) {
            var deliveryNotesToBill = await DeliveryNoteService.findDeliveryNotesToBill(
                i,
                timestampFrom,
                timestampTo
            );
            if (deliveryNotesToBill && deliveryNotesToBill.length) {
                var invoice = {
                    issuedTimestamp: issuedTimestamp
                };
                promises.push(this.create(invoice, deliveryNotesToBill));
            }
        }
        return Promise.all(promises);
    },
    update(id, invoice) {

        var invoiceToUpdate = {
            issuedTimestamp: invoice.issuedTimestamp
        };
        return HttpClient.patch(`${RESOURCE_NAME}/${id}`, invoiceToUpdate)
            .then(response => {
                return response.data;
            })
            .catch(() => {
                alert("Ha ocurrido un error actualizando la factura");
            });

    },
    download(id) {
        return HttpClient.get(`${INVOICE_DOWNLOAD_ENDPOINT}/?invoiceId=${id}`,
            {
                responseType: 'arraybuffer',
                headers: {
                    'Accept': 'application/pdf'
                }
            }).then(response => {
                let blob = new Blob([response.data], { type: 'application/pdf' });
                let link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = id + '.pdf';
                link.click();
            })
            .catch(() => {
                alert("Ha ocurrido un error descargando la factura");
            });
    }
}