package com.bullish.store.facade.admin.product;

import com.bullish.store.domain.product.api.ProductDto;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        productRepository.deleteAll();
        shelfRepository.deleteAll();
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
        List<ProductDto> expectedProductList = productEntities.stream().map(this::getDto).collect(Collectors.toList());

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
                    () -> assertThat(actualShelfGoodEntity.getProductId()).isEqualTo(product.getId()),
                    () -> assertThat(actualBasePrice).isEqualTo(Money.of(5000.5, "HKD"))
                );
            }
        );
    }
}