package com.bullish.store.domain.adjustment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DiscountAmountRepositoryIntTest {

    @Autowired
    DiscountAmountRepository discountAmountRepository;

    @BeforeEach
    void setup() {
        discountAmountRepository.deleteAll();
    }

    @Test
    void given_specifiedApplyAtEveryNthNumberOfItem_when_saveDiscountAmount_then_storeSpecifiedValue() {
        // Given
        final int applyAtEveryNthNumberOfItem = 2;
        final String shelfGoodId = UUID.randomUUID().toString();
        DiscountAmountEntity discountAmount = DiscountAmountEntity.builder()
            .name("My Discount")
            .applyToAllProduct(true)
            .shelfGoodId(shelfGoodId)
            .currency("HKD")
            .discountAmount(BigDecimal.valueOf(20.2))
            .applyAtEveryNthNumberOfItem(applyAtEveryNthNumberOfItem)
            .build();

        // When
        discountAmountRepository.save(discountAmount);
        DiscountAmountEntity actualDiscountAmount = discountAmountRepository.findAll().get(0);

        // Then
        assertAll(
            () -> assertThat(actualDiscountAmount.getId()).isNotNull(),
            () -> assertThat(actualDiscountAmount.getName()).isEqualTo("My Discount"),
            () -> assertThat(actualDiscountAmount.isApplyToAllProduct()).isTrue(),
            () -> assertThat(actualDiscountAmount.getShelfGoodId()).isEqualTo(shelfGoodId),
            () -> assertThat(actualDiscountAmount.getCurrency()).isEqualTo("HKD"),
            () -> assertThat(actualDiscountAmount.getDiscountAmount().compareTo(BigDecimal.valueOf(20.2)))
                .isEqualTo(0),
            () -> assertThat(actualDiscountAmount.getApplyAtEveryNthNumberOfItem())
                .isEqualTo(applyAtEveryNthNumberOfItem)
        );
    }

}