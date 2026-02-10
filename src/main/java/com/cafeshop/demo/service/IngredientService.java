package com.cafeshop.demo.service;

import com.cafeshop.demo.mode.Ingredient;
import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.repository.IngredientRepository;
import com.cafeshop.demo.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final MenuItemRepository menuItemRepository;

    @Transactional
    public Ingredient createIngredient(Long menuItemId, Ingredient ingredient) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("MenuItem not found: " + menuItemId));

        ingredient.setMenuItem(menuItem);

        return ingredientRepository.save(ingredient);
    }
}

