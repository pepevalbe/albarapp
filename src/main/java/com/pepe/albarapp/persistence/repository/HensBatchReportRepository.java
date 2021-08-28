package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.HensBatchReport;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HENS_BATCH_USER')")
public interface HensBatchReportRepository extends CrudRepository<HensBatchReport, String> {

	Set<HensBatchReport> findByHensBatchId(String hensBatchId);

	@Query("select hbr from HensBatchReport hbr where (?1 is null or hbr.hensBatch.id = ?1) and (?2 is null or hbr.reportTimestamp >= ?2) AND (?3 is null or hbr.reportTimestamp <= ?3)")
	Set<HensBatchReport> findByHensBatchIdAndReportTimestampBetween(String hensBatchId, Long fromReportTimestamp, Long toReportTimestamp);

	List<HensBatchReport> findByHensBatchIdOrderByReportTimestampAsc(String hensBatchId);

	Optional<HensBatchReport> findFirstByHensBatchIdAndReportTimestampLessThanAndWaterReadingNotNullOrderByReportTimestampDesc(String hensBatchId, long reportTimestamp);
}
