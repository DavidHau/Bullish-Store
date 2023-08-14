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
public abstract class DiscountDto {
    private String id;
    private String name;
    private String shelfGoodId;
    private boolean applyToAllProduct;
    private int applyAtEveryNthNumberOfIdenticalItem;
}
