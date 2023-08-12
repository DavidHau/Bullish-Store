package com.bullish.store.domain.checkout.api;

public interface CheckOutService {
    ReceiptDto getReceipt(String customerId);

}
