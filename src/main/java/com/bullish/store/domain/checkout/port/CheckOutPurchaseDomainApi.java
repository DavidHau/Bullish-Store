package com.bullish.store.domain.checkout.port;

import com.bullish.store.domain.purchase.api.BasketDto;
import com.bullish.store.domain.purchase.api.BasketManagement;
import org.springframework.stereotype.Service;

@Service
public class CheckOutPurchaseDomainApi {
    private final BasketManagement basketManagement;

    public CheckOutPurchaseDomainApi(
        BasketManagement basketManagement
    ) {
        this.basketManagement = basketManagement;
    }


    public BasketDto getBasket(String customerId) {
        return basketManagement.getBasket(customerId)
            .orElseThrow();
    }

}
