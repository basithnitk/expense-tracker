package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Sku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findBySku(Sku sku);

    List<Expense> findByDateTimeBetween(LocalDateTime from, LocalDateTime to);

    List<Expense> findBySkuAndDateTimeBetween(Sku sku, LocalDateTime from, LocalDateTime to);
}
