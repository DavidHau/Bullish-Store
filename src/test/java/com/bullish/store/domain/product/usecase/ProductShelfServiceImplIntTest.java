package com.bullish.store.domain.product.usecase;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ProductShelfServiceImplIntTest {
    @Autowired
    private ProductManagementImpl productManagement;

    @Autowired
    private ProductShelfServiceImpl shelfService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShelfRepository shelfRepository;

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

    @Test
    void given_productIsOnShelf_when_discontinue_then_removeProductFromShelf() {
        // Given
        List<ProductEntity> productEntities = prepareDefaultProductList();
        ProductEntity productToBeLaunch = productEntities.get(0);
        String shelfGoodId = shelfService.launch(productToBeLaunch.getId().toString()
            , Money.of(123, "HKD"));
        assertThat(shelfRepository.findByProductId(productToBeLaunch.getId())).isPresent();

        // When
        shelfService.discontinue(productToBeLaunch.getId().toString(), shelfGoodId);

        // Then
        assertThat(shelfRepository.findByProductId(productToBeLaunch.getId())).isNotPresent();
    }


}