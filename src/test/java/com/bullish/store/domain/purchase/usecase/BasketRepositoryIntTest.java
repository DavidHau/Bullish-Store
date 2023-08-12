package com.bullish.store.domain.purchase.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BasketRepositoryIntTest {

    @Autowired
    BasketRepository basketRepository;

    @Autowired
    LineItemRepository lineItemRepository;

    private final String CUSTOMER_ID = "X123456";

    @BeforeEach
    void setup() {
        lineItemRepository.deleteAll();
        basketRepository.deleteAll();
    }

    @Test
    void given_basketAlreadyExist_when_saveBasketWithCustomerId_then_shouldUseExistingBasket() {
        // Given
        final UUID originalBasketId = basketRepository.saveIfNotExist(BasketEntity.builder()
                .customerId(CUSTOMER_ID)
                .build())
            .getId();
        assertThat(originalBasketId).isNotNull();
        assertThat(basketRepository.findAll()).hasSize(1);
        assertThat(basketRepository.findAll().get(0).getCustomerId()).isEqualTo(CUSTOMER_ID);

        // When
        BasketEntity actualBasket = basketRepository.saveIfNotExist(BasketEntity.builder()
            .customerId(CUSTOMER_ID)
            .build());
        basketRepository.saveIfNotExist(BasketEntity.builder()
            .customerId("another customer id")
            .build());

        // Then
        assertAll(
            () -> assertThat(actualBasket.getCustomerId()).isEqualTo(CUSTOMER_ID),
            () -> assertThat(actualBasket.getId()).isEqualTo(originalBasketId),
            () -> assertThat(basketRepository.findByCustomerId(CUSTOMER_ID)).isPresent(),
            () -> assertThat(basketRepository.findAll()).hasSize(2)
        );
    }

    @Test
    void given_oneBasketWithLineItem_when_saveLineItem_then_ableToSaveMultipleLineItem() {
        // Given
        final BasketEntity basketEntity = basketRepository.saveIfNotExist(BasketEntity.builder()
            .customerId(CUSTOMER_ID)
            .build());
        final LineItemEntity existingLineItem = lineItemRepository.save(LineItemEntity.builder()
            .basket(basketEntity)
            .productId("productId1")
            .shelfGoodId("shelfGoodId1")
            .build());
        assertThat(lineItemRepository.findAllByBasket(basketEntity)).hasSize(1);

        // When
        LineItemEntity actualNewLineItem = lineItemRepository.save(LineItemEntity.builder()
            .basket(basketEntity)
            .productId("productId2")
            .shelfGoodId("shelfGoodId2")
            .build());
        List<LineItemEntity> actualNewLineItemList = lineItemRepository.saveAll(List.of(
            LineItemEntity.builder()
                .basket(basketEntity)
                .productId("productId3")
                .shelfGoodId("shelfGoodId3")
                .build(),
            LineItemEntity.builder()
                .basket(basketEntity)
                .productId("productId4")
                .shelfGoodId("shelfGoodId4")
                .build()
        ));

        // Then
        List<LineItemEntity> actualBasketLineItemList = lineItemRepository.findAllByBasket(basketEntity);
        assertAll(
            () -> assertThat(actualBasketLineItemList).hasSize(4),
            () -> assertThat(actualBasketLineItemList).containsOnlyOnce(existingLineItem),
            () -> assertThat(actualBasketLineItemList).containsOnlyOnce(actualNewLineItem),
            () -> assertThat(actualBasketLineItemList).containsOnlyOnce(actualNewLineItemList.get(0),
                actualBasketLineItemList.get(1))
        );
    }

    @Test
    void given_existBasketAndLineItem_when_deleteLineItem_then_ableToDeleteWithoutDeletingBasket() {
        // Given
        final BasketEntity basketEntity = basketRepository.saveIfNotExist(BasketEntity.builder()
            .customerId(CUSTOMER_ID)
            .build());
        final List<LineItemEntity> existingLineItems = lineItemRepository.saveAll(List.of(
            LineItemEntity.builder()
                .basket(basketEntity)
                .productId("productId1")
                .shelfGoodId("shelfGoodId1")
                .build(),
            LineItemEntity.builder()
                .basket(basketEntity)
                .productId("productId2")
                .shelfGoodId("shelfGoodId2")
                .build()
        ));

        // When
        lineItemRepository.delete(existingLineItems.get(0));
        assertThat(lineItemRepository.findAllByBasket(basketEntity)).hasSize(1);
        lineItemRepository.delete(existingLineItems.get(1));

        // Then
        assertAll(
            () -> assertThat(lineItemRepository.findAllByBasket(basketEntity)).isEmpty(),
            () -> assertThat(basketRepository.findById(basketEntity.getId())).isPresent()
        );
    }

    @Test
    void given_basketWithLineItems_when_deleteBasket_then_throwException() {
        // Given
        final BasketEntity basketEntity = basketRepository.saveIfNotExist(BasketEntity.builder()
            .customerId(CUSTOMER_ID)
            .build());
        lineItemRepository.saveAll(List.of(
            LineItemEntity.builder()
                .basket(basketEntity)
                .productId("productId1")
                .shelfGoodId("shelfGoodId1")
                .build(),
            LineItemEntity.builder()
                .basket(basketEntity)
                .productId("productId2")
                .shelfGoodId("shelfGoodId2")
                .build()
        ));
        basketRepository.saveIfNotExist(BasketEntity.builder()
            .customerId("another customer id")
            .build());
        assertThat(lineItemRepository.findAllByBasket(basketEntity)).hasSize(2);
        assertThat(basketRepository.findAll()).hasSize(2);

        // When
        DataIntegrityViolationException actualException = assertThrows(DataIntegrityViolationException.class,
            () -> basketRepository.delete(basketEntity));

        // Then
        assertAll(
            () -> assertThat(actualException.getMessage())
                .contains("LINE_ITEM FOREIGN KEY(BASKET_ID) REFERENCES PUBLIC.BASKET(ID)"),
            () -> assertThat(basketRepository.findAll()).hasSize(2),
            () -> assertThat(basketRepository.findById(basketEntity.getId())).isPresent(),
            () -> assertThat(lineItemRepository.findAllByBasket(basketEntity)).hasSize(2)
        );
    }
}