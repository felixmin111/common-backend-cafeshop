package com.cafeshop.demo.service.impl;

import com.cafeshop.demo.dto.ingredient.IngredientCreateRequest;
import com.cafeshop.demo.dto.menuItem.MenuItemCreateRequest;
import com.cafeshop.demo.dto.menuItem.MenuItemResponse;
import com.cafeshop.demo.dto.menuitemCreateSize.MenuItemSizeCreateRequest;
import com.cafeshop.demo.mapper.IngredientMapper;
import com.cafeshop.demo.mapper.MenuItemCreateRequestMapper;
import com.cafeshop.demo.mapper.MenuItemResponseMapper;
import com.cafeshop.demo.mode.*;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import com.cafeshop.demo.repository.CategoryRepository;
import com.cafeshop.demo.repository.MenuItemRepository;
import com.cafeshop.demo.repository.SizeRepository;
import com.cafeshop.demo.repository.TagRepository;
import com.cafeshop.demo.service.MenuItemService;
import com.cafeshop.demo.service.menuItem.IngredientSynchronizer;
import com.cafeshop.demo.service.menuItem.MenuItemSizeSynchronizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemCreateRequestMapper menuItemCreateRequestMapper;
    private final MenuItemResponseMapper menuItemResponseMapper;
    private final MenuItemRepository repository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepo;

    private final MenuItemSizeSynchronizer menuItemSizeSynchronizer;
    private final IngredientSynchronizer ingredientSynchronizer;


    @Override
    public MenuItemResponse create(MenuItemCreateRequest request) {
        repository.findBySku(request.getSku()).ifPresent(m -> {
            throw new RuntimeException("SKU already exists");
        });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        MenuItem menuItem = menuItemCreateRequestMapper.toEntity(request);
        menuItem.setCategory(category);

        menuItem.setTags(resolveTags(request.getTagIds()));

        menuItem.getIngredients().clear();
        menuItem.getSizes().clear();

        // Sync children (will insert active=true)
        menuItemSizeSynchronizer.sync(menuItem, request.getSizes());
        ingredientSynchronizer.sync(menuItem, request.getIngredients());


        return menuItemResponseMapper.toDto(repository.save(menuItem));
    }

    @Override
    public List<MenuItemResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(menuItemResponseMapper::toDto)
                .toList();
    }

    @Override
    public MenuItemResponse findById(Long id) {
        return repository.findById(id)
                .map(menuItemResponseMapper::toDto)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));
    }

    @Override
    public List<MenuItemResponse> findByStatus(MenuItemStatus status) {
        return repository.findByStatus(status)
                .stream()
                .map(menuItemResponseMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        MenuItem p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        p.getTags().clear();
        repository.delete(p);
    }

    private Set<Tag> resolveTags(Set<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return new HashSet<>();
        List<Tag> tags = tagRepo.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new RuntimeException("Some tagIds are invalid");
        }
        return new HashSet<>(tags);
    }

    @Override
    public MenuItemResponse update(Long id, MenuItemCreateRequest request) {
        MenuItem existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found: " + id));

        repository.findBySku(request.getSku()).ifPresent(found -> {
            if (!found.getId().equals(id)) throw new RuntimeException("SKU already exists");
        });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        menuItemCreateRequestMapper.updateEntityFromDto(request, existing);
        existing.setCategory(category);

        existing.getTags().clear();
        existing.getTags().addAll(resolveTags(request.getTagIds()));

//        existing.getIngredients().clear();
//        existing.getSizes().clear();

        // ✅ Soft-delete + versioning sync
        menuItemSizeSynchronizer.sync(existing, request.getSizes());
        ingredientSynchronizer.sync(existing, request.getIngredients());

        return menuItemResponseMapper.toDto(repository.save(existing));
    }

}
