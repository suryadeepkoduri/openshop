package com.suryadeep.openshop.repository;

import com.suryadeep.openshop.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantRepository extends JpaRepository<Variant,Long> {
}
