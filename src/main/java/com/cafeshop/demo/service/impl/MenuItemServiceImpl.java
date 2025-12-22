package com.cafeshop.demo.service.impl;

import com.cafeshop.demo.dto.menuItem.MenuItemCreateRequest;
import com.cafeshop.demo.dto.menuItem.MenuItemResponse;
import com.cafeshop.demo.mapper.MenuItemCreateRequestMapper;
import com.cafeshop.demo.mapper.MenuItemResponseMapper;
import com.cafeshop.demo.mode.Category;
import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import com.cafeshop.demo.repository.CategoryRepository;
import com.cafeshop.demo.repository.MenuItemRepository;
import com.cafeshop.demo.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemCreateRequestMapper menuItemCreateRequestMapper;
    private final MenuItemResponseMapper menuItemResponseMapper;
    private final MenuItemRepository repository;
    private final CategoryRepository categoryRepository;


    @Override
    public MenuItemResponse create(MenuItemCreateRequest request) {

        repository.findBySku(request.getSku()).ifPresent(m -> {
            throw new RuntimeException("SKU already exists");
        });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        MenuItem menuItem = menuItemCreateRequestMapper.toEntity(request);
        menuItem.setCategory(category);

        return menuItemResponseMapper.toDto(repository.save(menuItem));
    }

    @Override
    public List<MenuItemResponse> findAll() {
        return repository.findAllWithCategory()
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
        if (!repository.existsById(id))
            throw new RuntimeException("MenuItem not found");

        repository.deleteById(id);
    }

}
