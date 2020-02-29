import HttpClient from '@/services/HttpClient.js';
import moment from "moment";
import DeliveryNoteService from "@/services/DeliveryNoteService.js";

const RESOURCE_NAME = '/hateoas/invoices';
const INVOICE_COMPLETE_ENDPOINT = '/api/invoices';
const INVOICE_BILL_ENDPOINT = '/api/invoices/bill';
const INVOICE_DOWNLOAD_EDI_ENDPOINT = '/api/invoices/download/edi';
const INVOICE_DOWNLOAD_PDF_ENDPOINT = '/api/invoices/download/pdf';
const INVOICE_DOWNLOAD_PDF_MULTIPLE_ENDPOINT = '/api/invoices/download/pdf/multiple';

export default {
    getAll(options) {

        var params = {};

        if (options) {
            if (options.page) params.page = options.page - 1;
            if (options.itemsPerPage) params.size = options.itemsPerPage;
        }

        var queryString = Object.keys(params).map(function (key) {
            return key + '=' + params[key]
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;

        return HttpClient.get(RESOURCE_NAME + queryString)
            .then(response => {
                return {
                    invoices: response.data._embedded.invoices,
                    page: response.data.page
                }
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
        invoice.dateFormatted = moment.utc(invoice.issuedTimestamp, "x").format("DD/MM/YYYY");
        invoice.date = moment.utc(invoice.issuedTimestamp, "x").format("YYYY-MM-DD");
        for (const deliveryNote of invoice.deliveryNotes) {
            invoice.total += deliveryNote.deliveryNoteTotal.value;
            deliveryNote.date = moment.utc(deliveryNote.issuedTimestamp, "x").format("YYYY-MM-DD");
            deliveryNote.dateFormatted = moment.utc(deliveryNote.issuedTimestamp, "x").format("DD/MM/YYYY");
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

    getAllWithCustomerAndTotal(filter, options, timeout) {
        var params = {};
        if (filter && filter.form) {
            if (filter.form.customerCode) params.customerCode = filter.form.customerCode;
            if (filter.form.dateFrom) params.timestampFrom = moment.utc(filter.form.dateFrom, "YYYY-MM-DD").format('x');
            if (filter.form.dateTo) params.timestampTo = moment.utc(filter.form.dateTo, "YYYY-MM-DD").format('x');
            if (filter.products.productCodes && filter.products.productCodes.length) params.productCodes = filter.products.productCodes;
        }
        if (options) {
            if (options.page) params.page = options.page - 1;
            if (options.itemsPerPage) params.size = options.itemsPerPage;
            if (options.sortBy && options.sortBy.length) {
                var direction = options.sortDesc[0] ? 'desc' : 'asc';
                params.sort = options.sortBy + ',' + direction;
            }
        }

        var queryString = Object.keys(params).map(function (key) {
            return key + '=' + params[key]
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;

        var HttpClientOptions = timeout ? {
            timeout: timeout
        } : null;

        return HttpClient.get(INVOICE_COMPLETE_ENDPOINT + queryString, HttpClientOptions)
            .then(response => {
                return {
                    invoices: response.data.content,
                    totalElements: response.data.totalElements
                };
            });
    },

    async createList(customerCodeFrom, customerCodeTo, timestampFrom, timestampTo, issuedTimestamp) {

        var params = {
            customerCodeFrom: customerCodeFrom,
            customerCodeTo: customerCodeTo,
            timestampFrom: timestampFrom,
            timestampTo: timestampTo,
            issuedTimestamp
        };

        var queryString = Object.keys(params).map(function (key) {
            return key + '=' + params[key]
        }).join('&');

        if (queryString != "") queryString = '?' + queryString;

        return HttpClient.post(INVOICE_BILL_ENDPOINT + queryString)
            .then(response => {
                return response.data;
            });
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
    downloadEdi(id) {
        return HttpClient.get(`${INVOICE_DOWNLOAD_EDI_ENDPOINT}/?invoiceId=${id}`,
            {
                responseType: 'arraybuffer',
                headers: {
                    'Accept': 'text/xml'
                }
            }).then(response => {
                let blob = new Blob([response.data], { type: 'text/xml' });
                let link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = id + '.xml';
                link.click();
            })
            .catch(() => {
                alert("Ha ocurrido un error descargando la factura");
            });
    },
    downloadPdf(id) {
        return HttpClient.get(`${INVOICE_DOWNLOAD_PDF_ENDPOINT}/?invoiceId=${id}`,
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
            });
    },
    downloadPdfMultiple(ids) {
        return HttpClient.get(`${INVOICE_DOWNLOAD_PDF_MULTIPLE_ENDPOINT}/?invoiceId=${ids}`,
            {
                responseType: 'arraybuffer',
                headers: {
                    'Accept': 'application/octet-stream'
                },
                timeout: 120000
            }).then(response => {
                let blob = new Blob([response.data], { type: 'application/octet-stream' });
                let link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = 'invoices_' + moment.utc().format("DDMMYYYY") + '.zip';
                link.click();
            });
    }
}