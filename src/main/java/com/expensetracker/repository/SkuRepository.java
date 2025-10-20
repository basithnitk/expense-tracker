package com.expensetracker.sku.repository;

import com.expensetracker.sku.model.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SkuRepository extends JpaRepository<Sku, UUID> {
}
