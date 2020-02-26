package com.pepe.albarapp.service.document.csv;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.Invoice;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CsvFile {

	private static String DELIMITER = ";";
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/mm/yyyy");
	private static String[] HEADERS = {"Numero de factura", "Fecha", "Código cliente", "Razón social cliente", "NIF Cliente", "Alias cliente", "Cantidad producto", "Total"};

	private final String[] lines;

	public CsvFile(List<Invoice> invoices) {
		lines = invoices.stream().map(this::parseInvoice).toArray(String[]::new);
	}

	public void write(PrintWriter printWriter) {
		String header = String.join(DELIMITER, HEADERS);
		printWriter.println(header);
		for (String line : lines) {
			printWriter.println(line);
		}
	}

	private String parseInvoice(Invoice invoice) {
		Customer customer = invoice.getCustomer();
		return String.valueOf(invoice.getId()).concat(DELIMITER)
				.concat(DATE_FORMAT.format(new Date(invoice.getIssuedTimestamp()))).concat(DELIMITER)
				.concat(String.valueOf(customer.getCode())).concat(DELIMITER)
				.concat(customer.getName()).concat(DELIMITER)
				.concat(customer.getFiscalId()).concat(DELIMITER)
				.concat(customer.getAlias()).concat(DELIMITER)
				.concat(String.valueOf(invoice.getProductQuantity())).concat(DELIMITER)
				.concat(invoice.getTotal().toString()).concat(DELIMITER);
	}
}
