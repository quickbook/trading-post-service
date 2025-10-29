package com.tps.service;

 
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tps.dto.FirmCategoryDto;
import com.tps.model.FirmCategory;
import com.tps.repository.FirmCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirmCategoryService {
    private final FirmCategoryRepository repo;

    public List<FirmCategoryDto> getAllActive() {
        try {
            return repo.findByActiveTrueOrderBySortOrderAsc()
                       .stream()
                       .map(c -> new FirmCategoryDto(c.getId(), c.getKey(), c.getLabel()))
                       .toList();
        } catch (Exception ex) {
            log.error("Error while fetching firm categories", ex);
            return Collections.emptyList(); // ✅ safely return empty list
        }
    }

    public FirmCategory save(FirmCategoryDto dto) {
        FirmCategory entity = new FirmCategory();
        entity.setKey(dto.getKey());
        entity.setLabel(dto.getLabel());
        return repo.save(entity);
    }
}

