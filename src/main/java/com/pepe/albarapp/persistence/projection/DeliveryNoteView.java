package com.pepe.albarapp.persistence.projection;

import com.pepe.albarapp.persistence.domain.Customer;
import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.persistence.domain.DeliveryNoteItem;
import com.pepe.albarapp.persistence.domain.Invoice;
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
