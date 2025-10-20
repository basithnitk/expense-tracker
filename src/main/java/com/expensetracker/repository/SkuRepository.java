package com.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.model.Sku;

import java.util.UUID;

public interface SkuRepository extends JpaRepository<Sku, UUID> {
}
