import moment from "moment";

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

        //if (!csv.match(/^data:text\/csv/i)) {
            csv = 'data:text/csv;charset=utf-8,' + csv;
        //}

        var data = encodeURI(csv);
        link = document.createElement('a');
        link.setAttribute('href', data);
        link.setAttribute('download', localFilename);
        document.body.appendChild(link); // Required for FF
        link.click();
        document.body.removeChild(link);
    },
    prettyPrintInvoices(invoices) {
        var prettyInvoices = [];
        invoices.forEach(invoice => {
            prettyInvoices.push({
                "Numero de factura": "F" + invoice.id,
                "Fecha": moment(invoice.issuedTimestamp).format("DD/MM/YYYY"),
                "Cliente razon social": invoice.customerName,
                "Cliente NIF": invoice.customerFiscalId,
                "Cliente alias": invoice.customerAlias,
                "Cantidad producto": invoice.productQuantity,
                "Total": invoice.total.toLocaleString("es-ES", {
                    style: "currency",
                    currency: "EUR"
                })
            })
        })
        return prettyInvoices;
    }
}