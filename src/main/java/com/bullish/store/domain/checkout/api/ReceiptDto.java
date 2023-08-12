package com.bullish.store.domain.checkout.api;

import lombok.*;
import org.javamoney.moneta.Money;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ReceiptDto {
    private String customerId;
    private String basketId;
    private List<LineItem> lineItemList;
    private Money totalBasePrice;
    private Money totalDiscount;
    private Money totalPrice;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    public static class LineItem {
        private int lineItemId;
        private String shelfId;
        private String productId;
        private String name;
        private Money basePrice;
        private String discountName;
        private Money discountedAmount;
    }
}
