package com.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.model.Sku;

import java.util.Optional;
import java.util.UUID;

public interface SkuRepository extends JpaRepository<Sku, UUID> {
    boolean existsByL1AndL2AndL3AndL4AndL5(String l1, String l2, String l3, String l4, String l5);

    Optional<Sku> findByL1AndL2AndL3AndL4AndL5(String l1, String l2, String l3, String l4, String l5);

}
