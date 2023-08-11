package com.bullish.store.facade.customer.shopping;

import com.bullish.store.domain.product.api.ProductDto;
import com.bullish.store.domain.product.api.ProductManagement;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.domain.product.usecase.ProductEntity;
import com.bullish.store.domain.product.usecase.ProductRepository;
import com.bullish.store.domain.product.usecase.ShelfRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ShoppingControllerComponentTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductManagement productManagement;

    @Autowired
    private ProductShelfService shelfService;

    @Autowired
    private ShelfRepository shelfRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    private final ProductEntity ON_SALE_PRODUCT_1 = ProductEntity.builder()
        .name("iPhone XR")
        .description("Apple 10th special edition")
        .build();
    private final ProductEntity ON_SALE_PRODUCT_2 = ProductEntity.builder()
        .name("iPhone 13 mini")
        .description("Apple 13th small version")
        .build();
    private final ProductEntity NOT_ON_SALE_PRODUCT = ProductEntity.builder()
        .name("Galaxy S")
        .description("Samsung mobile")
        .build();
    private final Money PRODUCT_PRICE_1 = Money.of(5000, "HKD");
    private final Money PRODUCT_PRICE_2 = Money.of(6999.9, "HKD");


    @BeforeEach
    void setup() {
        shelfRepository.deleteAll();
        productRepository.deleteAll();
        prepareDefaultProductList();
    }

    private void prepareDefaultProductList() {
        ON_SALE_PRODUCT_1.setId(productRepository.save(ON_SALE_PRODUCT_1).getId());
        ON_SALE_PRODUCT_2.setId(productRepository.save(ON_SALE_PRODUCT_2).getId());
        NOT_ON_SALE_PRODUCT.setId(productRepository.save(NOT_ON_SALE_PRODUCT).getId());

        shelfService.launch(ON_SALE_PRODUCT_1.getId().toString(), PRODUCT_PRICE_1);
        shelfService.launch(ON_SALE_PRODUCT_2.getId().toString(), PRODUCT_PRICE_2);
    }

    private ProductDto getDto(ProductEntity productEntity) {
        return new ProductDto(productEntity.getId().toString(),
            productEntity.getName(),
            productEntity.getDescription());
    }

    @Test
    void given_2OnSaleProducts_when_findAllShelfGoods_then_return2ShelfGoods() throws Exception {
        // Given
        assertThat(shelfRepository.findAll()).hasSize(2);
        ShelfGoodDto expectedShelfGood1 =
            new ShelfGoodDto(shelfRepository.findByProductId(ON_SALE_PRODUCT_1.getId()).get().getId().toString(),
                getDto(ON_SALE_PRODUCT_1), PRODUCT_PRICE_1.getCurrency().toString(),
                PRODUCT_PRICE_1.getNumberStripped());
        ShelfGoodDto expectedShelfGood2 =
            new ShelfGoodDto(shelfRepository.findByProductId(ON_SALE_PRODUCT_2.getId()).get().getId().toString(),
                getDto(ON_SALE_PRODUCT_2), PRODUCT_PRICE_2.getCurrency().toString(),
                PRODUCT_PRICE_2.getNumberStripped());

        // When
        var result = mockMvc.perform(get("/customer/shelf/goods")
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andReturn();

        // [{"id":"fb28f725-22ec-4eaa-bb2f-9b8c2c3df4f5","product":{"productId":"2690b897-2be1-4ad3-920a-557067203f79","name":"iPhone XR","description":"Apple 10th special edition"},"currency":"HKD","basePrice":5000.00},{"id":"88b49133-5075-4a18-816c-11637da8805b","product":{"productId":"89a2ed0d-bd9c-4566-b878-19987fb07a64","name":"iPhone 13 mini","description":"Apple 13th small version"},"currency":"HKD","basePrice":6999.90}]
        List<ShelfGoodDto> actualShelfGoods =
            mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

        assertAll(
            () -> assertThat(actualShelfGoods.get(0).getId()).isEqualTo(expectedShelfGood1.getId()),
            () -> assertThat(actualShelfGoods.get(0).getCurrency()).isEqualTo(expectedShelfGood1.getCurrency()),
            () -> assertThat(
                actualShelfGoods.get(0).getBasePrice().compareTo(expectedShelfGood1.getBasePrice())).isEqualTo(0),
            () -> assertThat(actualShelfGoods.get(0).getProduct()).isNotNull(),
            () -> assertThat(actualShelfGoods.get(0).getProduct().getName()).isNotEmpty(),
            () -> assertThat(actualShelfGoods.get(0).getProduct().getName())
                .isEqualTo(expectedShelfGood1.getProduct().getName())
        );
    }


}