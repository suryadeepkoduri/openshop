package com.suryadeep.openshop.repository;

import com.suryadeep.openshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
