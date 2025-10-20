package com.expensetracker.expense_tracker.sku.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "skus")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sku {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String level1;
    private String level2;
    private String level3;
    private String level4;
    private String level5;
    private String unit;

    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
