package com.bullish.store.domain.checkout.usecase;

import com.bullish.store.domain.checkout.api.ReceiptDto;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CheckOutServiceTest {

    private CheckOutServiceImpl checkOutService;

    @Spy
    @InjectMocks
    private CheckOutPurchaseDomainApi checkOutPurchaseDomainApi;

    @Spy
    @InjectMocks
    private CheckOutProductDomainApi checkOutProductDomainApi;

    @Mock
    private ProductManagement productManagement;

    @Mock
    private ProductShelfService productShelfService;

    @Mock
    private BasketManagement basketManagement;

    private final String CUSTOMER_ID = "x123456";
    private final String BASKET_ID = "BASKET_ID_1";
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
    private final BasketDto BASKET_1 = new BasketDto(BASKET_ID, CUSTOMER_ID, List.of(
        new LineItemDto("0", SHELF_GOOD_1.getId(), SHELF_GOOD_1.getProduct().getProductId()),
        new LineItemDto("1", SHELF_GOOD_2.getId(), SHELF_GOOD_3.getProduct().getProductId()),
        new LineItemDto("2", SHELF_GOOD_3.getId(), SHELF_GOOD_3.getProduct().getProductId())
    ));

    @BeforeEach
    void setup() {
        checkOutService = new CheckOutServiceImpl(checkOutPurchaseDomainApi, checkOutProductDomainApi);
        doReturn(Optional.of(SHELF_GOOD_1)).when(productShelfService).findGood(SHELF_GOOD_1.getId());
        doReturn(Optional.of(SHELF_GOOD_2)).when(productShelfService).findGood(SHELF_GOOD_2.getId());
        doReturn(Optional.of(SHELF_GOOD_3)).when(productShelfService).findGood(SHELF_GOOD_3.getId());

        doReturn(Optional.of(BASKET_1)).when(basketManagement).getBasket(CUSTOMER_ID);
    }

    @Test
    void given_basketContains3items_when_getReceipt_then_returnWithTotalPriceCalculated() {
        ReceiptDto receipt = checkOutService.getReceipt(CUSTOMER_ID);

        assertAll(
            () -> assertThat(receipt.getCustomerId()).isEqualTo(CUSTOMER_ID),
            () -> assertThat(receipt.getBasketId()).isEqualTo(BASKET_ID),
            () -> assertThat(receipt.getLineItemList()).hasSize(3),
            () -> assertThat(receipt.getTotalPrice()).isEqualTo(Money.of(5599.9, "HKD"))
        );
    }
}