package com.pepe.albarapp.service;

import com.pepe.albarapp.persistence.domain.*;
import com.pepe.albarapp.persistence.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@Service
public class InvoiceService {

	@Autowired
    private InvoiceRepository invoiceRepository;
    
	@Autowired
    private DeliveryNoteRepository deliveryNoteRepository;

    @Transactional
    public List<Invoice> billProcess(Integer customerCodeFrom, Integer customerCodeTo, Long timestampFrom, Long timestampTo, Long issuedTimestamp) {

        // Create list of invoices created
        ArrayList<Invoice> invoicesCreated = new ArrayList<Invoice>();
        // Set pagination to a first page of 1000 elements
        Pageable pageable = PageRequest.of(0, 1000);

        // Iterate over all customer codes
        for (int i=customerCodeFrom;i<=customerCodeTo;i++) {

            // Find delivery notes for this customerCode that has not yet been billed
            Page<DeliveryNote> deliveryNotesToBill = deliveryNoteRepository.findDeliveryNotesToBill(i, timestampFrom, timestampTo, pageable);

            if (deliveryNotesToBill.getNumberOfElements() != 0) {
                // Create a new invoice and persist it
                Invoice invoice = new Invoice();
                invoice.setIssuedTimestamp(issuedTimestamp);
                Invoice persistedInvoice = invoiceRepository.save(invoice);
                invoicesCreated.add(persistedInvoice);

                // Set all delivery notes found relation to the created invoice
                deliveryNotesToBill.getContent().forEach(deliveryNote -> deliveryNote.setInvoice(persistedInvoice));
                deliveryNoteRepository.saveAll(deliveryNotesToBill.getContent());
            }
        }

        return invoicesCreated;

    }
}