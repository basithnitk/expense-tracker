package com.expensetracker.controller;

import com.expensetracker.model.PriceHistory;
import com.expensetracker.model.Sku;
import com.expensetracker.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/price-history")
@RequiredArgsConstructor
public class PriceHistoryController {

    private final PriceHistoryRepository priceHistoryRepository;

    @GetMapping
    public List<PriceHistory> getAll() {
        return priceHistoryRepository.findAll();
    }

    @GetMapping("/sku/{skuId}")
    public List<PriceHistory> getBySku(@PathVariable UUID skuId) {
        Sku sku = new Sku();
        sku.setId(skuId);
        return priceHistoryRepository.findBySkuOrderByEffectiveDateDesc(sku);
    }
}