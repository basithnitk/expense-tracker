package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /** Add new expense */
    @PostMapping
    public Expense addExpense(@RequestParam UUID skuId,
                              @RequestParam BigDecimal quantity,
                              @RequestParam(required = false) String notes) {
        return expenseService.addExpense(skuId, quantity, notes);
    }

    /** Get all expenses */
    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    /** Get expenses for a SKU in a date range */
    @GetMapping("/sku/{skuId}")
    public List<Expense> getExpensesForSku(@PathVariable UUID skuId,
                                           @RequestParam LocalDateTime from,
                                           @RequestParam LocalDateTime to) {
        return expenseService.getExpensesForSku(skuId, from, to);
    }

    /** Get expenses between dates (all SKUs) */
    @GetMapping("/between")
    public List<Expense> getExpensesBetween(@RequestParam LocalDateTime from,
                                            @RequestParam LocalDateTime to) {
        return expenseService.getExpensesBetween(from, to);
    }
}
