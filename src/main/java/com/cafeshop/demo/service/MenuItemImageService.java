package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.menuItemImage.MenuItemImageDto;
import com.cafeshop.demo.mapper.MenuItemImageMapper;
import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.mode.MenuItemImage;
import com.cafeshop.demo.repository.MenuItemImageRepository;
import com.cafeshop.demo.repository.MenuItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuItemImageService {
    private final MenuItemImageRepository repo;
    private final MenuItemRepository menuItemRepo;
    private final MenuItemImageMapper mapper;
    private final S3StorageService storage;

    public List<MenuItemImageDto> uploadMany(Long menuItemId, List<MultipartFile> files, Integer primaryIndex) {
        MenuItem menuItem = menuItemRepo.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found"));

        if (files == null || files.isEmpty()) return List.of();

        return java.util.stream.IntStream.range(0, files.size())
                .mapToObj(i -> {
                    MultipartFile f = files.get(i);
                    if (f == null || f.isEmpty()) return null;

                    // ✅ upload returns key + url
                    S3StorageService.UploadResult uploaded = storage.uploadWithKey(f, menuItemId);

                    MenuItemImage entity = MenuItemImage.builder()
                            .menuItem(menuItem)
                            .s3Key(uploaded.key())                 // ✅ FIX
                            .url(uploaded.url())                   // ✅ FIX
                            .contentType(f.getContentType())
                            .sizeBytes(f.getSize())
                            .primary(primaryIndex != null && primaryIndex == i)
                            .active(true)
                            .build();

                    return mapper.toDto(repo.save(entity));
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
    public List<MenuItemImageDto> getAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public MenuItemImageDto setPrimary(Long menuItemId, Long imageId) {

        // verify image belongs to menuItem
        MenuItemImage target = repo.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        if (!target.getMenuItem().getId().equals(menuItemId)) {
            throw new IllegalArgumentException("Image not in this menu item");
        }

        // set all images primary=false for this menuItem
        List<MenuItemImage> images = repo.findByMenuItemIdAndActiveTrue(menuItemId);
        for (MenuItemImage img : images) {
            img.setPrimary(img.getId().equals(imageId));
        }

        // save all (or rely on transactional dirty checking)
        repo.saveAll(images);

        return mapper.toDto(target);
    }

    @Transactional
    public MenuItemImageDto replaceFile(Long menuItemId, Long imageId, MultipartFile newFile) {

        if (newFile == null || newFile.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (newFile.getContentType() == null || !newFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files allowed");
        }

        MenuItemImage img = repo.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        if (!img.getMenuItem().getId().equals(menuItemId)) {
            throw new IllegalArgumentException("Image not in this menu item");
        }

        // keep old key to delete after successful upload
        String oldKey = img.getS3Key();

        // upload new file (recommended storage returns UploadResult {key,url})
        S3StorageService.UploadResult uploaded = storage.uploadWithKey(newFile, menuItemId);

        // update db record
        img.setS3Key(uploaded.key());
        img.setUrl(uploaded.url());
        img.setContentType(newFile.getContentType());
        img.setSizeBytes(newFile.getSize());

        MenuItemImage saved = repo.save(img);

        // delete old object from S3 (after db update)
        if (oldKey != null && !oldKey.isBlank()) {
            storage.delete(oldKey);
        }

        return mapper.toDto(saved);
    }
    @Transactional
    public void delete(Long menuItemId, Long imageId) {
        MenuItemImage img = repo.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        if (!img.getMenuItem().getId().equals(menuItemId)) {
            throw new IllegalArgumentException("Image not in this menu item");
        }

        img.setActive(false);
        repo.save(img);

        if (img.getS3Key() != null && !img.getS3Key().isBlank()) {
            storage.delete(img.getS3Key());
        }
    }
}