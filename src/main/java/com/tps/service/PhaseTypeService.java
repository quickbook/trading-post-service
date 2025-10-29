package com.tps.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tps.dto.PhaseTypeDto;
import com.tps.model.PhaseType;
import com.tps.repository.PhaseTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhaseTypeService {
    private final PhaseTypeRepository repo;

    public List<PhaseTypeDto> getAllActive() {
		try {
			return repo.findByActiveTrueOrderByIdAsc().stream()
					.map(p -> new PhaseTypeDto(p.getId(), p.getKey(), p.getLabel())).toList();
		} catch (Exception ex) {
			log.error("Error while fetching firm categories", ex);
			return Collections.emptyList(); // ✅ safely return empty list
		}
    }

    public PhaseType save(PhaseTypeDto dto) {
        PhaseType entity = new PhaseType();
        entity.setKey(dto.getKey());
        entity.setLabel(dto.getLabel());
        return repo.save(entity);
    }
}
