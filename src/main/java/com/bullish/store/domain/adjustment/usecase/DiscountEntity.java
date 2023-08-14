package com.bullish.store.domain.adjustment.usecase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ALARM_TYPE")
@SuperBuilder(toBuilder = true)
public abstract class DiscountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String shelfGoodId;

    @Column(nullable = false)
    private boolean applyToAllProduct;

    @Builder.Default
    @Column(nullable = false)
    private int applyAtEveryNthNumberOfIdenticalItem = 1;

}
