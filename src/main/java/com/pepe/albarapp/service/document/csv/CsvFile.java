package com.pepe.albarapp.service.document.csv;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.Invoice;

import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CsvFile {

	private static final String DELIMITER = ";";
	private static final String PREFIX = "=\"";
	private static final String SUFFIX = "\"";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(new Locale("es", "ES"));
	private static final String[] HEADERS = {"Numero de factura", "Cuenta contable", "Fecha", "Código cliente", "Razón social cliente", "NIF Cliente", "Alias cliente", "Cantidad producto", "Total", "Total IGIC"};

	private final String[] lines;

	public CsvFile(List<Invoice> invoices) {

		NUMBER_FORMAT.setMinimumFractionDigits(2);

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
				.concat(PREFIX).concat(Objects.toString(customer.getAccountingId(), "")).concat(SUFFIX).concat(DELIMITER)
				.concat(DATE_FORMAT.format(new Date(invoice.getIssuedTimestamp()))).concat(DELIMITER)
				.concat(String.valueOf(customer.getCode())).concat(DELIMITER)
				.concat(customer.getName()).concat(DELIMITER)
				.concat(customer.getFiscalId()).concat(DELIMITER)
				.concat(customer.getAlias()).concat(DELIMITER)
				.concat(String.valueOf(invoice.getProductQuantity())).concat(DELIMITER)
				.concat(PREFIX).concat(NUMBER_FORMAT.format(invoice.getTotal().setScale(2, RoundingMode.HALF_UP))).concat(SUFFIX).concat(DELIMITER)
				.concat(PREFIX).concat(NUMBER_FORMAT.format(invoice.getTaxTotal().setScale(2, RoundingMode.HALF_UP))).concat(SUFFIX).concat(DELIMITER);
	}
}
