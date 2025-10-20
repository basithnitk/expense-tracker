package com.expensetracker.sku.controller;

import com.expensetracker.sku.model.Sku;
import com.expensetracker.sku.repository.SkuRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skus")
@CrossOrigin
public class SkuController {

    private final SkuRepository repository;

    public SkuController(SkuRepository repository) {
        this.repository = repository;
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
    public Sku create(@RequestBody Sku sku) {
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
}
