package com.example.inventory.repository;

import com.example.inventory.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Material entity.
 * Provides CRUD operations and custom query methods for inventory management.
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    /**
     * Find a material by its SKU (Stock Keeping Unit).
     */
    Optional<Material> findBySku(String sku);

    /**
     * Check if a SKU already exists.
     * Used for validation when creating new materials.
     */
    boolean existsBySku(String sku);
}
