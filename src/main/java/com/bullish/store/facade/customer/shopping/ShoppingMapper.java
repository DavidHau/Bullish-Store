package com.bullish.store.facade.customer.shopping;

import com.bullish.store.domain.checkout.api.ReceiptDto;
import org.javamoney.moneta.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ShoppingMapper {

    @Mapping(target = "currency", source = "totalBasePrice.currency")
    BasketReceiptDto receiptDtoToBasketReceiptDto(ReceiptDto entity);

    default BigDecimal toBigDecimal(Money money) {
        if (money == null) {
            return null;
        }
        return money.getNumberStripped();
    }

    default String toString(CurrencyUnit currencyUnit) {
        return currencyUnit.getCurrencyCode();
    }

}
