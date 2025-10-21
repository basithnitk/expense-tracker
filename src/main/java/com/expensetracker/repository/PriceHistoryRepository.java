package com.expensetracker.repository;

import com.expensetracker.model.PriceHistory;
import com.expensetracker.model.Sku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {
    List<PriceHistory> findBySkuOrderByEffectiveDateDesc(Sku sku);

    Optional<PriceHistory> findTopBySkuOrderByEffectiveDateDesc(Sku sku);

    List<PriceHistory> findBySkuIdOrderByCreatedAtDesc(UUID skuId);

        // Fetch all price history for a SKU, newest first
    List<PriceHistory> findBySkuOrderByCreatedAtDesc(Sku sku);

    // Optionally: fetch latest price directly
    default PriceHistory findLatestPrice(Sku sku) {
        return findBySkuOrderByCreatedAtDesc(sku)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
