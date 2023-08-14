package com.bullish.store.domain.adjustment.usecase;


import com.bullish.store.domain.adjustment.api.AdjustmentMapper;
import com.bullish.store.domain.adjustment.api.DiscountAmountDto;
import com.bullish.store.domain.adjustment.api.DiscountManagement;
import com.bullish.store.domain.adjustment.api.DiscountRatioDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DiscountManagementImpl implements DiscountManagement {

    private final DiscountRatioRepository discountRatioRepository;
    private final DiscountAmountRepository discountAmountRepository;
    private final AdjustmentMapper adjustmentMapper = Mappers.getMapper(AdjustmentMapper.class);
    private final String SHELF_GOOD_ID_AND_APPLY_TO_ALL_PRODUCT_SETTING_COEXIST_ERROR_MESSAGE =
        "Discount with specified shelfGoodId cannot be applied to all product. (Hints: remove shelfGoodId param)";

    public DiscountManagementImpl(
        DiscountRatioRepository discountRatioRepository,
        DiscountAmountRepository discountAmountRepository
    ) {
        this.discountRatioRepository = discountRatioRepository;
        this.discountAmountRepository = discountAmountRepository;
    }

    @Override
    public String addRatioDiscount(CreateRatioDiscount request) {
        if (request.isApplyToAllProduct() && StringUtils.isNotEmpty(request.shelfGoodId())) {
            throw new IllegalArgumentException(SHELF_GOOD_ID_AND_APPLY_TO_ALL_PRODUCT_SETTING_COEXIST_ERROR_MESSAGE);
        }
        if (request.offRatio() <= 0 || request.offRatio() > 1) {
            throw new IllegalArgumentException("Discount ratio must be [0 < ratio <= 1].");
        }

        DiscountRatioEntity discountEntity = discountRatioRepository.save(DiscountRatioEntity.builder()
            .name(request.discountName())
            .shelfGoodId(request.shelfGoodId())
            .applyToAllProduct(request.isApplyToAllProduct())
            .offRatio(request.offRatio())
            .applyAtEveryNthNumberOfIdenticalItem(request.applyAtEveryNthNumberOfIdenticalItem())
            .build());
        return discountEntity.getId().toString();
    }

    @Override
    public String addAmountDiscount(CreateAmountDiscount request) {
        if (request.isApplyToAllProduct() && StringUtils.isNotEmpty(request.shelfGoodId())) {
            throw new IllegalArgumentException(SHELF_GOOD_ID_AND_APPLY_TO_ALL_PRODUCT_SETTING_COEXIST_ERROR_MESSAGE);
        }

        DiscountAmountEntity discountEntity = discountAmountRepository.save(DiscountAmountEntity.builder()
            .name(request.discountName())
            .shelfGoodId(request.shelfGoodId())
            .applyToAllProduct(request.isApplyToAllProduct())
            .currency(request.discountAmount().getCurrency().getCurrencyCode())
            .discountAmount(request.discountAmount().abs().getNumberStripped())
            .applyAtEveryNthNumberOfIdenticalItem(request.applyAtEveryNthNumberOfIdenticalItem())
            .build());
        return discountEntity.getId().toString();
    }

    @Override
    public List<DiscountRatioDto> getAllAutoApplyRatioDiscount() {
        return discountRatioRepository.findAll()
            .stream()
            .map(adjustmentMapper::discountRatioEntityToDto)
            .toList();
    }

    @Override
    public List<DiscountAmountDto> getAllAutoApplyAmountDiscount() {
        return discountAmountRepository.findAll()
            .stream()
            .map(adjustmentMapper::discountAmountEntityToDto)
            .toList();
    }

}
