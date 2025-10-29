package com.tps.controller;

import java.net.URI;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tps.dto.ApiResponse;
import com.tps.dto.Firm;
import com.tps.dto.FirmFilterOptionsDto;
import com.tps.dto.FirmQuery; 
import com.tps.service.CommonDataService;
import com.tps.service.FirmCategoryService;
import com.tps.service.FirmService;
import com.tps.service.PhaseTypeService;

@RestController
@RequestMapping("/api/v1/firms")
@RequiredArgsConstructor
@Validated
public class FirmsController {

    private final FirmCategoryService firmCategoryService;
    private final PhaseTypeService phaseTypeService;
    private final CommonDataService commonDataService;
    private final FirmService firmService;

    // ---------- Meta / filter options ----------  // Waiting for Other Tables
    @GetMapping("/filter-options")
    public ResponseEntity<ApiResponse<FirmFilterOptionsDto>> getFilterOptions() {
        FirmFilterOptionsDto options = new FirmFilterOptionsDto(
                firmCategoryService.getAllActive(),
                phaseTypeService.getAllActive(),
                commonDataService.getSortOptions(),
                commonDataService.getMinAccountSizeOptions()
        );

        return ResponseEntity.ok(
                ApiResponse.<FirmFilterOptionsDto>builder()
                        .success(true)
                        .message("Filter options fetched successfully")
                        .data(options)
                        .status(HttpStatus.OK)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- List with pagination + optional filters ----------
    @GetMapping                     //Pagination is out of my knowledge. Need expert Help
    public ResponseEntity<ApiResponse<Page<Firm>>> listFirms(
            @PageableDefault(size = 20) Pageable pageable,
            @Valid FirmQuery query // populated from request params
    ) {
        Page<Firm> page = firmService.find(query, pageable); //Lack of Knowledge

        return ResponseEntity.ok(
                ApiResponse.<Page<Firm>>builder()
                        .success(true)
                        .message("Firms fetched successfully")
                        .data(page)
                        .status(HttpStatus.OK)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- Get by id ----------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Firm>> getFirm(@PathVariable Long id) {
        Firm firm = firmService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<Firm>builder()
                        .success(true)
                        .message("Firm fetched successfully")
                        .data(firm)
                        .status(HttpStatus.OK)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- Create ----------
    @PostMapping
    public ResponseEntity<ApiResponse<Firm>> create(@Valid @RequestBody Firm firm) {
        Firm created = firmService.createFirm(firm);

        return ResponseEntity.created(URI.create("/api/v1/firms/" + created.getId()))
                .body(ApiResponse.<Firm>builder()
                        .success(true)
                        .message("Firm created successfully")
                        .data(created)
                        .status(HttpStatus.CREATED)
                        .timestamp(System.currentTimeMillis())
                        .build());
    }

    // ---------- Update (full) ----------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Firm>> update(
            @PathVariable Long id,
            @Valid @RequestBody Firm firmDto
    ) {
        Firm updated = firmService.updateFirm(id, firmDto);
        return ResponseEntity.ok(
                ApiResponse.<Firm>builder()
                        .success(true)
                        .message("Firm updated successfully")
                        .data(updated)
                        .status(HttpStatus.OK)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- Partial update (optional) ----------
    @PatchMapping("/{id}")   // Still some small bugs
    public ResponseEntity<ApiResponse<Firm>> patch(
            @PathVariable Long id,
            @RequestBody Firm partial // or a dedicated Patch DTO
    ) {
        Firm updated = firmService.patchFirm(id, partial);
        return ResponseEntity.ok(
                ApiResponse.<Firm>builder()
                        .success(true)
                        .message("Firm partially updated successfully")
                        .data(updated)
                        .status(HttpStatus.OK)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- Delete ----------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) { 
        firmService.deleteFirm(id);
        // Build the success response body
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(true)
                .message("Firm deleted successfully")
                .data(null) // No data needed for delete confirmation
                .status(HttpStatus.OK) // Use 200 OK
                .timestamp(System.currentTimeMillis())
                .build();
        return ResponseEntity.ok(response); // Return 200 OK with the body
    }
}