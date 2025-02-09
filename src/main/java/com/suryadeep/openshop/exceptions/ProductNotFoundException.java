package com.suryadeep.openshop.exceptions;

import com.suryadeep.openshop.model.Product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super("Product not found");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(long productId) {
        super("Product with id " + productId + " not found");
    }
}
