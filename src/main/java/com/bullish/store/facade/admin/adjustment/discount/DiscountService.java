package com.bullish.store.facade.admin.adjustment.discount;

import com.bullish.store.domain.adjustment.api.DiscountManagement;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    private DiscountManagement discountManagement;

    public DiscountService(
        DiscountManagement discountManagement
    ) {
        this.discountManagement = discountManagement;
    }

    public String create(DiscountManagement.CreateRatioDiscountRequest ratioDiscountRequest) {
        return discountManagement.addRatioDiscount(ratioDiscountRequest);
    }
}
