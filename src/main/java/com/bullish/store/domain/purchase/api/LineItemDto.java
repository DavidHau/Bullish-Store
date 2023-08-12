package com.bullish.store.domain.purchase.api;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class LineItemDto {
    private String lineItemId;
    private String shelfId;
    private String productId;
}
