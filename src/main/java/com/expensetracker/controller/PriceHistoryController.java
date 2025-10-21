package com.expensetracker.controller;

import com.expensetracker.model.PriceHistory;
import com.expensetracker.model.Sku;
import com.expensetracker.repository.PriceHistoryRepository;
import com.expensetracker.repository.SkuRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/price-history")
public class PriceHistoryController {

    private final PriceHistoryRepository priceHistoryRepository;
    private final SkuRepository skuRepository;

    public PriceHistoryController(PriceHistoryRepository priceHistoryRepository, SkuRepository skuRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.skuRepository = skuRepository;
    }

    @PostMapping("/{skuId}")
    public PriceHistory addPrice(@PathVariable UUID skuId, @RequestBody PriceHistory priceHistory) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new NoSuchElementException("SKU not found"));

        priceHistory.setSku(sku);
        return priceHistoryRepository.save(priceHistory);
    }

    @GetMapping("/{skuId}/latest")
    public PriceHistory getLatestPrice(@PathVariable UUID skuId) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new NoSuchElementException("SKU not found"));

        return priceHistoryRepository.findTopBySkuOrderByEffectiveDateDesc(sku)
                .orElseThrow(() -> new NoSuchElementException("No price history for this SKU"));
    }

    @GetMapping("/{skuId}")
    public List<PriceHistory> getPriceHistory(@PathVariable UUID skuId) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new NoSuchElementException("SKU not found"));

        return priceHistoryRepository.findBySkuOrderByEffectiveDateDesc(sku);
    }
}
