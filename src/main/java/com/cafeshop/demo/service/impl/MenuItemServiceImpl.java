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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemCreateRequestMapper menuItemCreateRequestMapper;
    private final MenuItemResponseMapper menuItemResponseMapper;
    private final IngredientMapper ingredientMapper;
    private final MenuItemRepository repository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepo;
    private final SizeRepository sizeRepository;


    @Override
    public MenuItemResponse create(MenuItemCreateRequest request) {

        repository.findBySku(request.getSku()).ifPresent(m -> {
            throw new RuntimeException("SKU already exists");
        });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        MenuItem menuItem = menuItemCreateRequestMapper.toEntity(request);
        menuItem.setCategory(category);

        menuItem.getSizes().clear();
        menuItem.getSizes().addAll(buildMenuItemSizes(menuItem, request.getSizes()));

        menuItem.setTags(resolveTags(request.getTagIds()));

        menuItem.getIngredients().clear();
        menuItem.getIngredients().addAll(buildIngredients(menuItem, request.getIngredients()));


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
            if (!found.getId().equals(id)) {
                throw new RuntimeException("SKU already exists");
            }
        });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        menuItemCreateRequestMapper.updateEntityFromDto(request,existing);

        existing.setCategory(category);

        existing.getTags().clear();
        existing.getTags().addAll(resolveTags(request.getTagIds()));

        existing.getSizes().clear();
        existing.getSizes().addAll(buildMenuItemSizes(existing, request.getSizes()));

        existing.getIngredients().clear();
        existing.getIngredients().addAll(buildIngredients(existing, request.getIngredients()));

        MenuItem saved = repository.save(existing);
        return menuItemResponseMapper.toDto(saved);
    }

    private Set<MenuItemSize> buildMenuItemSizes(MenuItem menuItem, Set<MenuItemSizeCreateRequest> sizeDtos) {
        if (sizeDtos == null || sizeDtos.isEmpty()) return new HashSet<>();

        // load all sizes once
        Set<Long> sizeIds = sizeDtos.stream().map(MenuItemSizeCreateRequest::getSizeId).collect(java.util.stream.Collectors.toSet());
        List<Size> sizes = sizeRepository.findAllById(sizeIds);
        if (sizes.size() != sizeIds.size()) {
            throw new RuntimeException("Some sizeIds are invalid");
        }
        var sizeMap = sizes.stream().collect(java.util.stream.Collectors.toMap(Size::getId, s -> s));

        Set<MenuItemSize> result = new HashSet<>();
        for (MenuItemSizeCreateRequest dto : sizeDtos) {
            Size size = sizeMap.get(dto.getSizeId());

            if (dto.getSellPrice() == null) {
                throw new RuntimeException("sellPrice is required for sizeId=" + dto.getSizeId());
            }

            result.add(MenuItemSize.builder()
                    .menuItem(menuItem)
                    .size(size)
                    .originalPrice(dto.getOriginalPrice())
                    .sellPrice(dto.getSellPrice())
                    .description(dto.getDesc())
                    .build());
        }
        return result;
    }

    private Set<Ingredient> buildIngredients(
            MenuItem menuItem,
            Set<IngredientCreateRequest> ingredientDtos
    ) {
        if (ingredientDtos == null || ingredientDtos.isEmpty()) return new HashSet<>();

        Set<Ingredient> result = new HashSet<>();
        for (var dto : ingredientDtos) {
            Ingredient ing = ingredientMapper.toEntity(dto);
            ing.setMenuItem(menuItem);
            result.add(ing);
        }
        return result;
    }


}
