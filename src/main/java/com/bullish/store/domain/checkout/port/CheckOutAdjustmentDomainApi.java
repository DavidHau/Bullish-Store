package com.bullish.store.domain.checkout.port;

import com.bullish.store.domain.adjustment.api.DiscountManagement;
import com.bullish.store.domain.adjustment.api.DiscountRatioDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckOutAdjustmentDomainApi {
    private final DiscountManagement discountManagement;

    public CheckOutAdjustmentDomainApi(
        DiscountManagement discountManagement
    ) {
        this.discountManagement = discountManagement;
    }

    public List<DiscountRatioDto> getAllAutoApplyRatioDiscount() {
        return discountManagement.getAllAutoApplyRatioDiscount();
    }
}
