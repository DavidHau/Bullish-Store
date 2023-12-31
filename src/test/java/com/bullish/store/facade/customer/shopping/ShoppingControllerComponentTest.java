package com.bullish.store.facade.customer.shopping;

import com.bullish.store.domain.adjustment.usecase.DiscountAmountEntity;
import com.bullish.store.domain.adjustment.usecase.DiscountAmountRepository;
import com.bullish.store.domain.adjustment.usecase.DiscountRatioEntity;
import com.bullish.store.domain.adjustment.usecase.DiscountRatioRepository;
import com.bullish.store.domain.product.api.ProductDto;
import com.bullish.store.domain.product.api.ProductManagement;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.domain.product.usecase.ProductEntity;
import com.bullish.store.domain.product.usecase.ProductRepository;
import com.bullish.store.domain.product.usecase.ShelfGoodEntity;
import com.bullish.store.domain.product.usecase.ShelfRepository;
import com.bullish.store.domain.purchase.usecase.BasketEntity;
import com.bullish.store.domain.purchase.usecase.BasketRepository;
import com.bullish.store.domain.purchase.usecase.LineItemEntity;
import com.bullish.store.domain.purchase.usecase.LineItemRepository;
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
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ShelfRepository shelfRepository;

    @Autowired
    BasketRepository basketRepository;

    @Autowired
    LineItemRepository lineItemRepository;

    @Autowired
    private ProductManagement productManagement;

    @Autowired
    private ProductShelfService shelfService;

    @Autowired
    DiscountRatioRepository discountRatioRepository;

    @Autowired
    DiscountAmountRepository discountAmountRepository;

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
        discountRatioRepository.deleteAll();
        discountAmountRepository.deleteAll();
        lineItemRepository.deleteAll();
        basketRepository.deleteAll();
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
            () -> assertThat(actualShelfGoods.get(0).getShelfGoodId()).isEqualTo(expectedShelfGood1.getShelfGoodId()),
            () -> assertThat(actualShelfGoods.get(0).getCurrency()).isEqualTo(expectedShelfGood1.getCurrency()),
            () -> assertThat(
                actualShelfGoods.get(0).getBasePrice().compareTo(expectedShelfGood1.getBasePrice())).isEqualTo(0),
            () -> assertThat(actualShelfGoods.get(0).getProduct()).isNotNull(),
            () -> assertThat(actualShelfGoods.get(0).getProduct().getName()).isNotEmpty(),
            () -> assertThat(actualShelfGoods.get(0).getProduct().getName())
                .isEqualTo(expectedShelfGood1.getProduct().getName())
        );
    }

    @Test
    void given_validShelfGoodId_when_addGoodToBasket_then_return204() throws Exception {
        // Given
        final String customerId = "x123456";
        final ShelfGoodEntity toBeAddedGood = shelfRepository.findByProductId(ON_SALE_PRODUCT_1.getId()).get();
        final UUID goodId = toBeAddedGood.getId();

        // When
        var result = mockMvc.perform(post("/customer/basket/{shelf-good-id}", goodId)
                .header("x-bullish-customer-id", customerId)
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isNoContent())
            .andReturn();

        Optional<BasketEntity> actualBasket = basketRepository.findByCustomerId(customerId);
        assertAll(
            () -> assertThat(actualBasket).isPresent(),
            () -> assertThat(actualBasket.get().getCustomerId()).isEqualTo(customerId),
            () -> {
                List<LineItemEntity> actualLineItemList = lineItemRepository.findAllByBasket(actualBasket.get());
                assertAll(
                    () -> assertThat(actualLineItemList).hasSize(1),
                    () -> assertThat(actualLineItemList.get(0).getShelfGoodId()).isEqualTo(goodId.toString())
                );
            }
        );
    }

    @Test
    void given_invalidShelfGoodId_when_addGoodToBasket_then_return400() throws Exception {
        // Given
        final String customerId = "x123456";
        final UUID goodId = UUID.randomUUID();

        // When
        var result = mockMvc.perform(post("/customer/basket/{shelf-good-id}", goodId)
                .header("x-bullish-customer-id", customerId)
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isBadRequest())
            .andReturn();

        Optional<BasketEntity> actualBasket = basketRepository.findByCustomerId(customerId);
        assertAll(
            () -> assertThat(actualBasket).isNotPresent()
        );
    }

    @Test
    void given_multipleGoodInBasket_when_deleteGoodFromBasket_then_return204AndOnlyRemoveOneEverytime()
        throws Exception {
        // Given
        final String customerId = "x123456";
        final String shelfGoodId1 = shelfRepository.findByProductId(ON_SALE_PRODUCT_1.getId()).get().getId().toString();
        final String shelfGoodId2 = shelfRepository.findByProductId(ON_SALE_PRODUCT_2.getId()).get().getId().toString();

        addToBasket(shelfGoodId1, customerId);
        addToBasket(shelfGoodId2, customerId);
        addToBasket(shelfGoodId1, customerId);

        Optional<BasketEntity> actualBasket = basketRepository.findByCustomerId(customerId);
        assertAll(
            () -> assertThat(actualBasket).isPresent(),
            () -> assertThat(actualBasket.get().getCustomerId()).isEqualTo(customerId),
            () -> assertThat(lineItemRepository.findAllByBasket(actualBasket.get())).hasSize(3)
        );

        // When
        mockMvc.perform(delete("/customer/basket/{shelf-good-id}", shelfGoodId1)
                .header("x-bullish-customer-id", customerId)
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isNoContent())
            .andReturn();

        assertAll(
            () -> {
                List<LineItemEntity> actualLineItemList = lineItemRepository.findAllByBasket(actualBasket.get());
                assertAll(
                    () -> assertThat(actualLineItemList).hasSize(2),
                    () -> assertThat(actualLineItemList.stream().map(LineItemEntity::getShelfGoodId))
                        .containsExactlyInAnyOrder(shelfGoodId1, shelfGoodId2)
                );
            }
        );

        // When
        mockMvc.perform(delete("/customer/basket/{shelf-good-id}", shelfGoodId2)
                .header("x-bullish-customer-id", customerId)
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isNoContent())
            .andReturn();

        assertAll(
            () -> {
                List<LineItemEntity> actualLineItemList = lineItemRepository.findAllByBasket(actualBasket.get());
                assertAll(
                    () -> assertThat(actualLineItemList).hasSize(1),
                    () -> assertThat(actualLineItemList.stream().map(LineItemEntity::getShelfGoodId))
                        .containsExactlyInAnyOrder(shelfGoodId1)
                );
            }
        );
    }

    @Test
    void given_buy4ProductsWithOneRatioDiscount_when_getReceipt_then_returnItemsWithDiscountCalculated()
        throws Exception {
        // Given
        final String customerId = "x123456";
        ShelfGoodDto expectedShelfGood1 =
            new ShelfGoodDto(shelfRepository.findByProductId(ON_SALE_PRODUCT_1.getId()).get().getId().toString(),
                getDto(ON_SALE_PRODUCT_1), PRODUCT_PRICE_1.getCurrency().toString(),
                PRODUCT_PRICE_1.getNumberStripped());
        ShelfGoodDto expectedShelfGood2 =
            new ShelfGoodDto(shelfRepository.findByProductId(ON_SALE_PRODUCT_2.getId()).get().getId().toString(),
                getDto(ON_SALE_PRODUCT_2), PRODUCT_PRICE_2.getCurrency().toString(),
                PRODUCT_PRICE_2.getNumberStripped());

        DiscountRatioEntity discountRatio = DiscountRatioEntity.builder()
            .name("2nd iPhone 13 mini 50% off")
            .applyToAllProduct(false)
            .shelfGoodId(expectedShelfGood2.getShelfGoodId())
            .offRatio(0.5)
            .applyAtEveryNthNumberOfIdenticalItem(2)
            .build();
        discountRatioRepository.save(discountRatio);

        addToBasket(expectedShelfGood1.getShelfGoodId(), customerId);
        addToBasket(expectedShelfGood1.getShelfGoodId(), customerId);
        addToBasket(expectedShelfGood2.getShelfGoodId(), customerId);
        addToBasket(expectedShelfGood2.getShelfGoodId(), customerId);


        // When
        var result = mockMvc.perform(get("/customer/basket/receipt")
                .header("x-bullish-customer-id", customerId)
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(customerId))
            .andExpect(jsonPath("$.basketId").isNotEmpty())
            .andExpect(jsonPath("$.currency").value("HKD"))
            .andExpect(jsonPath("$.lineItemList").isArray())
            .andExpect(jsonPath("$.lineItemList", hasSize(4)))
            .andExpect(jsonPath("$.lineItemList[0].name").value("iPhone XR"))
            .andExpect(jsonPath("$.lineItemList[0].discountName").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[0].discountedAmount").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[1].name").value("iPhone XR"))
            .andExpect(jsonPath("$.lineItemList[1].discountName").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[1].discountedAmount").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[2].name").value("iPhone 13 mini"))
            .andExpect(jsonPath("$.lineItemList[2].discountName").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[2].discountedAmount").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[3].name").value("iPhone 13 mini"))
            .andExpect(jsonPath("$.lineItemList[3].discountName").value("2nd iPhone 13 mini 50% off"))
            .andExpect(jsonPath("$.lineItemList[3].discountedAmount").value(-3499.95))
            .andExpect(jsonPath("$.totalBasePrice").value(23999.8))
            .andExpect(jsonPath("$.totalDiscount").value(-3499.95))
            .andExpect(jsonPath("$.totalPrice").value(20499.85))
            .andReturn();
    }

    @Test
    void given_buy4ProductsWithOneOverAmountDiscount_when_getReceipt_then_returnItemsWithDiscountCalculated()
        throws Exception {
        // Given
        final String customerId = "x123456";
        ShelfGoodDto expectedShelfGood1 =
            new ShelfGoodDto(shelfRepository.findByProductId(ON_SALE_PRODUCT_1.getId()).get().getId().toString(),
                getDto(ON_SALE_PRODUCT_1), PRODUCT_PRICE_1.getCurrency().toString(),
                PRODUCT_PRICE_1.getNumberStripped());
        ShelfGoodDto expectedShelfGood2 =
            new ShelfGoodDto(shelfRepository.findByProductId(ON_SALE_PRODUCT_2.getId()).get().getId().toString(),
                getDto(ON_SALE_PRODUCT_2), PRODUCT_PRICE_2.getCurrency().toString(),
                PRODUCT_PRICE_2.getNumberStripped());

        DiscountAmountEntity discountAmount = DiscountAmountEntity.builder()
            .name("2nd iPhone $8000 off")
            .applyToAllProduct(false)
            .shelfGoodId(expectedShelfGood2.getShelfGoodId())
            .currency("HKD")
            .discountAmount(BigDecimal.valueOf(8000))
            .applyAtEveryNthNumberOfIdenticalItem(2)
            .build();
        discountAmountRepository.save(discountAmount);

        addToBasket(expectedShelfGood1.getShelfGoodId(), customerId);
        addToBasket(expectedShelfGood1.getShelfGoodId(), customerId);
        addToBasket(expectedShelfGood2.getShelfGoodId(), customerId);
        addToBasket(expectedShelfGood2.getShelfGoodId(), customerId);


        // When
        var result = mockMvc.perform(get("/customer/basket/receipt")
                .header("x-bullish-customer-id", customerId)
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").value(customerId))
            .andExpect(jsonPath("$.basketId").isNotEmpty())
            .andExpect(jsonPath("$.currency").value("HKD"))
            .andExpect(jsonPath("$.lineItemList").isArray())
            .andExpect(jsonPath("$.lineItemList", hasSize(4)))
            .andExpect(jsonPath("$.lineItemList[0].name").value("iPhone XR"))
            .andExpect(jsonPath("$.lineItemList[0].discountName").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[0].discountedAmount").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[1].name").value("iPhone XR"))
            .andExpect(jsonPath("$.lineItemList[1].discountName").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[1].discountedAmount").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[2].name").value("iPhone 13 mini"))
            .andExpect(jsonPath("$.lineItemList[2].discountName").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[2].discountedAmount").doesNotExist())
            .andExpect(jsonPath("$.lineItemList[3].name").value("iPhone 13 mini"))
            .andExpect(jsonPath("$.lineItemList[3].discountName").value("2nd iPhone $8000 off"))
            .andExpect(jsonPath("$.lineItemList[3].discountedAmount").value(-6999.9))
            .andExpect(jsonPath("$.totalBasePrice").value(23999.8))
            .andExpect(jsonPath("$.totalDiscount").value(-6999.9))
            .andExpect(jsonPath("$.totalPrice").value(16999.9))
            .andReturn();
    }

    MvcResult addToBasket(String goodId, String customerId) throws Exception {
        return mockMvc.perform(post("/customer/basket/{shelf-good-id}", goodId)
                .header("x-bullish-customer-id", customerId)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNoContent())
            .andReturn();
    }

    @Test
    void given_noProductNoBasket_when_getReceipt_then_return200AndNoBody() throws Exception {
        // Given
        final String customerId = "x123456";

        // When
        var result = mockMvc.perform(get("/customer/basket/receipt")
                .header("x-bullish-customer-id", customerId)
                .contentType(MediaType.APPLICATION_JSON)
            )

            // Then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId").isEmpty())
            .andExpect(jsonPath("$.basketId").isEmpty())
            .andReturn();
    }

}