package com.pizzacalculator.pizzacalculatorserverside.bussiness.repository;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEntityRepository  extends JpaRepository<OrderEntity, Long> {
}
