package com.bullish.store.domain.adjustment.api;

import com.bullish.store.domain.adjustment.usecase.DiscountAmountEntity;
import com.bullish.store.domain.adjustment.usecase.DiscountRatioEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AdjustmentMapperTest {

    private final AdjustmentMapper adjustmentMapper = Mappers.getMapper(AdjustmentMapper.class);

    @Test
    void given_allValue_when_discountRatioEntityToDto_then_mapAllValue() {
        // Given
        UUID uuid = UUID.randomUUID();
        DiscountRatioEntity entity = DiscountRatioEntity.builder()
            .id(uuid)
            .name("NAME")
            .shelfGoodId("SHELF_GOOD_ID")
            .applyToAllProduct(true)
            .applyAtEveryNthNumberOfItem(10)
            .offRatio(123.4)
            .build();

        // When
        DiscountRatioDto actualRatioDiscountDto = adjustmentMapper.discountRatioEntityToDto(entity);

        // Then
        assertAll(
            () -> assertThat(actualRatioDiscountDto.getId()).isEqualTo(uuid.toString()),
            () -> assertThat(actualRatioDiscountDto.getName()).isEqualTo("NAME"),
            () -> assertThat(actualRatioDiscountDto.getShelfGoodId()).isEqualTo("SHELF_GOOD_ID"),
            () -> assertThat(actualRatioDiscountDto.isApplyToAllProduct()).isEqualTo(true),
            () -> assertThat(actualRatioDiscountDto.getApplyAtEveryNthNumberOfItem()).isEqualTo(10),
            () -> assertThat(actualRatioDiscountDto.getOffRatio()).isEqualTo(123.4)
        );
    }

    @Test
    void given_allValue_when_discountAmountEntityToDto_then_mapAllValue() {
        // Given
        UUID uuid = UUID.randomUUID();
        DiscountAmountEntity entity = DiscountAmountEntity.builder()
            .id(uuid)
            .name("NAME")
            .shelfGoodId("SHELF_GOOD_ID")
            .applyToAllProduct(true)
            .applyAtEveryNthNumberOfItem(10)
            .currency("HKD")
            .discountAmount(BigDecimal.valueOf(123.4))
            .build();

        // When
        DiscountAmountDto actualAmountDiscount = adjustmentMapper.discountAmountEntityToDto(entity);

        // Then
        assertAll(
            () -> assertThat(actualAmountDiscount.getId()).isEqualTo(uuid.toString()),
            () -> assertThat(actualAmountDiscount.getName()).isEqualTo("NAME"),
            () -> assertThat(actualAmountDiscount.getShelfGoodId()).isEqualTo("SHELF_GOOD_ID"),
            () -> assertThat(actualAmountDiscount.isApplyToAllProduct()).isEqualTo(true),
            () -> assertThat(actualAmountDiscount.getApplyAtEveryNthNumberOfItem()).isEqualTo(10),
            () -> assertThat(actualAmountDiscount.getCurrency()).isEqualTo("HKD"),
            () -> assertThat(actualAmountDiscount.getDiscountAmount()).isEqualTo(BigDecimal.valueOf(123.4))
        );
    }
}