package com.expensetracker.controller;

import org.springframework.web.bind.annotation.*;

import com.expensetracker.model.PriceHistory;
import com.expensetracker.model.Sku;
import com.expensetracker.repository.SkuRepository;
import com.expensetracker.service.PriceHistoryService;
import com.expensetracker.service.SkuService;
import com.expensetracker.repository.PriceHistoryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/skus")
@CrossOrigin
public class SkuController {

    private final SkuRepository repository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final SkuService skuService;
    private final PriceHistoryService priceHistoryService;

    public SkuController(SkuRepository repository, PriceHistoryRepository priceHistoryRepository, SkuService skuService,
            PriceHistoryService priceHistoryService) {
        this.repository = repository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.skuService = skuService;
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping
    public List<Sku> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Sku getById(@PathVariable UUID id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public Sku createSku(@RequestBody Sku sku) {
        // Check if SKU with same taxonomy exists
        Optional<Sku> existing = repository.findByL1AndL2AndL3AndL4AndL5(
                sku.getL1(), sku.getL2(), sku.getL3(), sku.getL4(), sku.getL5());

        // If exists, return it
        if (existing.isPresent()) {
            return existing.get();
        }

        // Otherwise, create new SKU
        return repository.save(sku);
    }

    @PutMapping("/{id}")
    public Sku update(@PathVariable UUID id, @RequestBody Sku sku) {
        Sku existing = repository.findById(id).orElseThrow();
        sku.setId(existing.getId());
        return repository.save(sku);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        repository.deleteById(id);
    }

    @GetMapping("/skus/{id}/price-insights")
    public Map<String, Object> getPriceInsights(@PathVariable UUID id) {
        Sku sku = repository.findById(id).orElseThrow();
        List<PriceHistory> history = priceHistoryRepository.findBySkuOrderByEffectiveDateDesc(sku);

        Map<String, Object> result = new HashMap<>();
        result.put("latestPrice", history.isEmpty() ? BigDecimal.ZERO : history.get(0).getPricePerUnit());
        result.put("averagePrice", history.stream()
                .map(PriceHistory::getPricePerUnit)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(history.size()), RoundingMode.HALF_UP));
        result.put("minPrice",
                history.stream().map(PriceHistory::getPricePerUnit).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        result.put("maxPrice",
                history.stream().map(PriceHistory::getPricePerUnit).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        result.put("history", history);

        return result;
    }

    /**
     * Create SKU if taxonomy not exists or return existing
     * Optionally record price in same request
     */
    @PostMapping
    public Sku createOrFetchSku(@RequestBody Sku sku,
            @RequestParam(required = false) BigDecimal price) {
        Sku savedSku = skuService.createOrReturnSku(sku);

        if (price != null) {
            priceHistoryService.recordPriceChange(savedSku, price);
        }

        return savedSku;
    }

    /** Search SKUs by levels (any combination) or name */
    @GetMapping("/search")
    public List<Sku> searchSkus(
            @RequestParam(required = false) String l1,
            @RequestParam(required = false) String l2,
            @RequestParam(required = false) String l3,
            @RequestParam(required = false) String l4,
            @RequestParam(required = false) String l5,
            @RequestParam(required = false) String name) {
        return skuService.searchSkus(l1, l2, l3, l4, l5, name);
    }

    /** Get latest price of a SKU */
    @GetMapping("/{skuId}/latest-price")
    public BigDecimal getLatestPrice(@PathVariable UUID skuId) {
        Optional<Sku> skuOpt = skuService.getSkuById(skuId);
        if (skuOpt.isEmpty())
            throw new RuntimeException("SKU not found");
        return priceHistoryService.getLatestPrice(skuOpt.get());
    }

    /** Get full price history for a SKU */
    @GetMapping("/{skuId}/price-history")
    public List<PriceHistory> getPriceHistory(@PathVariable UUID skuId) {
        Optional<Sku> skuOpt = skuService.getSkuById(skuId);
        if (skuOpt.isEmpty())
            throw new RuntimeException("SKU not found");
        return priceHistoryService.getPriceHistory(skuOpt.get());
    }

}
