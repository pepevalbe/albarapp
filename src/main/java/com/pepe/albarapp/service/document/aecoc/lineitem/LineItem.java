package com.pepe.albarapp.service.document.aecoc.lineitem;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.pepe.albarapp.persistence.domain.DeliveryNoteItem;
import com.pepe.albarapp.service.document.aecoc.Amount;
import com.pepe.albarapp.service.document.aecoc.IgicTax;
import lombok.Data;

@Data
public class LineItem {

	@JacksonXmlProperty(isAttribute = true)
	private String number;

	private final ItemID itemID = new ItemID("08420000000015");
	private Description description;
	private Invoiced invoiced;
	private Text freeText;
	private Amount netPrice;
	private Amount grossPrice;
	@JacksonXmlProperty(localName = "IGICTax")
	private IgicTax igicTax;
	private Amount lineItemAmount;

	public LineItem(DeliveryNoteItem item) {
		description = new Description(item.getProduct().getName());
		invoiced = new Invoiced(item.getQuantity());
		String auxDeliveryNoteNr = item.getDeliveryNote().getAuxDeliveryNoteNr();
		freeText = auxDeliveryNoteNr != null ? new Text(auxDeliveryNoteNr, "AAI") : null;
		netPrice = new Amount(item.getPrice() * (1 + item.getProduct().getTax()));
		grossPrice = new Amount(item.getPrice());
		igicTax = new IgicTax(item.getGrossTotal(), item.getTaxTotal(), item.getProduct().getTax());
		lineItemAmount = new Amount(item.getTotal());
	}

	public void setOrder(int order) {
		number = String.valueOf(order);
	}
}
