package com.bullish.store.domain.checkout.usecase;

import com.bullish.store.domain.adjustment.api.DiscountManagement;
import com.bullish.store.domain.adjustment.api.DiscountRatioDto;
import com.bullish.store.domain.checkout.api.ReceiptDto;
import com.bullish.store.domain.checkout.port.CheckOutAdjustmentDomainApi;
import com.bullish.store.domain.checkout.port.CheckOutProductDomainApi;
import com.bullish.store.domain.checkout.port.CheckOutPurchaseDomainApi;
import com.bullish.store.domain.product.api.ProductDto;
import com.bullish.store.domain.product.api.ProductManagement;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import com.bullish.store.domain.purchase.api.BasketDto;
import com.bullish.store.domain.purchase.api.BasketManagement;
import com.bullish.store.domain.purchase.api.LineItemDto;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CheckOutServiceTest {

    private CheckOutServiceImpl checkOutService;

    @Spy
    @InjectMocks
    private CheckOutPurchaseDomainApi checkOutPurchaseDomainApi;

    @Spy
    @InjectMocks
    private CheckOutProductDomainApi checkOutProductDomainApi;

    @Spy
    @InjectMocks
    private CheckOutAdjustmentDomainApi checkOutAdjustmentDomainApi;

    @Mock
    private ProductManagement productManagement;

    @Mock
    private ProductShelfService productShelfService;

    @Mock
    private BasketManagement basketManagement;

    @Mock
    private DiscountManagement discountManagement;


    private final String CUSTOMER_ID_1 = "x123456";
    private final String BASKET_ID_1 = "BASKET_ID_1";
    private final ShelfGoodDto SHELF_GOOD_1 = new ShelfGoodDto("SHELF_GOOD_ID_1",
        new ProductDto("PRODUCT_ID_1",
            "PRODUCT_NAME_1",
            "PRODUCT_DESCRIPTION_1"),
        "HKD",
        BigDecimal.valueOf(500)
    );
    private final ShelfGoodDto SHELF_GOOD_2 = new ShelfGoodDto("SHELF_GOOD_ID_2",
        new ProductDto("PRODUCT_ID_2",
            "PRODUCT_NAME_2",
            "PRODUCT_DESCRIPTION_2"),
        "HKD",
        BigDecimal.valueOf(5000)
    );
    private final ShelfGoodDto SHELF_GOOD_3 = new ShelfGoodDto("SHELF_GOOD_ID_3",
        new ProductDto("PRODUCT_ID_3",
            "PRODUCT_NAME_3",
            "PRODUCT_DESCRIPTION_3"),
        "HKD",
        BigDecimal.valueOf(99.9)
    );
    private final BasketDto BASKET_1 = new BasketDto(BASKET_ID_1, CUSTOMER_ID_1, List.of(
        new LineItemDto("0", SHELF_GOOD_1.getShelfGoodId(), SHELF_GOOD_1.getProduct().getProductId()),
        new LineItemDto("1", SHELF_GOOD_2.getShelfGoodId(), SHELF_GOOD_2.getProduct().getProductId()),
        new LineItemDto("2", SHELF_GOOD_3.getShelfGoodId(), SHELF_GOOD_3.getProduct().getProductId())
    ));

    private final DiscountRatioDto GLOBAL_DISCOUNT_2ND_ITEM_40_PC_OFF = new DiscountRatioDto("DISCOUNT_ID_1",
        "2nd item 40% off", null, true, 0.4, 2);

    @BeforeEach
    void setup() {
        checkOutService =
            new CheckOutServiceImpl(checkOutPurchaseDomainApi, checkOutProductDomainApi, checkOutAdjustmentDomainApi);
        doReturn(Optional.of(SHELF_GOOD_1)).when(productShelfService).findGood(SHELF_GOOD_1.getShelfGoodId());
        doReturn(Optional.of(SHELF_GOOD_2)).when(productShelfService).findGood(SHELF_GOOD_2.getShelfGoodId());
        doReturn(Optional.of(SHELF_GOOD_3)).when(productShelfService).findGood(SHELF_GOOD_3.getShelfGoodId());

        doReturn(Optional.of(BASKET_1)).when(basketManagement).getBasket(CUSTOMER_ID_1);
    }

    @Test
    void given_basketContains3items_when_getReceipt_then_returnWithTotalPriceCalculated() {
        ReceiptDto actualReceipt = checkOutService.getReceipt(CUSTOMER_ID_1).get();

        assertAll(
            () -> assertThat(actualReceipt.getCustomerId()).isEqualTo(CUSTOMER_ID_1),
            () -> assertThat(actualReceipt.getBasketId()).isEqualTo(BASKET_ID_1),
            () -> assertThat(actualReceipt.getLineItemList()).hasSize(3),
            () -> assertThat(actualReceipt.getTotalPrice()).isEqualTo(Money.of(5599.9, "HKD"))
        );
    }

    @Test
    void given_2ndItem40pcOffDiscount_when_buy4IdenticalProduct_then_generateReceiptWithDiscountTwice() {
        // Given
        doReturn(List.of(GLOBAL_DISCOUNT_2ND_ITEM_40_PC_OFF)).when(discountManagement).getAllAutoApplyRatioDiscount();
        final String customerId2 = "y123456";
        final String basketId2 = "BASKET_ID_2";
        final BasketDto basket2 = new BasketDto(basketId2, customerId2, List.of(
            new LineItemDto("0", SHELF_GOOD_2.getShelfGoodId(), SHELF_GOOD_2.getProduct().getProductId()),
            new LineItemDto("1", SHELF_GOOD_2.getShelfGoodId(), SHELF_GOOD_2.getProduct().getProductId()),
            new LineItemDto("2", SHELF_GOOD_2.getShelfGoodId(), SHELF_GOOD_2.getProduct().getProductId()),
            new LineItemDto("3", SHELF_GOOD_2.getShelfGoodId(), SHELF_GOOD_2.getProduct().getProductId())
        ));
        doReturn(Optional.of(basket2)).when(basketManagement).getBasket(customerId2);


        // When
        ReceiptDto actualReceipt = checkOutService.getReceipt(customerId2).get();

        // Then
        /*
            iPhone XR        $5000

            iPhone XR        $5000
                  2nd item 40% off
                  discount  ($2000)

            iPhone XR        $5000

            iPhone XR        $5000
                  2nd item 40% off
                  discount  ($2000)

            Total Base Price $20000
            Total Discount  ($4000)
            Total Price      $16000
         */
        assertAll(
            () -> assertThat(actualReceipt.getCustomerId()).isEqualTo(customerId2),
            () -> assertThat(actualReceipt.getBasketId()).isEqualTo(basketId2),
            () -> assertThat(actualReceipt.getLineItemList()).hasSize(4)
                .containsExactlyInAnyOrder(new ReceiptDto.LineItem(0,
                        SHELF_GOOD_2.getShelfGoodId(),
                        SHELF_GOOD_2.getProduct().getProductId(),
                        SHELF_GOOD_2.getProduct().getName(),
                        Money.of(SHELF_GOOD_2.getBasePrice(), SHELF_GOOD_2.getCurrency()),
                        null,
                        null),
                    new ReceiptDto.LineItem(1,
                        SHELF_GOOD_2.getShelfGoodId(),
                        SHELF_GOOD_2.getProduct().getProductId(),
                        SHELF_GOOD_2.getProduct().getName(),
                        Money.of(SHELF_GOOD_2.getBasePrice(), SHELF_GOOD_2.getCurrency()),
                        "2nd item 40% off",
                        Money.of(-2000, "HKD")   // 5000 * 40% = 2000
                    ), new ReceiptDto.LineItem(2,
                        SHELF_GOOD_2.getShelfGoodId(),
                        SHELF_GOOD_2.getProduct().getProductId(),
                        SHELF_GOOD_2.getProduct().getName(),
                        Money.of(SHELF_GOOD_2.getBasePrice(), SHELF_GOOD_2.getCurrency()),
                        null,
                        null),
                    new ReceiptDto.LineItem(3,
                        SHELF_GOOD_2.getShelfGoodId(),
                        SHELF_GOOD_2.getProduct().getProductId(),
                        SHELF_GOOD_2.getProduct().getName(),
                        Money.of(SHELF_GOOD_2.getBasePrice(), SHELF_GOOD_2.getCurrency()),
                        "2nd item 40% off",
                        Money.of(-2000, "HKD")   // 5000 * 40% = 2000
                    )),

            () -> assertThat(actualReceipt.getTotalBasePrice()).isEqualTo(Money.of(20000, "HKD")),
            () -> assertThat(actualReceipt.getTotalDiscount()).isEqualTo(Money.of(-4000, "HKD")),
            () -> assertThat(actualReceipt.getTotalPrice()).isEqualTo(Money.of(16000, "HKD"))
        );
    }

}