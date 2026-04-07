package com.example.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating materials.
 * Uses validation annotations to ensure data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialRequest {

    /**
     * Stock Keeping Unit - unique identifier for the material.
     */
    @NotBlank(message = "SKU is required")
    private String sku;

    /**
     * Material name.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * Optional description.
     */
    private String description;

    /**
     * Quantity in stock.
     * Must be non-negative.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    /**
     * Unit of measurement.
     */
    @NotBlank(message = "Unit is required")
    private String unit;
}
