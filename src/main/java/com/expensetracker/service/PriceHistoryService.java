package com.expensetracker.service;

import com.expensetracker.model.PriceHistory;
import com.expensetracker.model.Sku;
import com.expensetracker.repository.PriceHistoryRepository;
import com.expensetracker.repository.SkuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PriceHistoryService {

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    @Autowired
    private SkuRepository skuRepository;

    public PriceHistory addPriceEntry(UUID skuId, String price) {
        Optional<Sku> skuOpt = skuRepository.findById(skuId);
        if (skuOpt.isEmpty()) {
            throw new RuntimeException("SKU not found: " + skuId);
        }

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setSku(skuOpt.get());
        priceHistory.setPricePerUnit(new BigDecimal(price));
        priceHistory.setCreatedAt(LocalDateTime.now());

        return priceHistoryRepository.save(priceHistory);
    }

    public List<PriceHistory> getPriceHistory(UUID skuId) {
        return priceHistoryRepository.findBySkuIdOrderByCreatedAtDesc(skuId);
    }

    /** Record a new price change for a SKU */
    public PriceHistory recordPriceChange(Sku sku, BigDecimal price) {
        // Fetch latest price
        PriceHistory latest = getLatestPriceHistory(sku);

        if (latest != null && latest.getPricePerUnit().compareTo(price) == 0) {
            // No change
            return latest;
        }

        PriceHistory ph = PriceHistory.builder()
                .sku(sku)
                .pricePerUnit(price)
                .createdAt(LocalDateTime.now())
                .build();

        return priceHistoryRepository.save(ph);
    }

    /** Get latest price as BigDecimal */
    public BigDecimal getLatestPrice(Sku sku) {
        PriceHistory latest = getLatestPriceHistory(sku);
        return latest != null ? latest.getPricePerUnit() : BigDecimal.ZERO;
    }

    /** Helper: fetch latest PriceHistory entity */
    public PriceHistory getLatestPriceHistory(Sku sku) {
        return priceHistoryRepository.findBySkuOrderByCreatedAtDesc(sku)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /** Get all price history entries for a SKU, newest first */
    public List<PriceHistory> getPriceHistory(Sku sku) {
        return priceHistoryRepository.findBySkuOrderByCreatedAtDesc(sku);
    }
}
