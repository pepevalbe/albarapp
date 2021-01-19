package com.pepe.albarapp;

import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.persistence.domain.DeliveryNoteItem;
import com.pepe.albarapp.persistence.domain.Invoice;
import com.pepe.albarapp.persistence.domain.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;

@RunWith(SpringRunner.class)
public class InvoiceCalculationTests {

	private DeliveryNoteItem deliveryNoteItem;
	private DeliveryNote deliveryNote;
	private Invoice invoice;

	@Before
	public void initInvoice() {

		Product product = new Product();
		product.setTax(21);

		deliveryNoteItem = new DeliveryNoteItem();
		deliveryNoteItem.setPrice(4.9);
		deliveryNoteItem.setQuantity(2);
		deliveryNoteItem.setProduct(product);

		deliveryNote = new DeliveryNote();
		deliveryNote.setDeliveryNoteItems(new HashSet<>(Arrays.asList(deliveryNoteItem, deliveryNoteItem)));

		invoice = new Invoice();
		invoice.setDeliveryNotes(new HashSet<>(Arrays.asList(deliveryNote, deliveryNote)));
	}

	@Test
	public void deliveryNoteItemTest() {

		Assert.assertEquals("9.8", deliveryNoteItem.getGrossTotal().stripTrailingZeros().toPlainString());
		Assert.assertEquals("2.058", deliveryNoteItem.getTaxTotal().stripTrailingZeros().toPlainString());
		Assert.assertEquals("11.858", deliveryNoteItem.getTotal().stripTrailingZeros().toPlainString());
	}


	@Test
	public void deliveryNoteTest() {

		Assert.assertEquals("9.8", deliveryNote.getGrossTotal().stripTrailingZeros().toPlainString());
		Assert.assertEquals("2.058", deliveryNote.getTaxTotal().stripTrailingZeros().toPlainString());
		Assert.assertEquals("11.858", deliveryNote.getTotal().stripTrailingZeros().toPlainString());
	}

	@Test
	public void invoiceTest() {

		Assert.assertEquals(2, invoice.getProductQuantity());
		Assert.assertEquals("9.8", invoice.getGrossTotal().stripTrailingZeros().toPlainString());
		Assert.assertEquals("2.058", invoice.getTaxTotal().stripTrailingZeros().toPlainString());
		Assert.assertEquals("11.858", invoice.getTotal().stripTrailingZeros().toPlainString());
	}
}
