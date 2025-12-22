package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.menuItem.MenuItemCreateRequest;
import com.cafeshop.demo.dto.menuItem.MenuItemResponse;
import com.cafeshop.demo.mode.enums.MenuItemStatus;

import java.util.List;

public interface MenuItemService {

    MenuItemResponse create(MenuItemCreateRequest request);

    List<MenuItemResponse> findAll();

    MenuItemResponse findById(Long id);

    List<MenuItemResponse> findByStatus(MenuItemStatus status);

    void delete(Long id);
}