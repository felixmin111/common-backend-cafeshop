package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.mode.enums.MenuItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    Optional<MenuItem> findBySku(String sku);

    List<MenuItem> findByStatus(MenuItemStatus status);

    @Query("select mi from MenuItem mi join fetch mi.category")
    List<MenuItem> findAllWithCategory();
}