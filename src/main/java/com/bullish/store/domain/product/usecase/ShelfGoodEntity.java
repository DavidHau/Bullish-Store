package com.bullish.store.domain.product.usecase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shelf_good", indexes = {
    @Index(name = "idx_shelf_good_product_id", columnList = "product_id", unique = true)
})
public class ShelfGoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", unique = true)
    private ProductEntity product;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal basePrice;
}
