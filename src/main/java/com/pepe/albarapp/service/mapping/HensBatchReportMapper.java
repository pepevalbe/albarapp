package com.pepe.albarapp.service.mapping;

import com.pepe.albarapp.persistence.domain.HensBatchReport;
import com.pepe.albarapp.service.dto.report.HensBatchInfoDto;
import com.pepe.albarapp.service.dto.report.HensBatchReportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HensBatchReportMapper {

	@Mapping(source = "hensBatch.id", target = "hensBatchId")
	HensBatchReportDto map(HensBatchReport hensBatchReport);

	@Mapping(source = "hensBatchId", target = "hensBatch.id")
	HensBatchReport map(HensBatchReportDto hensBatchReportDto);

	@Mapping(source = "id", target = "hensBatchReportId")
	@Mapping(source = "hensBatch.id", target = "hensBatchId")
	HensBatchInfoDto mapToHensBatchInfo(HensBatchReport hensBatchReport);
}
