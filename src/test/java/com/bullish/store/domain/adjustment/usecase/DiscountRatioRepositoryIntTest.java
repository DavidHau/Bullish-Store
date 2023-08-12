package com.bullish.store.domain.adjustment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DiscountRatioRepositoryIntTest {

    @Autowired
    DiscountRatioRepository discountRatioRepository;

    private final int DEFAULT_APPLY_AT_EVERY_NTH_NUMBER_OF_ITEM = 1;

    @BeforeEach
    void setup() {
        discountRatioRepository.deleteAll();
    }

    @Test
    void given_specifiedApplyAtEveryNthNumberOfItem_when_saveDiscountRatio_then_storeSpecifiedValue() {
        // Given
        final int applyAtEveryNthNumberOfItem = 2;
        final String shelfGoodId = UUID.randomUUID().toString();
        DiscountRatioEntity discountRatio = DiscountRatioEntity.builder()
            .name("My Discount")
            .isApplyToAllProduct(true)
            .shelfGoodId(shelfGoodId)
            .offRatio(0.3)
            .applyAtEveryNthNumberOfItem(applyAtEveryNthNumberOfItem)
            .build();

        // When
        discountRatioRepository.save(discountRatio);
        DiscountRatioEntity actualDiscountRatio = discountRatioRepository.findAll().get(0);

        // Then
        assertAll(
            () -> assertThat(actualDiscountRatio.getId()).isNotNull(),
            () -> assertThat(actualDiscountRatio.getName()).isEqualTo("My Discount"),
            () -> assertThat(actualDiscountRatio.isApplyToAllProduct()).isTrue(),
            () -> assertThat(actualDiscountRatio.getShelfGoodId()).isEqualTo(shelfGoodId),
            () -> assertThat(actualDiscountRatio.getOffRatio()).isEqualTo(0.3),
            () -> assertThat(actualDiscountRatio.getApplyAtEveryNthNumberOfItem()).isEqualTo(
                applyAtEveryNthNumberOfItem)
        );
    }

    @Test
    void given_notSpecifiedApplyAtEveryNthNumberOfItem_when_saveDiscountRatio_then_storeDefaultValue() {
        // Given
        final String shelfGoodId = UUID.randomUUID().toString();
        DiscountRatioEntity discountRatio = DiscountRatioEntity.builder()
            .name("My Discount")
            .isApplyToAllProduct(true)
            .shelfGoodId(shelfGoodId)
            .offRatio(0.3)
            .build();

        // When
        discountRatioRepository.save(discountRatio);
        DiscountRatioEntity actualDiscountRatio = discountRatioRepository.findAll().get(0);

        // Then
        assertAll(
            () -> assertThat(actualDiscountRatio.getId()).isNotNull(),
            () -> assertThat(actualDiscountRatio.getName()).isEqualTo("My Discount"),
            () -> assertThat(actualDiscountRatio.isApplyToAllProduct()).isTrue(),
            () -> assertThat(actualDiscountRatio.getShelfGoodId()).isEqualTo(shelfGoodId),
            () -> assertThat(actualDiscountRatio.getOffRatio()).isEqualTo(0.3),
            () -> assertThat(actualDiscountRatio.getApplyAtEveryNthNumberOfItem())
                .isEqualTo(DEFAULT_APPLY_AT_EVERY_NTH_NUMBER_OF_ITEM)
        );
    }
}