package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.CustomerAecocInfo;
import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.service.document.aecoc.lineitem.LineItem;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JacksonXmlRootElement(localName = "invoicxml")
public class AecocInvoice {
	@JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsi")
	private final String xmlns1 = "http://www.w3.org/2001/XMLSchema-instance";
	@JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsd")
	private final String xmlns2 = "http://www.w3.org/2001/XMLSchema";
	@JacksonXmlProperty(isAttribute = true)
	private final String creationDate = "2019-12-31T00:00:00";
	@JacksonXmlProperty(isAttribute = true)
	private final String documentStatus = "ORIGINAL";
	@JacksonXmlProperty(isAttribute = true)
	private final String contentVersion = "AECOCv1.0";
	@JacksonXmlProperty(isAttribute = true)
	private final String documentStructureVersion = "1.0";
	@JacksonXmlProperty(isAttribute = true)
	private final String lastUpdateDate = "2002-01-24";

	private TypedEntityIdentification typedEntityIdentification;
	private CorporateOffice senderCorporateOffice;
	private CorporateOffice receiverCorporateOffice;
	private Party seller;
	private Party buyer;
	private Party shipParty;
	private Party payer;
	private Party invoicer;
	private Party invoicee;
	private AmountWrapper totalAmount;
	private AmountWrapper baseAmount;
	@JacksonXmlProperty(localName = "IGICTax")
	private IgicTax igicTax;
	private AmountWrapper taxAmount;
	private AmountWrapper payableAmount;
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "lineItem")
	private List<LineItem> lineItems;

	public static AecocInvoice build(Invoice invoice) {

		Customer customer = invoice.getCustomer();
		CustomerAecocInfo customerAecocInfo = customer.getCustomerAecocInfo();

		if (customer.getAddress() == null || customerAecocInfo == null || !customerAecocInfo.isValid()) {
			throw new ApiException(ApiError.ApiError011);
		}

		AecocInvoice aecocInvoice = new AecocInvoice();
		aecocInvoice.typedEntityIdentification = new TypedEntityIdentification(invoice.getId(), AecocConstants.glnOwner);
		aecocInvoice.senderCorporateOffice = AecocConstants.corporateOfficeOwner;
		aecocInvoice.receiverCorporateOffice = new CorporateOffice(customerAecocInfo.getReceiverGln());
		aecocInvoice.seller = AecocConstants.partyOwner;
		aecocInvoice.buyer = new Party(customerAecocInfo.getBuyerGln());
		aecocInvoice.shipParty = new Party(customerAecocInfo.getShipGln(), "SHIP_TO");
		aecocInvoice.payer = new Party(customerAecocInfo.getPayerGln());
		aecocInvoice.invoicer = AecocConstants.partyOwner;
		aecocInvoice.invoicee = new Party(customerAecocInfo.getInvoiceeGln());
		aecocInvoice.totalAmount = new AmountWrapper(invoice.getGrossTotal().add(invoice.getTaxTotal()));
		aecocInvoice.baseAmount = new AmountWrapper(invoice.getGrossTotal());
		aecocInvoice.igicTax = new IgicTax(invoice.getGrossTotal(), invoice.getTaxTotal(), invoice.getTaxTotal().divide(invoice.getGrossTotal()));
		aecocInvoice.taxAmount = new AmountWrapper(invoice.getTaxTotal());
		aecocInvoice.payableAmount = new AmountWrapper(invoice.getGrossTotal().add(invoice.getTaxTotal()));

		aecocInvoice.lineItems = invoice.getDeliveryNotes().stream()
				.flatMap(deliveryNote -> deliveryNote.getDeliveryNoteItems().stream().map(LineItem::new))
				.collect(Collectors.toList());

		for (int i = 0; i < aecocInvoice.lineItems.size(); i++) {
			aecocInvoice.lineItems.get(i).setOrder(i + 1);
		}
		return aecocInvoice;
	}
}
