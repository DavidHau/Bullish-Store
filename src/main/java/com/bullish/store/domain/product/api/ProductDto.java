package com.bullish.store.domain.product.api;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ProductDto {
    private String productId;
    private String name;
    private String description;
}
