package com.bullish.store.facade.admin.adjustment.discount;

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

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        discountRatioRepository.deleteAll();
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
                            "applyAtEveryNthNumberOfItem": 2
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
            () -> assertThat(actualDiscountRatioList.get(0).getApplyAtEveryNthNumberOfItem()).isEqualTo(2)
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
                            "applyAtEveryNthNumberOfItem": 2
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
            () -> assertThat(actualDiscountRatioList.get(0).getApplyAtEveryNthNumberOfItem()).isEqualTo(2)
        );
    }
}