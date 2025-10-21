package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Sku;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.SkuRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final SkuRepository skuRepository;
    private final PriceHistoryService priceHistoryService;

    public ExpenseService(ExpenseRepository expenseRepository,
                          SkuRepository skuRepository,
                          PriceHistoryService priceHistoryService) {
        this.expenseRepository = expenseRepository;
        this.skuRepository = skuRepository;
        this.priceHistoryService = priceHistoryService;
    }

    /** Add a new expense */
    public Expense addExpense(UUID skuId, BigDecimal quantity, String notes) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("SKU not found"));

        BigDecimal unitPrice = priceHistoryService.getLatestPrice(sku);
        BigDecimal totalPrice = unitPrice.multiply(quantity);

        Expense expense = Expense.builder()
                .sku(sku)
                .quantity(quantity)
                .unit(sku.getUnit())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .notes(notes)
                .build();

        return expenseRepository.save(expense);
    }

    /** Fetch all expenses */
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    /** Fetch expenses for a SKU within a date range */
    public List<Expense> getExpensesForSku(UUID skuId, LocalDateTime from, LocalDateTime to) {
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("SKU not found"));
        return expenseRepository.findBySkuAndDateTimeBetween(sku, from, to);
    }

    /** Fetch expenses between dates (all SKUs) */
    public List<Expense> getExpensesBetween(LocalDateTime from, LocalDateTime to) {
        return expenseRepository.findByDateTimeBetween(from, to);
    }
}
