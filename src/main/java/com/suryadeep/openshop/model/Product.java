package com.suryadeep.openshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private long productId;
    private String productName;
    private String productDescription;
    private String productImage;
    private double productPrice;
}
