package com.bullish.store.domain.adjustment.usecase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "discount_amount")
public class DiscountAmountEntity extends DiscountEntity {

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal discountAmount;
}
