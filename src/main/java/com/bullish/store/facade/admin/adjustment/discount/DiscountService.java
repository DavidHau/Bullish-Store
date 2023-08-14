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

    public String create(DiscountController.CreateAmountRatioRequest ratioDiscountRequest) {
        return discountManagement.addRatioDiscount(new DiscountManagement.CreateRatioDiscount(
            ratioDiscountRequest.discountName(),
            ratioDiscountRequest.isApplyToAllProduct(),
            ratioDiscountRequest.shelfGoodId(),
            ratioDiscountRequest.offRatio(),
            ratioDiscountRequest.applyAtEveryNthNumberOfIdenticalItem()
        ));
    }

    public String create(DiscountController.CreateAmountDiscountRequest amountDiscountRequest) {
        return discountManagement.addAmountDiscount(new DiscountManagement.CreateAmountDiscount(
            amountDiscountRequest.discountName(),
            amountDiscountRequest.isApplyToAllProduct(),
            amountDiscountRequest.shelfGoodId(),
            amountDiscountRequest.currency(),
            amountDiscountRequest.discountAmount(),
            amountDiscountRequest.applyAtEveryNthNumberOfIdenticalItem()
        ));
    }
}
