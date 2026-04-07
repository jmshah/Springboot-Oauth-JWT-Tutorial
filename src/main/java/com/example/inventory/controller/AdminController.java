package com.example.inventory.controller;

import com.example.inventory.entity.Material;
import com.example.inventory.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminController - administrative endpoints for ADMIN role only.
 *
 * Contains operations that should only be accessible to administrators,
 * such as system statistics and bulk operations.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final MaterialRepository materialRepository;

    /**
     * Get system statistics.
     * Only accessible by ADMIN role.
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        log.info("Admin fetching system statistics");

        List<Material> allMaterials = materialRepository.findAll();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMaterials", allMaterials.size());
        stats.put("totalQuantity", allMaterials.stream()
            .mapToInt(Material::getQuantity)
            .sum());
        stats.put("lowStockItems", allMaterials.stream()
            .filter(m -> m.getQuantity() < 100)
            .count());

        return ResponseEntity.ok(stats);
    }
}
