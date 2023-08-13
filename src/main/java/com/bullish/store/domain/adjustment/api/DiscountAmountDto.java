package com.bullish.store.domain.adjustment.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
public class DiscountAmountDto extends DiscountDto {
    private String currency;
    private BigDecimal discountAmount;
}
