package com.suryadeep.openshop.repository;

import com.suryadeep.openshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
