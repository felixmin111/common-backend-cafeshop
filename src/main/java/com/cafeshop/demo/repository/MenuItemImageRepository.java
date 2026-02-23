package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.MenuItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemImageRepository extends JpaRepository<MenuItemImage, Long> {
    List<MenuItemImage> findByMenuItemIdAndActiveTrue(Long menuItemId);
}