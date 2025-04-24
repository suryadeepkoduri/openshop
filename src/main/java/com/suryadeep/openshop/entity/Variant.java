package com.suryadeep.openshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Variant {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "variant_sequence")
    @SequenceGenerator(name = "variant_sequence", sequenceName = "variant_seq", initialValue = 1, allocationSize = 1)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
