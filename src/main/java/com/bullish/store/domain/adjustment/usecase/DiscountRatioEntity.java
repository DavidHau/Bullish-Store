package com.bullish.store.domain.adjustment.usecase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "discount_ratio")
public class DiscountRatioEntity extends DiscountEntity {

    @Column(nullable = false)
    private double offRatio;

}
