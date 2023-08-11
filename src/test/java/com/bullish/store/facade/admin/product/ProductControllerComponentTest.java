package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.usecase.ProductEntity;
import com.bullish.store.domain.product.usecase.ProductRepository;
import com.bullish.store.domain.product.usecase.ShelfGoodEntity;
import com.bullish.store.domain.product.usecase.ShelfRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private ShelfRepository shelfRepository;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        shelfRepository.deleteAll();
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

    @Test
    void given_productExist_when_launchProduct_then_storeShelfGoodInShelf() throws Exception {
        // Given
        ProductEntity product = productRepository.save(ProductEntity.builder()
            .name("Galaxy S")
            .name("Samsung mobile")
            .build());
        final UUID productId = product.getId();

        // when
        var result = mockMvc.perform(post("/admin/product/{product-id}/launch", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                        {
                            "currency": "HKD",
                            "basePrice": 5000.5
                        }
                        """
                )
            )

            // Then
            .andExpect(status().isCreated())
            .andReturn();

        Optional<ShelfGoodEntity> actualShelf = shelfRepository.findByProductId(productId);
        assertAll(
            () -> assertThat(actualShelf).isPresent(),
            () -> {
                ShelfGoodEntity actualShelfGoodEntity = actualShelf.get();
                Money actualBasePrice = Money.of(
                    actualShelfGoodEntity.getBasePrice(),
                    actualShelfGoodEntity.getCurrency()
                );
                assertAll(
                    () -> assertThat(result.getResponse().getContentAsString()).isEqualTo(
                        actualShelfGoodEntity.getId().toString()),
                    () -> assertThat(actualShelfGoodEntity.getProductId()).isEqualTo(product.getId()),
                    () -> assertThat(actualBasePrice).isEqualTo(Money.of(5000.5, "HKD"))
                );
            }
        );
    }
}