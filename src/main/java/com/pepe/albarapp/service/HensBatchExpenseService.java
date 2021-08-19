package com.pepe.albarapp.service;

import com.pepe.albarapp.api.error.ApiError;
import com.pepe.albarapp.api.error.ApiException;
import com.pepe.albarapp.persistence.domain.HensBatchExpense;
import com.pepe.albarapp.persistence.repository.HensBatchExpenseRepository;
import com.pepe.albarapp.service.dto.HensBatchExpenseDto;
import lombok.extern.slf4j.Slf4j;
import com.pepe.albarapp.service.mapping.HensBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HensBatchExpenseService {
    
    @Autowired
	private HensBatchExpenseRepository hensBatchExpenseRepository;

	@Autowired
	private HensBatchMapper hensBatchMapper;

    @Transactional(readOnly = true)
	public HensBatchExpenseDto getHensBatchExpense(String hensBatchExpenseId) {

		return hensBatchExpenseRepository.findById(hensBatchExpenseId)
				.map(hensBatchExpense -> hensBatchMapper.map(hensBatchExpense))
				.orElseThrow(() -> new ApiException(ApiError.ApiError006));
	}

	@Transactional(readOnly = true)
	public Set<HensBatchExpenseDto> getAllHensBatchExpenses(String hensBatchId) {

		// Get hens batch expenses for the given hens batch id
		Set<HensBatchExpense> hensBatchExpenses = hensBatchExpenseRepository.findByHensBatchId(hensBatchId);

		if (hensBatchExpenses.isEmpty()) {
			return Collections.emptySet();
		}

		return hensBatchExpenses.stream().map(hensBatchMapper::map).collect(Collectors.toSet());
	}

    @Transactional
	public HensBatchExpenseDto createHensBatchExpense(HensBatchExpenseDto hensBatchExpenseDto) {

		// Create hens batch report
		HensBatchExpense createdHensBatchExpense = hensBatchExpenseRepository.save(hensBatchMapper.map(hensBatchExpenseDto));
		return hensBatchMapper.map(createdHensBatchExpense);
	}

	@Transactional
	public HensBatchExpenseDto updateHensBatchExpense(HensBatchExpenseDto hensBatchExpenseDto) {

		// Get hens batch report
		HensBatchExpense hensBatchExpense = hensBatchExpenseRepository.findById(hensBatchExpenseDto.getId())
				.orElseThrow(() -> new ApiException(ApiError.ApiError006));

		// Update hens batch report
		HensBatchExpense updatedHensBatchExpense = hensBatchExpenseRepository.save(hensBatchMapper.map(hensBatchExpenseDto));
		return hensBatchMapper.map(updatedHensBatchExpense);
	}

    @Transactional
	public boolean deleteHensBatchExpense(String hensBatchExpenseId) {

		if (!hensBatchExpenseRepository.findById(hensBatchExpenseId).isPresent()) {
			return false;
		}

		hensBatchExpenseRepository.deleteById(hensBatchExpenseId);
		return true;
	}
}
