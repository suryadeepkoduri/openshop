package com.suryadeep.openshop.repository;

import com.suryadeep.openshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository <OrderItem, Long> {

}
