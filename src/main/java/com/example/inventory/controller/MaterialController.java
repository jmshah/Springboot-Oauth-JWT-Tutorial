package com.example.inventory.controller;

import com.example.inventory.dto.MaterialRequest;
import com.example.inventory.dto.MaterialResponse;
import com.example.inventory.dto.QuantityUpdateRequest;
import com.example.inventory.entity.Material;
import com.example.inventory.repository.MaterialRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MaterialController - REST API for inventory management.
 *
 * Implements RBAC (Role-Based Access Control):
 * - ADMIN: Full CRUD access to all materials
 * - OPERATOR: Can read all materials, update quantities only
 * - AUDITOR: Read-only access to view materials and quantities
 *
 * Uses @PreAuthorize for method-level security in addition to URL-based security.
 */
@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@Slf4j
public class MaterialController {

    private final MaterialRepository materialRepository;

    /**
     * Get all materials.
     * Accessible by: ADMIN, OPERATOR, AUDITOR
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'AUDITOR')")
    public ResponseEntity<List<MaterialResponse>> getAllMaterials() {
        log.info("Fetching all materials");

        List<MaterialResponse> materials = materialRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(materials);
    }

    /**
     * Get a single material by ID.
     * Accessible by: ADMIN, OPERATOR, AUDITOR
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'AUDITOR')")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable Long id) {
        log.info("Fetching material with id: {}", id);

        return materialRepository.findById(id)
            .map(material -> ResponseEntity.ok(toResponse(material)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get a material by SKU.
     * Accessible by: ADMIN, OPERATOR, AUDITOR
     */
    @GetMapping("/sku/{sku}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'AUDITOR')")
    public ResponseEntity<MaterialResponse> getMaterialBySku(@PathVariable String sku) {
        log.info("Fetching material with SKU: {}", sku);

        return materialRepository.findBySku(sku)
            .map(material -> ResponseEntity.ok(toResponse(material)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new material.
     * Accessible by: ADMIN, OPERATOR
     *
     * Note: While OPERATOR can create materials, their primary role is quantity management.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<MaterialResponse> createMaterial(
            @Valid @RequestBody MaterialRequest request) {
        log.info("Creating new material with SKU: {}", request.getSku());

        // Check if SKU already exists
        if (materialRepository.existsBySku(request.getSku())) {
            return ResponseEntity.badRequest().build();
        }

        Material material = new Material(
            request.getSku(),
            request.getName(),
            request.getQuantity(),
            request.getUnit()
        );
        material.setDescription(request.getDescription());

        Material saved = materialRepository.save(material);
        log.info("Created material with id: {}", saved.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    /**
     * Update an existing material (full update).
     * Accessible by: ADMIN, OPERATOR
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<MaterialResponse> updateMaterial(
            @PathVariable Long id,
            @Valid @RequestBody MaterialRequest request) {
        log.info("Updating material with id: {}", id);

        return materialRepository.findById(id)
            .map(existing -> {
                existing.setSku(request.getSku());
                existing.setName(request.getName());
                existing.setDescription(request.getDescription());
                existing.setQuantity(request.getQuantity());
                existing.setUnit(request.getUnit());

                Material updated = materialRepository.save(existing);
                return ResponseEntity.ok(toResponse(updated));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update material quantity only.
     * This is the primary operation for OPERATOR role.
     * Accessible by: ADMIN, OPERATOR
     */
    @PatchMapping("/{id}/quantity")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<MaterialResponse> updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody QuantityUpdateRequest request) {
        log.info("Updating quantity for material id: {}, new quantity: {}", id, request.getQuantity());

        return materialRepository.findById(id)
            .map(material -> {
                Integer oldQuantity = material.getQuantity();
                material.setQuantity(request.getQuantity());

                log.info("Quantity changed from {} to {} for material: {}",
                    oldQuantity, request.getQuantity(), material.getSku());

                if (request.getReason() != null) {
                    log.info("Reason for change: {}", request.getReason());
                }

                Material updated = materialRepository.save(material);
                return ResponseEntity.ok(toResponse(updated));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a material.
     * Accessible by: ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long id) {
        log.info("Deleting material with id: {}", id);

        if (materialRepository.existsById(id)) {
            materialRepository.deleteById(id);
            log.info("Deleted material with id: {}", id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Convert Material entity to MaterialResponse DTO.
     */
    private MaterialResponse toResponse(Material material) {
        return MaterialResponse.builder()
            .id(material.getId())
            .sku(material.getSku())
            .name(material.getName())
            .description(material.getDescription())
            .quantity(material.getQuantity())
            .unit(material.getUnit())
            .createdAt(material.getCreatedAt())
            .updatedAt(material.getUpdatedAt())
            .build();
    }
}
