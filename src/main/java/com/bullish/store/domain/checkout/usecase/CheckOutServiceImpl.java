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

import java.util.*;

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
    public Optional<ReceiptDto> getReceipt(String customerId) {
        Optional<BasketDto> basketOptional = checkOutPurchaseDomainApi.getBasket(customerId);
        if (basketOptional.isEmpty()) {
            return Optional.empty();
        }
        BasketDto basket = basketOptional.get();

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

        final Money totalPrice = totalBasicPrice.add(totalDiscountedAmount);

        ReceiptDto receipt = ReceiptDto.builder()
            .customerId(basket.getCustomerId())
            .basketId(basket.getId())
            .lineItemList(goods)
            .totalBasePrice(totalBasicPrice)
            .totalDiscount(totalDiscountedAmount)
            .totalPrice(totalPrice)
            .build();

        return Optional.of(receipt);
    }

    private void applyAllAutoApplyDiscount(List<ReceiptDto.LineItem> goods) {
        List<DiscountRatioDto> allAutoApplyRatioDiscount = checkOutAdjustmentDomainApi.getAllAutoApplyRatioDiscount();

        for (DiscountRatioDto ratioDiscount : allAutoApplyRatioDiscount) {
            Map<String, Integer> theNthMatchItemMap = new HashMap<>();

            for (ReceiptDto.LineItem good : goods) {
                boolean isDiscountApplicable =
                    ratioDiscount.isApplyToAllProduct() || good.getShelfId().equals(ratioDiscount.getShelfGoodId());
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
        Money discountAmount = good.getBasePrice().multiply(ratioDiscount.getOffRatio()).negate();
        good.setDiscountName(ratioDiscount.getName());
        good.setDiscountedAmount(discountAmount);
    }
}
