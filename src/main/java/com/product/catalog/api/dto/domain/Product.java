package com.product.catalog.api.dto.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Entity
@Table(
        name = "products",
        uniqueConstraints = @UniqueConstraint(name = "uk_product_name", columnNames = "name")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Product(String name, BigDecimal price, String currency) {
        changeName(name);
        changePrice(price);
        this.currency = currency.toUpperCase();
    }

    public void update(String name, BigDecimal price, String currency, Boolean active) {
        if (name != null) changeName(name);
        if (price != null) changePrice(price);
        if (currency != null) this.currency = currency.toUpperCase();
        if (active != null) this.active = active;
    }

    private void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        this.name = name.trim();
    }

    private void changePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Price must be >= 0");
        }
        this.price = price;
    }
}
