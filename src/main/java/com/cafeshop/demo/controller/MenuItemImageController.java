package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.menuItemImage.MenuItemImageDto;
import com.cafeshop.demo.mapper.MenuItemImageMapper;
import com.cafeshop.demo.service.MenuItemImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menuItems")
public class MenuItemImageController {

    private final MenuItemImageService service;
    private final MenuItemImageMapper mapper;

    @PostMapping(value = "/{menuItemId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<MenuItemImageDto> upload(
            @PathVariable Long menuItemId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(required = false) Integer primaryIndex
    ) {
        return service.uploadMany(menuItemId, files, primaryIndex);
    }

    @PutMapping(value = "/{menuItemId}/images/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MenuItemImageDto replaceFile(
            @PathVariable Long menuItemId,
            @PathVariable Long imageId,
            @RequestPart("file") MultipartFile file
    ) {
        return service.replaceFile(menuItemId, imageId, file);
    }


    @DeleteMapping("/{menuItemId}/images/{imageId}")
    public void delete(@PathVariable Long menuItemId, @PathVariable Long imageId) {
        service.delete(menuItemId, imageId);
    }

    @PatchMapping("/{menuItemId}/images/{imageId}/primary")
    public MenuItemImageDto setPrimary(
            @PathVariable Long menuItemId,
            @PathVariable Long imageId
    ) {
        return service.setPrimary(menuItemId, imageId);
    }
}
