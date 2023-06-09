package com.pizzacalculator.pizzacalculatorserverside.bussiness.repository;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.OrderDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetailsEntity, Long> {
}
