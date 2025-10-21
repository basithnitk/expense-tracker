package com.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.expensetracker.model.Sku;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkuRepository extends JpaRepository<Sku, UUID> {
    boolean existsByL1AndL2AndL3AndL4AndL5(String l1, String l2, String l3, String l4, String l5);

    Optional<Sku> findByL1AndL2AndL3AndL4AndL5(String l1, String l2, String l3, String l4, String l5);

    // SkuRepository.java
    @Query("SELECT s FROM Sku s WHERE " +
            "(:l1 IS NULL OR s.l1 = :l1) AND " +
            "(:l2 IS NULL OR s.l2 = :l2) AND " +
            "(:l3 IS NULL OR s.l3 = :l3) AND " +
            "(:l4 IS NULL OR s.l4 = :l4) AND " +
            "(:l5 IS NULL OR s.l5 = :l5) AND " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Sku> searchByLevelsOrName(String l1, String l2, String l3, String l4, String l5, String name);

}
