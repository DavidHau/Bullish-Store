package com.bullish.store.domain.adjustment.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
public class DiscountRatioDto extends DiscountDto {
    private double offRatio;
}
