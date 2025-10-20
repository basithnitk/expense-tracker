package com.expensetracker.controller;

import com.expensetracker.model.PriceHistory;
import com.expensetracker.model.Transaction;
import com.expensetracker.repository.PriceHistoryRepository;
import com.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // @PostMapping
    // public Transaction addTransaction(@RequestBody Transaction transaction) {
    // return transactionRepository.save(transaction);
    // }

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        // Save transaction first
        Transaction saved = transactionRepository.save(transaction);

        // Fetch latest price history for this SKU
        List<PriceHistory> histories = priceHistoryRepository.findBySkuOrderByEffectiveDateDesc(transaction.getSku());
        boolean priceChanged = histories.isEmpty() ||
                histories.get(0).getPricePerUnit().compareTo(transaction.getPricePerUnit()) != 0;

        // If price is new, add to price history
        if (priceChanged) {
            PriceHistory ph = PriceHistory.builder()
                    .sku(transaction.getSku())
                    .pricePerUnit(transaction.getPricePerUnit())
                    .build();
            priceHistoryRepository.save(ph);
        }

        return saved;
    }

    @GetMapping("/summary/total")
    public BigDecimal getTotalExpenses() {
        return transactionRepository.findAll().stream()
                .map(Transaction::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @GetMapping("/summary/by-sku")
    public Map<String, BigDecimal> getExpensesBySku() {
        return transactionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        t -> t.getSku().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getTotalPrice, BigDecimal::add)));
    }

    @GetMapping("/summary")
    public BigDecimal getExpensesInRange(
            @RequestParam String start,
            @RequestParam String end) {
        LocalDateTime startDate = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end).atTime(23,59,59);
        
        return transactionRepository.findAll().stream()
                .filter(t -> !t.getTransactionDate().isBefore(startDate) && !t.getTransactionDate().isAfter(endDate))
                .map(Transaction::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
