package com.bullish.store.domain.checkout.port;

import com.bullish.store.domain.checkout.api.ReceiptDto;
import com.bullish.store.domain.product.api.ProductShelfService;
import com.bullish.store.domain.product.api.ShelfGoodDto;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CheckOutProductDomainApi {
    private final ProductShelfService productShelfService;

    public CheckOutProductDomainApi(
        ProductShelfService productShelfService
    ) {
        this.productShelfService = productShelfService;
    }

    public List<ReceiptDto.LineItem> findGoods(List<String> shelfGoodIds) {
        List<ShelfGoodDto> shelfGoodDtoList = shelfGoodIds.stream()
            .map(productShelfService::findGood)
            .map(Optional::orElseThrow) // potential to have other handling, e.g. check if on shelf again with new price
            .toList();

        List<ReceiptDto.LineItem> lineItemList = new ArrayList<>();
        for (int i = 0; i < shelfGoodDtoList.size(); i++) {
            ShelfGoodDto good = shelfGoodDtoList.get(i);
            lineItemList.add(new ReceiptDto.LineItem(
                i,
                good.getId(),
                good.getProduct().getProductId(),
                good.getProduct().getName(),
                Money.of(good.getBasePrice(), good.getCurrency())
            ));
        }
        return lineItemList;
    }
}
