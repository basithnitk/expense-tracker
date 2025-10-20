package com.expensetracker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "skus",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"l1", "l2", "l3", "l4", "l5"})
    }
)
public class Sku {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name; // just the leaf name, can repeat under different taxonomy

    private String l1;
    private String l2;
    private String l3;
    private String l4;
    private String l5;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        validateLevels();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
        validateLevels();
    }

    private void validateLevels() {
        if (l1 == null || l1.isBlank()) {
            throw new IllegalArgumentException("At least l1 must be defined for SKU hierarchy");
        }
    }
}
