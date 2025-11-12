package com.tps.mapper;

import org.springframework.stereotype.Component;
import com.tps.dto.FirmReviewDto;
import com.tps.model.FirmReview;

@Component
public class FirmReviewMapper {

    /**
     * Converts a FirmReview entity to a DTO for responses.
     */
    public FirmReviewDto toDto(FirmReview entity) {
        if (entity == null) {
            return null;
        }

        FirmReviewDto dto = new FirmReviewDto();
        dto.setId(entity.getId());
        dto.setReviewerName(entity.getReviewerName());     
        dto.setRating(entity.getRating());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setIsDeleted(entity.getIsDeleted());
        dto.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getFirm() != null) {
            dto.setFirmId(entity.getFirm().getId());
            dto.setPropName(entity.getFirm().getName());
        }
        
        return dto;
    }
}