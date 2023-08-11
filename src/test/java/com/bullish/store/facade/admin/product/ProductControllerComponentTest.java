package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.api.ProductDto;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.domain.product.usecase.ProductEntity;
import com.bullish.store.domain.product.usecase.ProductRepository;
import com.bullish.store.domain.product.usecase.ShelfGoodEntity;
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
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        shelfRepository.deleteAll();
        productRepository.deleteAll();
    }

    private List<ProductEntity> prepareDefaultProductList() {
        List<ProductEntity> defaultProductList = List.of(
            ProductEntity.builder()
                .name("iPhone XR")
                .description("Apple 10th special edition")
                .build(),
            ProductEntity.builder()
                .name("iPhone 13 mini")
                .description("Apple 13th small version")
                .build(),
            ProductEntity.builder()
                .name("Galaxy S")
                .description("Samsung mobile")
                .build());
        return productRepository.saveAll(defaultProductList);
    }

    private ProductDto getDto(ProductEntity productEntity) {
        return new ProductDto(productEntity.getId().toString(),
            productEntity.getName(),
            productEntity.getDescription());
    }

    @Test
    void given_noProduct_when_createProduct_then_return201AndProductIdAndStoreInDb() throws Exception {
        // Given
        assertThat(productRepository.findAll()).isEmpty();

        // When
        var result = mockMvc.perform(post("/admin/products")
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
    void given_3Products_when_getProducts_then_returnListOfProduct() throws Exception {
        // Given
        List<ProductEntity> productEntities = prepareDefaultProductList();
        final List<ProductDto> expectedProductList =
            productEntities.stream().map(this::getDto).collect(Collectors.toList());

        // When
        var result = mockMvc.perform(get("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(3)))
            .andReturn();

        // [{"productId":"27996449-5465-4a05-ae19-3e1b74b6ee4c","name":"iPhone XR","description":"Apple 10th special edition"},{"productId":"146f0e98-9ef9-4fdd-80fa-f28f61f693d0","name":"iPhone 13 mini","description":"Apple 13th small version"},{"productId":"17f3e3ac-08b9-4fde-b6ee-3ef8e68852f6","name":"Galaxy S","description":"Samsung mobile"}]
        List<ProductDto> actualProductList =
            mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

        assertAll(
            () -> assertThat(actualProductList).containsAll(expectedProductList),
            () -> assertThat(actualProductList.get(0).getProductId()).isNotEmpty(),
            () -> assertThat(actualProductList.get(0).getName()).isNotEmpty(),
            () -> assertThat(actualProductList.get(0).getDescription()).isNotEmpty()
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
        var result = mockMvc.perform(post("/admin/products/{product-id}/launch", productId)
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
                    () -> assertThat(actualShelfGoodEntity.getProduct().getId()).isEqualTo(product.getId()),
                    () -> assertThat(actualBasePrice).isEqualTo(Money.of(5000.5, "HKD"))
                );
            }
        );
    }

    @Test
    void given_productAlreadyRelaunched_when_discontinueProduct_then_return409() throws Exception {
        // Given
        ProductEntity product = productRepository.save(ProductEntity.builder()
            .name("Galaxy S")
            .name("Samsung mobile")
            .build());
        final UUID productId = product.getId();
        final String originalShelfGoodId = launchProduct(productId, "HKD", 123)
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        discontinueProduct(productId, originalShelfGoodId)
            .andExpect(status().isNoContent());
        launchProduct(productId, "HKD", 456.5)
            .andExpect(status().isCreated());

        // when
        var result = discontinueProduct(productId, originalShelfGoodId)

            // Then
            .andExpect(status().isConflict())
            .andReturn();

        Optional<ShelfGoodEntity> actualShelf = shelfRepository.findByProductId(productId);
        assertAll(
            () -> assertThat(actualShelf).isPresent(),
            () -> assertThat(BigDecimal.valueOf(456.5).compareTo(actualShelf.get().getBasePrice())).isEqualTo(0),
            () -> assertThat(result.getResponse().getContentAsString()).contains("Product in shelf is outdated")
        );
    }

    private ResultActions launchProduct(UUID productId, String currency, double basePrice) throws Exception {
        return mockMvc.perform(post("/admin/products/{product-id}/launch", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                    {
                        "currency": "%s",
                        "basePrice": %s
                    }
                    """.formatted(currency, basePrice)
            )
        );
    }

    private ResultActions discontinueProduct(UUID productId, String shelfGoodId) throws Exception {
        return mockMvc.perform(delete("/admin/products/{product-id}/discontinue/{shelf-good-id}"
            , productId, shelfGoodId)
            .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void given_shelfGoodAlreadyDiscontinued_when_deleteProduct_then_return204() throws Exception {
        // Given
        ProductEntity product = productRepository.save(ProductEntity.builder()
            .name("Galaxy S")
            .name("Samsung mobile")
            .build());
        final UUID productId = product.getId();
        final String originalShelfGoodId = launchProduct(productId, "HKD", 123)
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        discontinueProduct(productId, originalShelfGoodId)
            .andExpect(status().isNoContent());
        assertThat(productRepository.findById(productId)).isPresent();

        // when
        var result = mockMvc.perform(delete("/admin/products/{product-id}", productId)
                .contentType(MediaType.APPLICATION_JSON))

            // Then
            .andExpect(status().isNoContent())
            .andReturn();

        assertThat(productRepository.findById(productId)).isNotPresent();
    }

    @Test
    void given_productIsOnSale_when_deleteProduct_then_return423AndDoNotDelete() throws Exception {
        // Given
        ProductEntity product = productRepository.save(ProductEntity.builder()
            .name("Galaxy S")
            .name("Samsung mobile")
            .build());
        final UUID productId = product.getId();
        launchProduct(productId, "HKD", 123)
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        assertThat(productRepository.findById(productId)).isPresent();

        // when
        var result = mockMvc.perform(delete("/admin/products/{product-id}", productId)
                .contentType(MediaType.APPLICATION_JSON))

            // Then
            .andExpect(status().isLocked())
            .andReturn();

        assertAll(
            () -> assertThat(result.getResponse().getContentAsString()).contains(
                "Please discontinue product before removing product"),
            () -> assertThat(productRepository.findById(productId)).isPresent()
        );
    }

    @Test
    void given_2ProductsOnSale_when_getAllShelfGoods_then_returnListOfShelfGoods() throws Exception {
        // Given
        List<ProductEntity> productEntities = prepareDefaultProductList();
        final String shelfGoodId0 = launchProduct(productEntities.get(0).getId(), "HKD", 123)
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        final String shelfGoodId1 = launchProduct(productEntities.get(1).getId(), "HKD", 456.5)
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        final List<ShelfGoodDto> expectedShelfGoodList = List.of(
            new ShelfGoodDto(shelfGoodId0, this.getDto(productEntities.get(0)), "HKD", BigDecimal.valueOf(123)),
            new ShelfGoodDto(shelfGoodId1, this.getDto(productEntities.get(1)), "HKD", BigDecimal.valueOf(456.5))
        );

        // When
        var result = mockMvc.perform(get("/admin/products/shelf/goods")
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andReturn();

        // [{"id":"57c4cbcb-2028-427c-adae-2ebc713cbcfe","product":{"productId":"6d06e386-6014-4919-883a-64c8492e54ba","name":"iPhone XR","description":"Apple 10th special edition"},"currency":"HKD","basePrice":123.00},{"id":"3c91783a-e29a-4f3b-b4dd-ba758518f3e4","product":{"productId":"66210de1-e337-41d1-ae66-159aa10b77f2","name":"iPhone 13 mini","description":"Apple 13th small version"},"currency":"HKD","basePrice":456.50}]
        List<ShelfGoodDto> actualShelfGoods =
            mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

        assertAll(
            () -> assertThat(actualShelfGoods.get(0).getId()).isNotEmpty(),
            () -> assertThat(actualShelfGoods.get(0).getId()).isEqualTo(expectedShelfGoodList.get(0).getId()),
            () -> assertThat(actualShelfGoods.get(0).getCurrency()).isNotEmpty(),
            () -> assertThat(BigDecimal.valueOf(123).compareTo(actualShelfGoods.get(0).getBasePrice())).isEqualTo(0),
            () -> assertThat(actualShelfGoods.get(0).getProduct()).isNotNull(),
            () -> assertThat(actualShelfGoods.get(0).getProduct().getName()).isNotEmpty(),
            () -> assertThat(actualShelfGoods.get(0).getProduct().getName())
                .isEqualTo(expectedShelfGoodList.get(0).getProduct().getName())
        );
    }

}