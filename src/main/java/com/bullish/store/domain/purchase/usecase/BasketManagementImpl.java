package com.bullish.store.domain.purchase.usecase;

import com.bullish.store.domain.purchase.api.BasketManagement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BasketManagementImpl implements BasketManagement {
    @Override
    public String addShelfGoodToBasket(String customerId, String shelfGoodId, String productId) {

        // TODO: check exist then add to basket
        return null;
    }

    // TODO: after removing all items, auto delete basket
}
