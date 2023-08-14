package com.bullish.store.facade.admin.adjustment.discount;

import com.bullish.store.domain.adjustment.usecase.DiscountAmountEntity;
import com.bullish.store.domain.adjustment.usecase.DiscountAmountRepository;
import com.bullish.store.domain.adjustment.usecase.DiscountRatioEntity;
import com.bullish.store.domain.adjustment.usecase.DiscountRatioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DiscountControllerComponentTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiscountRatioRepository discountRatioRepository;

    @Autowired
    private DiscountAmountRepository discountAmountRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        discountRatioRepository.deleteAll();
        discountAmountRepository.deleteAll();
    }

    @Test
    void given_noDiscount_when_addAllProductRatioDiscount_then_return201AndDiscountIdAndStoreInDb() throws Exception {
        // Given
        assertThat(discountRatioRepository.findAll()).isEmpty();

        // When
        var result = mockMvc.perform(post("/admin/adjustment/discount/ratio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "discountName": "All Item 2nd 30% off",
                            "isApplyToAllProduct": true,
                            "offRatio": 0.3,
                            "applyAtEveryNthNumberOfIdenticalItem": 2
                        }
                        """
                )
            )

            // Then
            .andExpect(status().isCreated())
            .andReturn();

        List<DiscountRatioEntity> actualDiscountRatioList = discountRatioRepository.findAll();
        assertAll(
            () -> assertThat(actualDiscountRatioList.size()).isEqualTo(1),
            () -> assertThat(actualDiscountRatioList.get(0).getId()).isNotNull(),
            () -> assertThat(result.getResponse().getContentAsString()).isEqualTo(
                actualDiscountRatioList.get(0).getId().toString()),
            () -> assertThat(actualDiscountRatioList.get(0).getName()).isEqualTo("All Item 2nd 30% off"),
            () -> assertThat(actualDiscountRatioList.get(0).isApplyToAllProduct()).isTrue(),
            () -> assertThat(actualDiscountRatioList.get(0).getShelfGoodId()).isNullOrEmpty(),
            () -> assertThat(actualDiscountRatioList.get(0).getOffRatio()).isEqualTo(0.3),
            () -> assertThat(actualDiscountRatioList.get(0).getApplyAtEveryNthNumberOfIdenticalItem()).isEqualTo(2)
        );
    }

    @Test
    void given_noDiscount_when_addSpecifiedProductRatioDiscount_then_return201AndDiscountIdAndStoreInDb()
        throws Exception {
        // Given
        assertThat(discountRatioRepository.findAll()).isEmpty();

        // When
        var result = mockMvc.perform(post("/admin/adjustment/discount/ratio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "discountName": "Specific Item 2nd 30% off",
                            "isApplyToAllProduct": false,
                            "shelfGoodId": "860214ce-e83b-4315-a44c-574c59708291",
                            "offRatio": 0.3,
                            "applyAtEveryNthNumberOfIdenticalItem": 2
                        }
                        """
                )
            )

            // Then
            .andExpect(status().isCreated())
            .andReturn();

        List<DiscountRatioEntity> actualDiscountRatioList = discountRatioRepository.findAll();
        assertAll(
            () -> assertThat(actualDiscountRatioList.size()).isEqualTo(1),
            () -> assertThat(actualDiscountRatioList.get(0).getId()).isNotNull(),
            () -> assertThat(result.getResponse().getContentAsString()).isEqualTo(
                actualDiscountRatioList.get(0).getId().toString()),
            () -> assertThat(actualDiscountRatioList.get(0).getName()).isEqualTo("Specific Item 2nd 30% off"),
            () -> assertThat(actualDiscountRatioList.get(0).isApplyToAllProduct()).isFalse(),
            () -> assertThat(actualDiscountRatioList.get(0).getShelfGoodId())
                .isEqualTo("860214ce-e83b-4315-a44c-574c59708291"),
            () -> assertThat(actualDiscountRatioList.get(0).getOffRatio()).isEqualTo(0.3),
            () -> assertThat(actualDiscountRatioList.get(0).getApplyAtEveryNthNumberOfIdenticalItem()).isEqualTo(2)
        );
    }

    @Test
    void given_invalidRatioDiscountSetting_when_addRatioDiscount_then_return400()
        throws Exception {
        // Given
        assertThat(discountRatioRepository.findAll()).isEmpty();

        // When
        var result = mockMvc.perform(post("/admin/adjustment/discount/ratio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "discountName": "Specific Item 2nd 30% off",
                            "isApplyToAllProduct": true,
                            "shelfGoodId": "860214ce-e83b-4315-a44c-574c59708291",
                            "offRatio": 0.3,
                            "applyAtEveryNthNumberOfIdenticalItem": 2
                        }
                        """
                )
            )

            // Then
            .andExpect(status().isBadRequest())
            .andReturn();

        assertAll(
            () -> assertThat(discountRatioRepository.findAll()).isEmpty(),
            () -> assertThat(result.getResponse().getContentAsString())
                .contains("Discount with specified shelfGoodId cannot be applied to all product")
        );
    }

    @Test
    void given_noDiscount_when_addSpecifiedProductAmountDiscount_then_return201AndDiscountIdAndStoreInDb()
        throws Exception {
        // Given
        assertThat(discountAmountRepository.findAll()).isEmpty();

        // When
        var result = mockMvc.perform(post("/admin/adjustment/discount/amount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "discountName": "Specific Item 2nd 30% off",
                            "isApplyToAllProduct": false,
                            "shelfGoodId": "860214ce-e83b-4315-a44c-574c59708291",
                            "currency": "HKD",
                            "discountAmount": 33.3,
                            "applyAtEveryNthNumberOfIdenticalItem": 2
                        }
                        """
                )
            )

            // Then
            .andExpect(status().isCreated())
            .andReturn();

        List<DiscountAmountEntity> actualDiscountAmountList = discountAmountRepository.findAll();
        assertAll(
            () -> assertThat(actualDiscountAmountList.size()).isEqualTo(1),
            () -> assertThat(actualDiscountAmountList.get(0).getId()).isNotNull(),
            () -> assertThat(result.getResponse().getContentAsString()).isEqualTo(
                actualDiscountAmountList.get(0).getId().toString()),
            () -> assertThat(actualDiscountAmountList.get(0).getName()).isEqualTo("Specific Item 2nd 30% off"),
            () -> assertThat(actualDiscountAmountList.get(0).isApplyToAllProduct()).isFalse(),
            () -> assertThat(actualDiscountAmountList.get(0).getShelfGoodId())
                .isEqualTo("860214ce-e83b-4315-a44c-574c59708291"),
            () -> assertThat(actualDiscountAmountList.get(0).getCurrency()).isEqualTo("HKD"),
            () -> assertThat(actualDiscountAmountList.get(0).getDiscountAmount().compareTo(BigDecimal.valueOf(33.3)))
                .isEqualTo(0),
            () -> assertThat(actualDiscountAmountList.get(0).getApplyAtEveryNthNumberOfIdenticalItem()).isEqualTo(2)
        );
    }


    @Test
    void given_inputNegativeDiscountValue_when_addSpecifiedProductAmountDiscount_then_return201AndDiscountIdAndStorePositiveValueInDb()
        throws Exception {
        // Given
        assertThat(discountAmountRepository.findAll()).isEmpty();

        // When
        var result = mockMvc.perform(post("/admin/adjustment/discount/amount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "discountName": "Specific Item 2nd 30% off",
                            "isApplyToAllProduct": false,
                            "shelfGoodId": "860214ce-e83b-4315-a44c-574c59708291",
                            "currency": "HKD",
                            "discountAmount": -33.3,
                            "applyAtEveryNthNumberOfIdenticalItem": 2
                        }
                        """
                )
            )

            // Then
            .andExpect(status().isCreated())
            .andReturn();

        List<DiscountAmountEntity> actualDiscountAmountList = discountAmountRepository.findAll();
        assertAll(
            () -> assertThat(actualDiscountAmountList.size()).isEqualTo(1),
            () -> assertThat(actualDiscountAmountList.get(0).getId()).isNotNull(),
            () -> assertThat(result.getResponse().getContentAsString()).isEqualTo(
                actualDiscountAmountList.get(0).getId().toString()),
            () -> assertThat(actualDiscountAmountList.get(0).getName()).isEqualTo("Specific Item 2nd 30% off"),
            () -> assertThat(actualDiscountAmountList.get(0).isApplyToAllProduct()).isFalse(),
            () -> assertThat(actualDiscountAmountList.get(0).getShelfGoodId())
                .isEqualTo("860214ce-e83b-4315-a44c-574c59708291"),
            () -> assertThat(actualDiscountAmountList.get(0).getCurrency()).isEqualTo("HKD"),
            () -> assertThat(actualDiscountAmountList.get(0).getDiscountAmount().compareTo(BigDecimal.valueOf(33.3)))
                .isEqualTo(0),
            () -> assertThat(actualDiscountAmountList.get(0).getApplyAtEveryNthNumberOfIdenticalItem()).isEqualTo(2)
        );
    }

    @Test
    void given_invalidAmountDiscountSetting_when_addAmountDiscount_then_return400()
        throws Exception {
        // Given
        assertThat(discountAmountRepository.findAll()).isEmpty();

        // When
        var result = mockMvc.perform(post("/admin/adjustment/discount/amount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "discountName": "Specific Item 2nd 30% off",
                            "isApplyToAllProduct": true,
                            "shelfGoodId": "860214ce-e83b-4315-a44c-574c59708291",
                            "currency": "HKD",
                            "discountAmount": 33.3,
                            "applyAtEveryNthNumberOfIdenticalItem": 2
                        }
                        """
                )
            )

            // Then
            .andExpect(status().isBadRequest())
            .andReturn();

        assertAll(
            () -> assertThat(discountAmountRepository.findAll()).isEmpty(),
            () -> assertThat(result.getResponse().getContentAsString())
                .contains("Discount with specified shelfGoodId cannot be applied to all product")
        );
    }
}