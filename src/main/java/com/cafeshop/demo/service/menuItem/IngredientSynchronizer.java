package com.cafeshop.demo.service.menuItem;

import com.cafeshop.demo.dto.ingredient.IngredientCreateRequest;
import com.cafeshop.demo.dto.ingredient.IngredientUpsertRequest;
import com.cafeshop.demo.mapper.IngredientMapper;
import com.cafeshop.demo.mode.Ingredient;
import com.cafeshop.demo.mode.MenuItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IngredientSynchronizer {

    private final IngredientMapper ingredientMapper;

    public void sync(MenuItem menuItem, Set<IngredientUpsertRequest> reqs) {
        Set<IngredientUpsertRequest> incoming = (reqs == null) ? Set.of() : reqs;

        Map<Long, Ingredient> activeById = menuItem.getIngredients().stream()
                .filter(i -> Boolean.TRUE.equals(i.getActive()))
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(Ingredient::getId, Function.identity()));

        Set<Long> touchedExistingIds = new HashSet<>();

        for (IngredientUpsertRequest r : incoming) {
            validate(r);

            if (r.getId() == null) {
                menuItem.getIngredients().add(createNew(menuItem, r));
                continue;
            }

            Ingredient existing = activeById.get(r.getId());
            if (existing == null) {
                throw new RuntimeException("Ingredient not found or already inactive. id=" + r.getId());
            }
            touchedExistingIds.add(existing.getId());

            if (!IngredientComparer.sameBusinessData(existing, r)) {
                existing.setActive(false);
                menuItem.getIngredients().add(createNew(menuItem, r));
            }
        }

        // Soft-delete removed from request
        menuItem.getIngredients().stream()
                .filter(i -> Boolean.TRUE.equals(i.getActive()))
                .filter(i -> i.getId() != null)
                .filter(i -> !touchedExistingIds.contains(i.getId()))
                .forEach(i -> i.setActive(false));

       System.out.println("menuItem.getIngredients().size()-->"+menuItem.getIngredients().size());
    }



    private void validate(IngredientUpsertRequest r) {
        if (r.getName() == null || r.getName().isBlank()) throw new RuntimeException("Ingredient name is required");
        // add your validation rules for amount/price if needed
    }

    private Ingredient createNew(MenuItem menuItem, IngredientUpsertRequest r) {
        IngredientCreateRequest req = new IngredientCreateRequest();
        req.setName(r.getName());
        req.setAmount(r.getAmount());
        req.setPrice(r.getPrice());
        req.setNote(r.getNote());

        Ingredient ing = ingredientMapper.toEntity(req);
        ing.setMenuItem(menuItem);
        ing.setActive(true);
        System.out.println("ing--> "+ing);
        return ing;
    }
}
