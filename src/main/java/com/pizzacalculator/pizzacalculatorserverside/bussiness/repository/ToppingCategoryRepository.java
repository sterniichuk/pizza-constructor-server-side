package com.pizzacalculator.pizzacalculatorserverside.bussiness.repository;

import com.pizzacalculator.pizzacalculatorserverside.bussiness.model.entity.ToppingCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToppingCategoryRepository extends JpaRepository<ToppingCategoryEntity, Long> {
    Optional<ToppingCategoryEntity> findByNameIgnoreCase(String name);
}
