package com.suryadeep.openshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "carts")
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_sequence")
    @SequenceGenerator(name = "cart_sequence", sequenceName = "cart_seq", initialValue = 1, allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Transient
    private BigDecimal price;

    @OneToOne(mappedBy = "cart")
    private User user;

}
