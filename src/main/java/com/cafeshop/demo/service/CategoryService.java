package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.category.CategoryDto;
import com.cafeshop.demo.dto.category.CategoryRequestDto;
import com.cafeshop.demo.mapper.CategoryMapper;
import com.cafeshop.demo.mode.Category;
import com.cafeshop.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repo;
    private final CategoryMapper mapper;

    @Transactional
    public CategoryDto create(CategoryRequestDto req) {
        String name = req.name().trim();

        if (repo.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category name already exists: " + name);
        }

        Category entity = mapper.toEntity(req);

        // defaults
        if (entity.getActive() == null) entity.setActive(true);

        // computed fields
        entity.setName(name);
        entity.setSlug(toSlug(name));

        Category saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAll() {
        return repo.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional
    public CategoryDto update(Long id, CategoryRequestDto req) {
        Category entity = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));

        String name = req.name().trim();

        if (repo.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new IllegalArgumentException("Category name already exists: " + name);
        }

        // apply dto fields
        mapper.updateEntity(req, entity);

        // ensure name trimmed + slug updated
        entity.setName(name);
        entity.setSlug(toSlug(name));

        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        Category entity = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));

        long count = entity.getMenuItems() == null ? 0 : entity.getMenuItems().size();
        if (count > 0) {
            throw new IllegalStateException("Cannot delete category. It has " + count + " menu items.");
        }

        repo.delete(entity);
    }

    private String toSlug(String input) {
        return input.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-");
    }
}
