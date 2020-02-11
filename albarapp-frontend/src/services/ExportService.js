import moment from "moment";
import InvoiceService from "./InvoiceService";
import DeliveryNoteService from "./DeliveryNoteService";

export default {
    convertChartDataToCSV(data, columnDelimiter, lineDelimiter) {
        var result, ctr, keys;

        data = data || null;
        if (data == null || !data.length) {
            return null;
        }

        var localColumnDelimiter = columnDelimiter || ',';
        var localLineDelimiter = lineDelimiter || '\n';

        keys = Object.keys(data[0]);

        result = '';
        result += keys.join(localColumnDelimiter);
        result += localLineDelimiter;

        data.forEach(function (item) {
            ctr = 0;
            keys.forEach(function (key) {
                if (ctr > 0) result += localColumnDelimiter;
                result += item[key];
                ctr++;
            });
            result += localLineDelimiter;
        });
        return result;
    },
    downloadCSV(csv, filename) {
        var link;

        if (csv == null) return;

        var localFilename = filename || 'Facturas.csv';

        var data = encodeURI(csv);
        link = document.createElement('a');
        link.setAttribute('href', "data:text/csv;charset=utf-8,%EF%BB%BF" + data);
        link.setAttribute('download', localFilename);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    },
    prettyPrintInvoices(invoices) {
        var prettyInvoices = [];
        invoices.forEach(invoice => {
            prettyInvoices.push({
                "Numero de factura": "F" + invoice.id,
                "Fecha": moment.utc(invoice.issuedTimestamp).format("DD/MM/YYYY"),
                "Código cliente": invoice.customerCode,
                "Razón social cliente": invoice.customerName,
                "NIF Cliente": invoice.customerFiscalId,
                "Alias cliente": invoice.customerAlias,
                "Cantidad producto": invoice.productQuantity,
                "Total": invoice.total.toLocaleString("es-ES", {
                    style: "currency",
                    currency: "EUR"
                })
            })
        })
        return prettyInvoices;
    },
    async exportEDI(invoiceId) {

        var invoice = await InvoiceService.get(invoiceId);

        var doc = document.implementation.createDocument("", "", null);

        var root = doc.createElement("root");
        root.setAttribute("id", "root");
        doc.appendChild(root);

        var totalAmount = doc.createElement("totalAmount");
        root.appendChild(totalAmount);
        var amount = doc.createElement("amount");
        totalAmount.appendChild(amount);
        amount.setAttribute("currencyISOcode", "EUR");
        amount.innerHTML = invoice.total.toLocaleString("en-EN", { useGrouping: false });

        var baseAmount = doc.createElement("baseAmount");
        root.appendChild(baseAmount);
        amount = doc.createElement("amount");
        baseAmount.appendChild(amount);
        amount.setAttribute("currencyISOcode", "EUR");
        amount.innerHTML = invoice.total.toLocaleString("en-EN", { useGrouping: false });

        var igicTax = doc.createElement("IGICTax");
        igicTax.setAttribute("percentage", "0");
        root.appendChild(igicTax);
        totalAmount = doc.createElement("totalAmount");
        igicTax.appendChild(totalAmount);
        amount = doc.createElement("amount");
        totalAmount.appendChild(amount);
        amount.setAttribute("currencyISOcode", "EUR");
        amount.innerHTML = invoice.total.toLocaleString("en-EN", { useGrouping: false });

        baseAmount = doc.createElement("baseAmount");
        igicTax.appendChild(baseAmount);
        amount = doc.createElement("amount");
        baseAmount.appendChild(amount);
        amount.setAttribute("currencyISOcode", "EUR");
        amount.innerHTML = invoice.total.toLocaleString("en-EN", { useGrouping: false });
        igicTax.appendChild(baseAmount);
        igicTax.appendChild(totalAmount);

        var taxAmount = doc.createElement("taxAmount");
        root.appendChild(taxAmount);
        amount = doc.createElement("amount");
        taxAmount.appendChild(amount);
        amount.setAttribute("currencyISOcode", "EUR");
        amount.innerHTML = "0";

        var payableAmount = doc.createElement("payableAmount");
        root.appendChild(payableAmount);
        amount = doc.createElement("amount");
        payableAmount.appendChild(amount);
        amount.setAttribute("currencyISOcode", "EUR");
        amount.innerHTML = invoice.total.toLocaleString("en-EN", { useGrouping: false });

        await InvoiceService.getDeliveryNotes(invoice);
        var index = 0;
        for (const deliveryNote of invoice.deliveryNotes) {
            await DeliveryNoteService.getDeliveryNoteItems(deliveryNote);
            for (const deliveryNoteItem of deliveryNote.deliveryNoteItems) {
                var total = deliveryNoteItem.quantity * deliveryNoteItem.price;
                var quantityTotal = deliveryNoteItem.quantity;
                var price = deliveryNoteItem.price;
                var auxDeliveryNr = deliveryNote.auxDeliveryNoteNr;

                var lineItem = doc.createElement("lineItem");
                root.appendChild(lineItem);
                lineItem.setAttribute("number", index + 1);

                var itemID = doc.createElement("itemID");
                lineItem.appendChild(itemID);
                var gtin = doc.createElement("gtin");
                itemID.appendChild(gtin);
                gtin.innerHTML = "08420000000015";

                var description = doc.createElement("description");
                lineItem.appendChild(description);
                var innerDescription = doc.createElement("description");
                description.appendChild(innerDescription);
                innerDescription.setAttribute("language", "ES");
                var text = doc.createElement("text");
                innerDescription.appendChild(text);
                text.innerHTML = "Huevos";

                var invoiced = doc.createElement("invoiced");
                lineItem.appendChild(invoiced);
                var quantity = doc.createElement("quantity");
                invoiced.appendChild(quantity);
                quantity.setAttribute("unitOfMeasure", "PCE");
                quantity.innerHTML = quantityTotal;

                var freeText = doc.createElement("freeText");
                lineItem.appendChild(freeText);
                freeText.setAttribute("type", "AAI");
                freeText.setAttribute("language", "ES");
                text = doc.createElement("text");
                freeText.appendChild(text);
                text.innerHTML = auxDeliveryNr;

                var netPrice = doc.createElement("netPrice");
                lineItem.appendChild(netPrice);
                amount = doc.createElement("amount");
                netPrice.appendChild(amount);
                amount.setAttribute("currencyISOcode", "EUR");
                amount.innerHTML = price;

                var grossPrice = doc.createElement("grossPrice");
                lineItem.appendChild(grossPrice);
                amount = doc.createElement("amount");
                grossPrice.appendChild(amount);
                amount.setAttribute("currencyISOcode", "EUR");
                amount.innerHTML = price;

                igicTax = doc.createElement("IGICTax");
                igicTax.setAttribute("percentage", "0");
                lineItem.appendChild(igicTax);
                baseAmount = doc.createElement("baseAmount");
                igicTax.appendChild(baseAmount);
                amount = doc.createElement("amount");
                baseAmount.appendChild(amount);
                amount.setAttribute("currencyISOcode", "EUR");
                amount.innerHTML = total.toLocaleString("en-EN", { useGrouping: false });
                taxAmount = doc.createElement("taxAmount");
                igicTax.appendChild(taxAmount);
                amount = doc.createElement("amount");
                taxAmount.appendChild(amount);
                amount.setAttribute("currencyISOcode", "EUR");
                amount.innerHTML = "0";

                var lineItemAmount = doc.createElement("lineItemAmount");
                lineItem.appendChild(lineItemAmount);
                amount = doc.createElement("amount");
                lineItemAmount.appendChild(amount);
                amount.setAttribute("currencyISOcode", "EUR");
                amount.innerHTML = total.toLocaleString("en-EN", { useGrouping: false });
                amount.innerHTML = total.toLocaleString("en-EN", { useGrouping: false });

                index++;
            }
        }

        var xml = new XMLSerializer().serializeToString(doc.getElementById("root"));

        xml = xml.replace('<root id="root">', "");
        xml = xml.replace('</root>', "");

        var link;
        var localFilename = 'Fra_'+ invoiceId + '.xml';
        var data = encodeURI(xml);
        link = document.createElement('a');
        link.setAttribute('href', "data:text/xml;charset=utf-8,%EF%BB%BF" + data);
        link.setAttribute('download', localFilename);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}