package com.bullish.store.domain.adjustment.usecase;


import com.bullish.store.domain.adjustment.api.DiscountManagement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiscountManagementImpl implements DiscountManagement {

    private DiscountRatioRepository discountRatioRepository;

    public DiscountManagementImpl(
        DiscountRatioRepository discountRatioRepository
    ) {
        this.discountRatioRepository = discountRatioRepository;
    }

    @Override
    public String addRatioDiscount(CreateRatioDiscountRequest request) {
        if (request.isApplyToAllProduct() && StringUtils.isNotEmpty(request.shelfGoodId())) {
            throw new IllegalArgumentException("Discount with specified shelfGoodId cannot be applied to all product.");
        }

        DiscountRatioEntity discountEntity = discountRatioRepository.save(DiscountRatioEntity.builder()
            .name(request.discountName())
            .shelfGoodId(request.shelfGoodId())
            .isApplyToAllProduct(request.isApplyToAllProduct())
            .offRatio(request.offRatio())
            .applyAtEveryNthNumberOfItem(request.applyAtEveryNthNumberOfItem())
            .build());
        return discountEntity.getId().toString();
    }


}
