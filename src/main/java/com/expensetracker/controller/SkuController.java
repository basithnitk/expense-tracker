package com.expensetracker.controller;

import org.springframework.web.bind.annotation.*;

import com.expensetracker.model.PriceHistory;
import com.expensetracker.model.Sku;
import com.expensetracker.repository.SkuRepository;
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

    public SkuController(SkuRepository repository, PriceHistoryRepository priceHistoryRepository) {
        this.repository = repository;
        this.priceHistoryRepository = priceHistoryRepository;
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

}
