package com.bullish.store.domain.adjustment.api;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class DiscountRatioDto {
    private String id;
    private String name;
    private String shelfGoodId;
    private boolean isApplyToAllProduct;
    private double offRatio;
    private int applyAtEveryNthNumberOfItem;
}
