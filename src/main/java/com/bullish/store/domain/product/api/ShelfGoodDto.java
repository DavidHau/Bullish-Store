package com.bullish.store.domain.product.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShelfGoodDto {
    private String shelfGoodId;
    private ProductDto product;
    private String currency;
    private BigDecimal basePrice;
}
