package com.pepe.albarapp.service.document.aecoc.lineitem;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.DeliveryNoteItem;
import com.pepe.albarapp.service.document.aecoc.AmountWrapper;
import com.pepe.albarapp.service.document.aecoc.IgicTax;
import lombok.Data;

@Data
public class LineItem {

	@JacksonXmlProperty(isAttribute = true)
	private String number;

	private ItemID itemID;
	private Description description;
	private Invoiced invoiced;
	private Text freeText;
	private AmountWrapper netPrice;
	private AmountWrapper grossPrice;
	@JacksonXmlProperty(localName = "IGICTax")
	private IgicTax igicTax;
	private AmountWrapper lineItemAmount;

	public LineItem(DeliveryNoteItem item) {
		if (item.getProduct().getAecocGtin() == null) {
			throw new ApiException(ApiError.ApiError011);
		}
		itemID = new ItemID(item.getProduct().getAecocGtin());
		description = new Description(item.getProduct().getName());
		invoiced = new Invoiced(item.getQuantity());
		String auxDeliveryNoteNr = item.getDeliveryNote().getAuxDeliveryNoteNr();
		freeText = auxDeliveryNoteNr != null ? new Text(auxDeliveryNoteNr, "AAI") : null;
		netPrice = new AmountWrapper(item.getPrice() * (1 + item.getProduct().getTax()));
		grossPrice = new AmountWrapper(item.getPrice());
		igicTax = new IgicTax(item.getGrossTotal(), item.getTaxTotal(), item.getProduct().getTax());
		lineItemAmount = new AmountWrapper(item.getTotal());
	}

	public void setOrder(int order) {
		number = String.valueOf(order);
	}
}
