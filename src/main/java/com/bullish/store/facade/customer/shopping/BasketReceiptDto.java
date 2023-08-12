package com.bullish.store.facade.customer.shopping;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class BasketReceiptDto {
    private String customerId;
    private String basketId;
    private String currency;
    private List<LineItem> lineItemList;
    private BigDecimal totalBasePrice;
    private BigDecimal totalDiscount;
    private BigDecimal totalPrice;

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
        private BigDecimal basePrice;
        private String discountName;
        private BigDecimal discountedAmount;
    }
}
