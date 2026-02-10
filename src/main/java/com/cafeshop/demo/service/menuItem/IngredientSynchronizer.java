package com.cafeshop.demo.service.menuItem;

import com.cafeshop.demo.dto.ingredient.IngredientCreateRequest;
import com.cafeshop.demo.dto.ingredient.IngredientUpsertRequest;
import com.cafeshop.demo.mapper.IngredientMapper;
import com.cafeshop.demo.mode.Ingredient;
import com.cafeshop.demo.mode.MenuItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IngredientSynchronizer {

    private final IngredientMapper ingredientMapper;

    public void sync(MenuItem menuItem, Set<IngredientUpsertRequest> reqs) {
        Set<IngredientUpsertRequest> incoming = (reqs == null) ? Set.of() : reqs;

        // 1) Index existing by ID (active + inactive)
        Map<Long, Ingredient> byId = menuItem.getIngredients().stream()
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(
                        Ingredient::getId,
                        Function.identity(),
                        (a, b) -> a
                ));

        // 2) Index existing by business key: name|amount|price (active + inactive)
        Map<String, Ingredient> byKey = menuItem.getIngredients().stream()
                .collect(Collectors.toMap(
                        this::businessKey,
                        Function.identity(),
                        (a, b) -> a
                ));

        Set<Long> touchedIds = new HashSet<>();
        Set<String> touchedKeys = new HashSet<>();

        for (IngredientUpsertRequest r : incoming) {
            validate(r);

            // Case A: request has ID -> update that record if exists, else create new
            if (r.getId() != null) {
                Ingredient existing = byId.get(r.getId());
                if (existing == null) {
                    // id not found in this menuItem -> create new
                    Ingredient created = createNew(menuItem, r);
                    menuItem.getIngredients().add(created);
                    touchedKeys.add(businessKey(created));
                    continue;
                }

                // reactivate + update all fields
                existing.setActive(true);
                existing.setName(r.getName());
                existing.setAmount(r.getAmount());
                existing.setPrice(r.getPrice());
                existing.setNote(r.getNote());

                touchedIds.add(existing.getId());
                touchedKeys.add(businessKey(existing));
                continue;
            }

            // Case B: no ID -> try match by business key (name+amount+price)
            String key = businessKey(r);
            Ingredient matched = byKey.get(key);

            if (matched != null) {
                // reuse old object
                matched.setActive(true);
                matched.setNote(r.getNote()); // only note update (as you said)
                // (optional) you can also normalize name/amount/price if you want

                if (matched.getId() != null) touchedIds.add(matched.getId());
                touchedKeys.add(key);
            } else {
                Ingredient created = createNew(menuItem, r);
                menuItem.getIngredients().add(created);
                touchedKeys.add(businessKey(created));
            }
        }

        // Soft-delete: items not present in request
        menuItem.getIngredients().stream()
                .filter(i -> Boolean.TRUE.equals(i.getActive()))
                .filter(i -> {
                    // if it has id, check touchedIds; else check touchedKeys
                    if (i.getId() != null) return !touchedIds.contains(i.getId());
                    return !touchedKeys.contains(businessKey(i));
                })
                .forEach(i -> i.setActive(false));
    }

    private String businessKey(Ingredient i) {
        return norm(i.getName()) + "|" + norm(i.getAmount()) + "|" + normMoney(i.getPrice());
    }

    private String businessKey(IngredientUpsertRequest r) {
        return norm(r.getName()) + "|" + norm(r.getAmount()) + "|" + normMoney(r.getPrice());
    }

    private String norm(String s) {
        return (s == null) ? "" : s.trim().toLowerCase();
    }

    private String normMoney(BigDecimal v) {
        if (v == null) return "";
        // Normalize 6.0 vs 6.00
        return v.stripTrailingZeros().toPlainString();
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
