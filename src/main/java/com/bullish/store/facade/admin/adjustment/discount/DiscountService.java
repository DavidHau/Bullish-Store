package com.bullish.store.facade.admin.adjustment.discount;

import com.bullish.store.domain.adjustment.api.DiscountManagement;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    private final DiscountManagement discountManagement;
    private final DiscountMapper discountMapper = Mappers.getMapper(DiscountMapper.class);

    public DiscountService(
        DiscountManagement discountManagement
    ) {
        this.discountManagement = discountManagement;
    }

    public String create(DiscountController.CreateRatioDiscountRequest ratioDiscountRequest) {
        return discountManagement.addRatioDiscount(
            discountMapper.createRatioDiscountRequestToCreateRatioDiscount(ratioDiscountRequest)
        );
    }

    public String create(DiscountController.CreateAmountDiscountRequest amountDiscountRequest) {
        return discountManagement.addAmountDiscount(
            discountMapper.createAmountDiscountRequestToCreateAmountDiscount(amountDiscountRequest)
        );
    }
}
