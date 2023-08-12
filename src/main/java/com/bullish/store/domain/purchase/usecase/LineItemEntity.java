package com.bullish.store.domain.purchase.usecase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "line_item", indexes = {
    @Index(name = "idx_line_item_basket_id", columnList = "basket_id", unique = false)
})
/**
 * Aggregate of Basket
 */
public class LineItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "basket_id", referencedColumnName = "id")
    private BasketEntity basket;

    @Column(nullable = false)
    private String shelfGoodId;

    @Column(nullable = false)
    private String productId;
}
