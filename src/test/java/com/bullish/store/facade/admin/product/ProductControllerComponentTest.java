package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.usecase.ProductEntity;
import com.bullish.store.domain.product.usecase.ProductRepository;
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
class ProductControllerComponentTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
    }

    @Test
    void given_noProduct_when_createProduct_then_return201AndProductIdAndStoreInDb() throws Exception {
        // Given
        assertThat(productRepository.findAll()).isEmpty();

        // When
        var result = mockMvc.perform(post("/admin/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "productName": "iPhone 12",
                            "description": "Apple's mobile product"
                        }
                        """
                )
            )

            // Then
            .andExpect(status().isCreated())
            .andReturn();

        List<ProductEntity> actualProductList = productRepository.findAll();
        assertAll(
            () -> assertThat(actualProductList.size()).isEqualTo(1),
            () -> assertThat(actualProductList.get(0).getId()).isNotNull(),
            () -> assertThat(result.getResponse().getContentAsString()).isEqualTo(
                actualProductList.get(0).getId().toString()),
            () -> assertThat(actualProductList.get(0).getName()).isEqualTo("iPhone 12"),
            () -> assertThat(actualProductList.get(0).getDescription()).isEqualTo("Apple's mobile product")
        );

    }

}