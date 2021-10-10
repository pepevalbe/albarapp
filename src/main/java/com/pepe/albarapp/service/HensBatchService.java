package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.repository.HensBatchRepository;
import com.pepe.albarapp.service.dto.HensBatchDto;
import com.pepe.albarapp.service.mapping.HensBatchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HensBatchService {

    @Autowired
    private HensBatchMapper hensBatchMapper;

    @Autowired
    private HensBatchRepository hensBatchRepository;

    @Transactional(readOnly = true)
    public List<HensBatchDto> getHensBatches() {
        return hensBatchRepository.findAll().stream().map(hensBatchMapper::map).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HensBatchDto> getActiveHensBatches() {
        return hensBatchRepository.findByEndTimestampIsNull().stream().map(hensBatchMapper::map).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HensBatchDto getHensBatch(String id) {
        return hensBatchMapper.map(hensBatchRepository.findById(id).orElseThrow(() -> new ApiException(ApiError.ApiError006)));
    }

    @Transactional
    public HensBatchDto persistHensBatch(HensBatchDto hensBatchDto) {
        return hensBatchMapper.map(hensBatchRepository.save(hensBatchMapper.map(hensBatchDto)));
    }
}
