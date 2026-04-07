package com.example.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for material responses.
 * Excludes sensitive data and provides a clean API response format.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private Integer quantity;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
