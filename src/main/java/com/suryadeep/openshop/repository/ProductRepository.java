package com.suryadeep.openshop.repository;

import com.suryadeep.openshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
