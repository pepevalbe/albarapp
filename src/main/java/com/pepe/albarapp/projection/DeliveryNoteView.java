package com.pepe.albarapp.projection;

import com.pepe.albarapp.persistance.Customer;
import com.pepe.albarapp.persistance.DeliveryNote;
import com.pepe.albarapp.persistance.DeliveryNoteItem;
import com.pepe.albarapp.persistance.Invoice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

@Projection(name = "deliveryNoteView", types = {DeliveryNote.class})
public interface DeliveryNoteView {

    @Value("#{'A' + target.id}")
    String getDeliveryNoteNumber();

    String getAuxDeliveryNoteNumber();

    long getIssuedTimestamp();

    Customer getCustomer();

    Invoice getInvoice();

    Set<DeliveryNoteItem> getDeliveryNoteItems();
}
