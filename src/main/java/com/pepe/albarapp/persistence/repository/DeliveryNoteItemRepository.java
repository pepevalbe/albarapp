package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.DeliveryNote;
import com.pepe.albarapp.persistence.domain.DeliveryNoteItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_BILLING_USER')")
public interface DeliveryNoteItemRepository extends CrudRepository<DeliveryNoteItem, String> {

	Set<DeliveryNoteItem> findByDeliveryNote(DeliveryNote deliveryNote);

	void deleteByDeliveryNoteId(Long deliveryNoteId);

	@Query(value = "select SUM(dni.quantity*dni.price*(1+dni.product.tax/100)) from DeliveryNoteItem dni where dni.deliveryNote.issuedTimestamp >= ?1 and dni.deliveryNote.issuedTimestamp <= ?2")
	Double calcTotalByIssuedTimestampRange(Long minTimestamp, Long maxTimestamp);

	@Query(value = "select SUM(dni.quantity*dni.price*(1+dni.product.tax/100)) from DeliveryNoteItem dni where dni.deliveryNote.issuedTimestamp >= ?1 and dni.deliveryNote.issuedTimestamp <= ?2 and dni.product.code in (?3)")
	Double calcTotalByIssuedTimestampRangeAndProductCodes(Long minTimestamp, Long maxTimestamp, List<Integer> productCodes);

	@Query(value = "select SUM(dni.quantity*dni.price*(1+dni.product.tax/100)) / SUM(dni.quantity) from DeliveryNoteItem dni")
	Double calcAveragePrice();

	@Query(value = "select SUM(dni.quantity*dni.price*(1+dni.product.tax/100)) / SUM(dni.quantity) from DeliveryNoteItem dni where dni.product.code in (?1)")
	Double calcAveragePriceByProductCodes(List<Integer> productCodes);
}
