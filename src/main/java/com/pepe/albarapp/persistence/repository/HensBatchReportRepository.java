package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.HensBatchReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface HensBatchReportRepository extends CrudRepository<HensBatchReport, String> {

	Set<HensBatchReport> findByHensBatchId(String hensBatchId);

	Optional<HensBatchReport> findFirstByReportTimestampBeforeAndWaterReadingNotNullOrderByReportTimestampDesc(long reportTimestamp);

	Optional<HensBatchReport> findFirstByReportTimestampAfterAndWaterReadingNotNullOrderByReportTimestampAsc(long reportTimestamp);

	Set<HensBatchReport> findByReportTimestampBetween(long fromReportTimestamp, long toReportTimestamp);

	@Query(value = "select report_timestamp from hens_batch_report where hens_batch_id = ?1 and report_timestamp < ?2 order by report_timestamp desc limit 1", nativeQuery = true)
	Optional<Long> findPreviousReport(String hensBatchId, long reportTimestamp);

	@Query(value = "select report_timestamp from hens_batch_report where hens_batch_id = ?1 and report_timestamp > ?2 order by report_timestamp asc limit 1", nativeQuery = true)
	Optional<Long> findNextReport(String hensBatchId, long reportTimestamp);
}
