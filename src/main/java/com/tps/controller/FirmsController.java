package com.tps.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.ApiResponse;
import com.tps.dto.Firm;
import com.tps.dto.FirmFilterOptionsDto;
import com.tps.dto.FirmQuery;
import com.tps.service.CommonDataService;
import com.tps.service.FirmCategoryService;
import com.tps.service.FirmService;
import com.tps.service.PhaseTypeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tradingpost/api/v1/firms")
@RequiredArgsConstructor
@Validated
public class FirmsController {

    private final FirmCategoryService firmCategoryService;
    private final PhaseTypeService phaseTypeService;
    private final CommonDataService commonDataService;
    private final FirmService firmService;

    // ---------- Meta / filter options ----------  // Waiting for Other Tables
    @GetMapping("/filter-options")
    public ResponseEntity<ApiResponse<FirmFilterOptionsDto>> getFilterOptions(HttpServletRequest request) {
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
                        .path(request.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- List with pagination + optional filters ----------
    @GetMapping                     //Pagination is out of my knowledge. Need expert Help
    public ResponseEntity<ApiResponse<Page<Firm>>> listFirms(
            @PageableDefault(size = 20) Pageable pageable,
            @Valid FirmQuery query, // populated from request params
            HttpServletRequest request
    ) {
        Page<Firm> page = firmService.find(query, pageable); //Lack of Knowledge

        return ResponseEntity.ok(
                ApiResponse.<Page<Firm>>builder()
                        .success(true)
                        .message("Firms fetched successfully")
                        .data(page)
                        .status(HttpStatus.OK)
                        .path(request.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- Get by id ----------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Firm>> getFirm(@PathVariable Long id, HttpServletRequest request) {
        Firm firm = firmService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<Firm>builder()
                        .success(true)
                        .message("Firm fetched successfully")
                        .data(firm)
                        .status(HttpStatus.OK)
                        .path(request.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- Create ----------
    @PostMapping
    public ResponseEntity<ApiResponse<Firm>> create(@Valid @RequestBody Firm firm, HttpServletRequest request) {
        Firm created = firmService.createFirm(firm);

        return ResponseEntity.created(URI.create("/api/v1/firms/" + created.getId()))
                .body(ApiResponse.<Firm>builder()
                        .success(true)
                        .message("Firm created successfully")
                        .data(created)
                        .status(HttpStatus.CREATED)
                        .path(request.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build());
    }

    // ---------- Update (full) ----------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Firm>> update(
            @PathVariable Long id,
            @Valid @RequestBody Firm firmDto,
            HttpServletRequest request
    ) {
        Firm updated = firmService.updateFirm(id, firmDto);
        return ResponseEntity.ok(
                ApiResponse.<Firm>builder()
                        .success(true)
                        .message("Firm updated successfully")
                        .data(updated)
                        .path(request.getRequestURI()) 
                        .status(HttpStatus.OK)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- Partial update (optional) ----------
    @PatchMapping("/{id}")   // Still some small bugs
    public ResponseEntity<ApiResponse<Firm>> patch(
            @PathVariable Long id,
            @Valid @RequestBody Firm partial,
            HttpServletRequest request
    ) {
        Firm updated = firmService.patchFirm(id, partial);
        return ResponseEntity.ok(
                ApiResponse.<Firm>builder()
                        .success(true)
                        .message("Firm partially updated successfully")
                        .data(updated)
                        .status(HttpStatus.OK)
                        .path(request.getRequestURI()) 
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    // ---------- Delete ----------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id, HttpServletRequest request) { 
        firmService.deleteFirm(id);
        // Build the success response body
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(true)
                .message("Firm deleted successfully")
                .data(null) // No data needed for delete confirmation
                .status(HttpStatus.OK) // Use 200 OK
                .path(request.getRequestURI()) 
                .timestamp(System.currentTimeMillis())
                .build();
        return ResponseEntity.ok(response); // Return 200 OK with the body
    }
}