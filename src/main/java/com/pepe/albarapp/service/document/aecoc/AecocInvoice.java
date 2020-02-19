package com.pepe.albarapp.service.document.aecoc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.service.document.aecoc.lineitem.LineItem;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JacksonXmlRootElement(localName = "invoicxml")
public class AecocInvoice {
	@JacksonXmlProperty(isAttribute = true, localName = "xlmns:xsi")
	private final String xmlns1 = "http://www.w3.org/2001/XMLSchema-instance";
	@JacksonXmlProperty(isAttribute = true, localName = "xlmns:xsd")
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
	private Amount totalAmount;
	private Amount baseAmount;
	@JacksonXmlProperty(localName = "IGICTax")
	private IgicTax igicTax;
	private Amount taxAmount;
	private Amount payableAmount;
	@JacksonXmlElementWrapper(useWrapping = false, localName = "lineItem")
	private List<LineItem> lineItems;

	public static AecocInvoice build(Invoice invoice) {

		AecocInvoice aecocInvoice = new AecocInvoice();
		aecocInvoice.typedEntityIdentification = new TypedEntityIdentification(invoice.getId(), AecocConstants.glnOwner, AecocConstants.partyNameOwner);
		aecocInvoice.senderCorporateOffice = AecocConstants.corporateOfficeOwner;
		aecocInvoice.receiverCorporateOffice = new CorporateOffice("glnBuyer", "nameBuyer", "VATregistrationNumber", "streetAndNumber", "cityName", "postCode");
		aecocInvoice.seller = AecocConstants.partyOwner;
		aecocInvoice.buyer = new Party("glnBuyer", "nameBuyer");
		aecocInvoice.shipParty = new Party("glnShip", "nameShip", "SHIP_TO");
		aecocInvoice.payer = new Party("glnBuyer", "nameBuyer");
		aecocInvoice.invoicer = AecocConstants.partyOwner;
		aecocInvoice.invoicee = new Party("glnBuyer", "nameBuyer");
		aecocInvoice.totalAmount = new Amount(invoice.getGrossTotal() + invoice.getTaxTotal());
		aecocInvoice.baseAmount = new Amount(invoice.getGrossTotal());
		aecocInvoice.igicTax = new IgicTax(invoice.getGrossTotal(), invoice.getTaxTotal(), invoice.getTaxTotal() / invoice.getGrossTotal());
		aecocInvoice.taxAmount = new Amount(invoice.getTaxTotal());
		aecocInvoice.payableAmount = new Amount(invoice.getGrossTotal() + invoice.getTaxTotal());

		aecocInvoice.lineItems = invoice.getDeliveryNotes().stream()
				.flatMap(deliveryNote -> deliveryNote.getDeliveryNoteItems().stream().map(LineItem::new))
				.collect(Collectors.toList());

		for (int i = 0; i < aecocInvoice.lineItems.size(); i++) {
			aecocInvoice.lineItems.get(i).setOrder(i + 1);
		}
		return aecocInvoice;
	}
}
