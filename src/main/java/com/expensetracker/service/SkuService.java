package com.expensetracker.service;

import com.expensetracker.model.PriceHistory;
import com.expensetracker.model.Sku;
import com.expensetracker.repository.PriceHistoryRepository;
import com.expensetracker.repository.SkuRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SkuService {

    private final SkuRepository skuRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    public SkuService(SkuRepository skuRepository, PriceHistoryRepository priceHistoryRepository) {
        this.skuRepository = skuRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    /**
     * Create a new SKU or return existing one if taxonomy exists
     */
    public Sku createOrReturnSku(Sku sku) {
        Optional<Sku> existingOpt = skuRepository.findByL1AndL2AndL3AndL4AndL5(
                sku.getL1(), sku.getL2(), sku.getL3(), sku.getL4(), sku.getL5());

        if (existingOpt.isPresent()) {
            return existingOpt.get(); // taxonomy exists, return existing SKU
        }

        return skuRepository.save(sku); // create new SKU
    }

    /**
     * Log a new price for a SKU
     */
    public PriceHistory recordPriceChange(Sku sku, BigDecimal price) {
        // Get the latest price if exists
        Optional<PriceHistory> latestOpt = priceHistoryRepository.findBySkuOrderByCreatedAtDesc(sku)
                .stream()
                .findFirst();

        if (latestOpt.isPresent() && latestOpt.get().getPricePerUnit().compareTo(price) == 0) {
            // No change in price, skip recording
            return latestOpt.get();
        }

        PriceHistory ph = PriceHistory.builder()
                .sku(sku)
                .pricePerUnit(price)
                .createdAt(LocalDateTime.now())
                .build();

        return priceHistoryRepository.save(ph);
    }

    /**
     * Fetch full price history for a SKU (latest first)
     */
    public List<PriceHistory> getPriceHistory(Sku sku) {
        return priceHistoryRepository.findBySkuOrderByCreatedAtDesc(sku);
    }

    /**
     * Fetch latest price for a SKU
     */
    public BigDecimal getLatestPrice(Sku sku) {
        return priceHistoryRepository.findBySkuOrderByCreatedAtDesc(sku)
                .stream()
                .findFirst()
                .map(PriceHistory::getPricePerUnit)
                .orElse(BigDecimal.ZERO); // or throw exception
    }

    // Fetch SKU by ID
    public Optional<Sku> getSkuById(UUID id) {
            return skuRepository.findById(id);
    }

    // Search SKUs by levels or name
    public List<Sku> searchSkus(String l1, String l2, String l3, String l4, String l5, String name) {
        // Example using JPA Specification or Query methods
        // For simplicity, here is a placeholder call (implement with Criteria API or
        // custom query)
        return skuRepository.searchByLevelsOrName(l1, l2, l3, l4, l5, name);
    }

}
