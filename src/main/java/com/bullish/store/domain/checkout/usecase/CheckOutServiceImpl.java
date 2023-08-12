package com.bullish.store.domain.checkout.usecase;

import com.bullish.store.domain.checkout.api.CheckOutService;
import com.bullish.store.domain.checkout.api.ReceiptDto;
import com.bullish.store.domain.checkout.port.CheckOutProductDomainApi;
import com.bullish.store.domain.checkout.port.CheckOutPurchaseDomainApi;
import com.bullish.store.domain.purchase.api.BasketDto;
import com.bullish.store.domain.purchase.api.LineItemDto;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CheckOutServiceImpl implements CheckOutService {

    private final CheckOutPurchaseDomainApi checkOutPurchaseDomainApi;
    private final CheckOutProductDomainApi checkOutProductDomainApi;

    public CheckOutServiceImpl(
        CheckOutPurchaseDomainApi checkOutPurchaseDomainApi,
        CheckOutProductDomainApi checkOutProductDomainApi
    ) {
        this.checkOutPurchaseDomainApi = checkOutPurchaseDomainApi;
        this.checkOutProductDomainApi = checkOutProductDomainApi;
    }

    @Override
    public ReceiptDto getReceipt(String customerId) {
        BasketDto basket = checkOutPurchaseDomainApi.getBasket(customerId);
        List<String> shelfGoodIdList = basket.getLineItemList().stream()
            .map(LineItemDto::getShelfId)
            .toList();

        List<ReceiptDto.LineItem> goods = checkOutProductDomainApi.findGoods(shelfGoodIdList);
        Money totalBasicPrice = goods.stream().map(ReceiptDto.LineItem::basePrice)
            .reduce(Money::add)
            .orElseThrow();
        ReceiptDto receipt = new ReceiptDto(goods, totalBasicPrice);

        // TODO: handle discounts
        return receipt;
    }
}
