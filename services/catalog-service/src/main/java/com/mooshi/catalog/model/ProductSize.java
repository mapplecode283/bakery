package com.mooshi.catalog.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "product_sizes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductSize {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "price_multiplier", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal priceMultiplier = BigDecimal.ONE;
}
