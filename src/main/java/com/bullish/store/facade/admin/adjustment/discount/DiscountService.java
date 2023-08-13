package com.bullish.store.facade.admin.adjustment.discount;

import com.bullish.store.domain.adjustment.api.DiscountManagement;
import org.javamoney.moneta.Money;
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

    //TODO to mapper
    public String create(DiscountController.CreateAmountDiscountRequest amountDiscountRequest) {
        DiscountManagement.CreateAmountDiscountRequest createAmountDiscountRequest =
            new DiscountManagement.CreateAmountDiscountRequest(
                amountDiscountRequest.discountName(),
                amountDiscountRequest.isApplyToAllProduct(),
                amountDiscountRequest.shelfGoodId(),
                Money.of(amountDiscountRequest.discountAmount(), amountDiscountRequest.currency()),
                amountDiscountRequest.applyAtEveryNthNumberOfItem()
            );
        return discountManagement.addAmountDiscount(createAmountDiscountRequest);
    }
}
