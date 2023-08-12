package com.bullish.store.domain.checkout.api;

import lombok.*;
import org.javamoney.moneta.Money;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ReceiptDto {
    private List<LineItem> lineItemList;
    private Money totalPrice;

    public record LineItem(
        int lineItemId,
        String shelfId,
        String productId,
        String name,
        Money basePrice
    ) {
    }
}
