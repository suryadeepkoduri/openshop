package com.suryadeep.openshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_sequence")
    @SequenceGenerator(name = "cart_item_sequence", sequenceName = "cart_item_seq", initialValue = 1, allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant variant;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Transient
    private BigDecimal price;
}
