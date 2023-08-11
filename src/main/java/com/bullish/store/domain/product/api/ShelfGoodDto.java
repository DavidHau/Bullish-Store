package com.bullish.store.domain.product.api;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ShelfGoodDto {
    private String id;
    private ProductDto product;
    private String currency;
    private BigDecimal basePrice;
}
