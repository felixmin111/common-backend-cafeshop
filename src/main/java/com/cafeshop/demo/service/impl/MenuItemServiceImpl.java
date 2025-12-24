package com.cafeshop.demo.service.impl;

import com.cafeshop.demo.dto.menuItem.MenuItemCreateRequest;
import com.cafeshop.demo.dto.menuItem.MenuItemResponse;
import com.cafeshop.demo.mapper.MenuItemCreateRequestMapper;
import com.cafeshop.demo.mapper.MenuItemResponseMapper;
import com.cafeshop.demo.mode.Category;
import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.mode.Tag;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import com.cafeshop.demo.repository.CategoryRepository;
import com.cafeshop.demo.repository.MenuItemRepository;
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
    private final MenuItemRepository repository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepo;


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

        // ✅ SKU unique check (only if changed)
        repository.findBySku(request.getSku()).ifPresent(found -> {
            if (!found.getId().equals(id)) {
                throw new RuntimeException("SKU already exists");
            }
        });

        // ✅ update category if provided
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        // ✅ map request -> existing entity (exclude category/tags in mapper)
        // If your mapper has updateEntity(@MappingTarget MenuItem, MenuItemCreateRequest)
        menuItemCreateRequestMapper.updateEntityFromDto(request,existing);

        existing.setCategory(category);

        // ✅ replace tags (many-to-many)
        existing.getTags().clear();
        existing.getTags().addAll(resolveTags(request.getTagIds()));

        // save
        MenuItem saved = repository.save(existing);
        return menuItemResponseMapper.toDto(saved);
    }


}
