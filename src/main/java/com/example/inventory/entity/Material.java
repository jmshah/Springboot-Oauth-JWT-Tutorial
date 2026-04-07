package com.example.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Material entity - represents an inventory item in the warehouse.
 *
 * This is the main domain entity for the inventory management system.
 * Different users have different permissions on this entity based on their roles.
 */
@Entity
@Table(name = "materials")
@Data
@NoArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique SKU (Stock Keeping Unit) code for the material.
     */
    @Column(name = "sku", unique = true, nullable = false)
    private String sku;

    /**
     * Human-readable name of the material.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Optional description of the material.
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Current quantity in stock.
     * Only OPERATOR and ADMIN roles can modify this.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    /**
     * Unit of measurement (e.g., "pieces", "kg", "liters").
     */
    @Column(name = "unit", nullable = false)
    private String unit = "pieces";

    /**
     * Timestamp when the record was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the record was last updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Automatically set createdAt timestamp before persisting.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Automatically update updatedAt timestamp before updating.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Material(String sku, String name, Integer quantity, String unit) {
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
}
