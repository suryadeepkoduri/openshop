package com.suryadeep.openshop.repository;

import com.suryadeep.openshop.dto.response.ProductResponse;
import com.suryadeep.openshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByCategoryId(Long categoryId);
    Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);
}
