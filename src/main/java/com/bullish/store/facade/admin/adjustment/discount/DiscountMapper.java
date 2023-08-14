package com.bullish.store.facade.admin.adjustment.discount;

import com.bullish.store.domain.adjustment.api.DiscountManagement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    DiscountManagement.CreateRatioDiscount createRatioDiscountRequestToCreateRatioDiscount(
        DiscountController.CreateRatioDiscountRequest request);

    default DiscountManagement.CreateAmountDiscount createAmountDiscountRequestToCreateAmountDiscount(
        DiscountController.CreateAmountDiscountRequest request) {
        return new DiscountManagement.CreateAmountDiscount(
            request.discountName(),
            request.isApplyToAllProduct(),
            request.shelfGoodId(),
            request.currency(),
            request.discountAmount(),
            request.applyAtEveryNthNumberOfIdenticalItem()
        );
    }

}
