package com.bullish.store.domain.checkout.usecase;

import com.bullish.store.domain.adjustment.api.DiscountRatioDto;
import com.bullish.store.domain.checkout.api.CheckOutService;
import com.bullish.store.domain.checkout.api.ReceiptDto;
import com.bullish.store.domain.checkout.port.CheckOutAdjustmentDomainApi;
import com.bullish.store.domain.checkout.port.CheckOutProductDomainApi;
import com.bullish.store.domain.checkout.port.CheckOutPurchaseDomainApi;
import com.bullish.store.domain.purchase.api.BasketDto;
import com.bullish.store.domain.purchase.api.LineItemDto;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class CheckOutServiceImpl implements CheckOutService {

    private final CheckOutPurchaseDomainApi checkOutPurchaseDomainApi;
    private final CheckOutProductDomainApi checkOutProductDomainApi;
    private final CheckOutAdjustmentDomainApi checkOutAdjustmentDomainApi;

    public CheckOutServiceImpl(
        CheckOutPurchaseDomainApi checkOutPurchaseDomainApi,
        CheckOutProductDomainApi checkOutProductDomainApi,
        CheckOutAdjustmentDomainApi checkOutAdjustmentDomainApi
    ) {
        this.checkOutPurchaseDomainApi = checkOutPurchaseDomainApi;
        this.checkOutProductDomainApi = checkOutProductDomainApi;
        this.checkOutAdjustmentDomainApi = checkOutAdjustmentDomainApi;
    }

    @Override
    public ReceiptDto getReceipt(String customerId) {
        BasketDto basket = checkOutPurchaseDomainApi.getBasket(customerId);
        List<String> shelfGoodIdList = basket.getLineItemList().stream()
            .map(LineItemDto::getShelfId)
            .toList();

        List<ReceiptDto.LineItem> goods = checkOutProductDomainApi.findGoods(shelfGoodIdList);

        applyAllAutoApplyDiscount(goods);

        final Money totalBasicPrice = goods.stream().map(ReceiptDto.LineItem::getBasePrice)
            .reduce(Money::add)
            .orElseThrow();

        final Money totalDiscountedAmount = goods.stream().map(ReceiptDto.LineItem::getDiscountedAmount)
            .filter(Objects::nonNull)
            .reduce(Money::add)
            .orElse(Money.zero(totalBasicPrice.getCurrency()));

        final Money totalPrice = totalBasicPrice.subtract(totalDiscountedAmount);

        ReceiptDto receipt = ReceiptDto.builder()
            .customerId(basket.getCustomerId())
            .basketId(basket.getId())
            .lineItemList(goods)
            .totalBasePrice(totalBasicPrice)
            .totalDiscount(totalDiscountedAmount)
            .totalPrice(totalPrice)
            .build();

        return receipt;
    }

    private void applyAllAutoApplyDiscount(List<ReceiptDto.LineItem> goods) {
        List<DiscountRatioDto> allAutoApplyRatioDiscount = checkOutAdjustmentDomainApi.getAllAutoApplyRatioDiscount();

        for (DiscountRatioDto ratioDiscount : allAutoApplyRatioDiscount) {
            Map<String, Integer> theNthMatchItemMap = new HashMap<>();

            for (ReceiptDto.LineItem good : goods) {
                boolean isDiscountApplicable = ratioDiscount.isApplyToAllProduct();
                if (!isDiscountApplicable) {
                    continue;
                }
                final String matchCountKey = good.getShelfId();
                final int originalMatchCount = theNthMatchItemMap.getOrDefault(matchCountKey, 0);
                final int currentMatchCount = originalMatchCount + 1;
                theNthMatchItemMap.put(matchCountKey, currentMatchCount);
                if (ratioDiscount.getApplyAtEveryNthNumberOfItem() == currentMatchCount) {
                    applyDiscount(good, ratioDiscount);
                    theNthMatchItemMap.put(matchCountKey, 0);
                }
            }

        }
    }

    private void applyDiscount(ReceiptDto.LineItem good, DiscountRatioDto ratioDiscount) {
        Money discountAmount = good.getBasePrice().multiply(ratioDiscount.getOffRatio());
        good.setDiscountName(ratioDiscount.getName());
        good.setDiscountedAmount(discountAmount);
    }
}
