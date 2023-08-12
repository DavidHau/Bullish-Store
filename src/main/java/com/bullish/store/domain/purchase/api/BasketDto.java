package com.bullish.store.domain.purchase.api;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BasketDto {
    String id;
    String customerId;
    List<LineItemDto> lineItemList;
}
