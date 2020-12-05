package com.pepe.albarapp.persistence.repository;

import com.pepe.albarapp.persistence.domain.WaterReading;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:8080")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
public interface WaterReadingRepository extends CrudRepository<WaterReading, String> {

	@Query(value = "select w from WaterReading w  where (w.hensBatch.id = ?1) and (?2 is null or w.readingTimestamp >= ?2) and (?3 is null or w.readingTimestamp <= ?3)")
	Set<WaterReading> findByHensBatchIdAndTimestampRange(String hensBatchId, Long timestampFrom, Long timestampTo);

	@Query(value = "select reading_value from water_reading where hens_batch_id = ?1 and reading_timestamp < ?2 order by reading_timestamp desc limit 1", nativeQuery = true)
	Optional<Double> findPreviousWaterReading(String hensBatchId, long readingTimestamp);

	@Query(value = "select reading_value from water_reading where hens_batch_id = ?1 and reading_timestamp > ?2 order by reading_timestamp asc limit 1", nativeQuery = true)
	Optional<Double> findNextWaterReading(String hensBatchId, long readingTimestamp);
}
