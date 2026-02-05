package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {}
