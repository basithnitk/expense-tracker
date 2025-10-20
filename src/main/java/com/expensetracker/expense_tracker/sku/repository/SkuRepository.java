package com.expensetracker.expense_tracker.sku.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expensetracker.expense_tracker.sku.model.Sku;

import java.util.UUID;

public interface SkuRepository extends JpaRepository<Sku, UUID> {
}
