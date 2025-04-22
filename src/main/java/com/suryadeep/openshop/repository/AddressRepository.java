package com.suryadeep.openshop.repository;

import com.suryadeep.openshop.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
