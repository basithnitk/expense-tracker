package com.expensetracker.repository;

import com.expensetracker.model.PriceHistory;
import com.expensetracker.model.Sku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {
    List<PriceHistory> findBySkuOrderByEffectiveDateDesc(Sku sku);
}
