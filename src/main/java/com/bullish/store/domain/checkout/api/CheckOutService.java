package com.bullish.store.domain.checkout.api;

import java.util.Optional;

public interface CheckOutService {
    Optional<ReceiptDto> getReceipt(String customerId);

}
