package com.suryadeep.openshop.repository;

import com.suryadeep.openshop.entity.Order;
import com.suryadeep.openshop.entity.User;
import com.suryadeep.openshop.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository <Order, Long> {


    Arrays findByUser(User user);

    
    List<Order> findByUserId(Long id);

    List<Order> findByUserAndStatus(User user, OrderStatus status);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

}
