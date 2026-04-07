package com.example.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating material quantities only.
 * Used by OPERATOR role which can only modify quantities, not full material details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuantityUpdateRequest {

    /**
     * New quantity value.
     * Must be non-negative.
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    /**
     * Optional reason for the quantity change.
     * Useful for audit trails.
     */
    private String reason;
}
