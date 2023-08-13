package com.bullish.store.domain.adjustment.api;

import com.bullish.store.domain.adjustment.usecase.DiscountAmountEntity;
import com.bullish.store.domain.adjustment.usecase.DiscountRatioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdjustmentMapper {

    DiscountRatioDto discountRatioEntityToDto(DiscountRatioEntity entity);

    DiscountAmountDto discountAmountEntityToDto(DiscountAmountEntity entity);

}
