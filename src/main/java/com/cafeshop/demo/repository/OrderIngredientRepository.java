package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.OrderIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderIngredientRepository extends JpaRepository<OrderIngredient, Long> {}
