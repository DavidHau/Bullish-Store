package com.bullish.store.facade.admin.adjustment.discount;

import com.bullish.store.domain.adjustment.api.DiscountManagement;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    private final DiscountManagement discountManagement;

    public DiscountService(
        DiscountManagement discountManagement
    ) {
        this.discountManagement = discountManagement;
    }

    public String create(DiscountManagement.CreateRatioDiscountRequest ratioDiscountRequest) {
        return discountManagement.addRatioDiscount(ratioDiscountRequest);
    }

    public String create(DiscountController.CreateAmountDiscountRequest amountDiscountRequest) {
        return discountManagement.addAmountDiscount(new DiscountManagement.CreateAmountDiscountRequest(
            amountDiscountRequest.discountName(),
            amountDiscountRequest.isApplyToAllProduct(),
            amountDiscountRequest.shelfGoodId(),
            amountDiscountRequest.currency(),
            amountDiscountRequest.discountAmount(),
            amountDiscountRequest.applyAtEveryNthNumberOfIdenticalItem()
        ));
    }
}
