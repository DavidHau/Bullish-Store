package com.bullish.store.domain.purchase.usecase;

import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.usecase.ProductEntity;
import com.bullish.store.domain.product.usecase.ProductRepository;
import com.bullish.store.domain.product.usecase.ShelfRepository;
import com.bullish.store.domain.purchase.api.BasketDto;
import com.bullish.store.domain.purchase.api.LineItemDto;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BasketManagementIntTest {

    @Autowired
    private BasketManagementImpl basketManagement;

    @Autowired
    private ProductShelfService shelfService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private LineItemRepository lineItemRepository;

    private final ProductEntity ON_SALE_PRODUCT_1 = ProductEntity.builder()
        .name("iPhone XR")
        .description("Apple 10th special edition")
        .build();
    private final ProductEntity ON_SALE_PRODUCT_2 = ProductEntity.builder()
        .name("iPhone 13 mini")
        .description("Apple 13th small version")
        .build();
    private final ProductEntity ON_SALE_PRODUCT_3 = ProductEntity.builder()
        .name("Galaxy S")
        .description("Samsung mobile")
        .build();
    private final Money PRODUCT_PRICE_1 = Money.of(500, "HKD");
    private final Money PRODUCT_PRICE_2 = Money.of(5000, "HKD");
    private final Money PRODUCT_PRICE_3 = Money.of(99.9, "HKD");
    String shelfGoodId1;
    String shelfGoodId2;
    String shelfGoodId3;

    @BeforeEach
    void setup() {
        lineItemRepository.deleteAll();
        basketRepository.deleteAll();
        shelfRepository.deleteAll();
        productRepository.deleteAll();

        prepareDefaultProductList();
    }

    private void prepareDefaultProductList() {
        ON_SALE_PRODUCT_1.setId(productRepository.save(ON_SALE_PRODUCT_1).getId());
        ON_SALE_PRODUCT_2.setId(productRepository.save(ON_SALE_PRODUCT_2).getId());
        ON_SALE_PRODUCT_3.setId(productRepository.save(ON_SALE_PRODUCT_3).getId());

        shelfGoodId1 = shelfService.launch(ON_SALE_PRODUCT_1.getId().toString(), PRODUCT_PRICE_1);
        shelfGoodId2 = shelfService.launch(ON_SALE_PRODUCT_2.getId().toString(), PRODUCT_PRICE_2);
        shelfGoodId3 = shelfService.launch(ON_SALE_PRODUCT_3.getId().toString(), PRODUCT_PRICE_3);
    }

    @Test
    void given_added2ProductToBasket_when_getBasket_then_shouldReturnBasketWith2LineItems() {
        // Give
        String customerId = "x123456";
        basketManagement.addShelfGoodToBasket(customerId, shelfGoodId1);
        basketManagement.addShelfGoodToBasket(customerId, shelfGoodId3);
        basketManagement.addShelfGoodToBasket("another customer", shelfGoodId2);

        // When
        BasketDto actualBasket = basketManagement.getBasket(customerId).get();

        // Then
        actualBasket.getLineItemList().forEach(item -> item.setLineItemId("anyId"));
        assertAll(
            () -> assertThat(actualBasket.getCustomerId()).isEqualTo(customerId),
            () -> assertThat(actualBasket.getLineItemList()).hasSize(2)
                .containsExactlyInAnyOrder(
                    new LineItemDto("anyId", shelfGoodId1, ON_SALE_PRODUCT_1.getId().toString()),
                    new LineItemDto("anyId", shelfGoodId3, ON_SALE_PRODUCT_3.getId().toString())
                )
        );
    }

    @Test
    void given_multipleBaskets_when_removeShelfGoodFromBasket_then_onlyRemoveFromTheCorrespondingCustomerBasket() {
        // Give
        final String customerId = "x123456";
        basketManagement.addShelfGoodToBasket(customerId, shelfGoodId1);
        basketManagement.addShelfGoodToBasket(customerId, shelfGoodId2);
        basketManagement.addShelfGoodToBasket("another customer", shelfGoodId2);

        // When
        basketManagement.removeShelfGoodFromBasket(customerId, shelfGoodId2);

        // Then
        final BasketDto actualBasket = basketManagement.getBasket(customerId).orElseThrow();
        final BasketDto anotherCustomerBasket = basketManagement.getBasket("another customer").orElseThrow();
        assertAll(
            () -> assertThat(actualBasket.getCustomerId()).isEqualTo(customerId),
            () -> assertThat(actualBasket.getLineItemList()).hasSize(1),
            () -> assertThat(actualBasket.getLineItemList().get(0).getShelfId()).isEqualTo(shelfGoodId1),

            () -> assertThat(anotherCustomerBasket.getCustomerId()).isEqualTo("another customer"),
            () -> assertThat(anotherCustomerBasket.getLineItemList()).hasSize(1),
            () -> assertThat(anotherCustomerBasket.getLineItemList().get(0).getShelfId()).isEqualTo(shelfGoodId2)
        );
    }

    @Test
    void given_onlyRemainOneGoodInBasket_when_removeShelfGoodFromBasket_then_alsoRemoveBasket() {
        // Give
        final String customerId = "x123456";
        basketManagement.addShelfGoodToBasket(customerId, shelfGoodId2);
        basketManagement.addShelfGoodToBasket("another customer", shelfGoodId2);
        assertThat(basketRepository.findByCustomerId(customerId)).isPresent();

        // When
        basketManagement.removeShelfGoodFromBasket(customerId, shelfGoodId2);

        // Then
        assertAll(
            () -> assertThat(basketRepository.findByCustomerId(customerId)).isNotPresent(),
            () -> assertThat(basketRepository.findByCustomerId("another customer")).isPresent()
        );
    }

}