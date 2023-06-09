package com.pizzacalculator.pizzacalculatorserverside.bussiness.repository;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.ToppingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToppingRepository extends JpaRepository<ToppingEntity, Long> {
    Optional<ToppingEntity> findByName(String name);
}
