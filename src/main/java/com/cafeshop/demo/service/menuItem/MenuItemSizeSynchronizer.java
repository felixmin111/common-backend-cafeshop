package com.cafeshop.demo.service.menuItem;

import com.cafeshop.demo.dto.menuItem.MenuItemSizeUpsertRequest;
import com.cafeshop.demo.mode.MenuItem;
import com.cafeshop.demo.mode.MenuItemSize;
import com.cafeshop.demo.mode.Size;
import com.cafeshop.demo.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MenuItemSizeSynchronizer {

    private final SizeRepository sizeRepository;

    public void sync(MenuItem menuItem, Set<MenuItemSizeUpsertRequest> reqs) {
        Set<MenuItemSizeUpsertRequest> incoming = (reqs == null) ? Set.of() : reqs;

        // Map existing by sizeId (IMPORTANT: include inactive too, because it might be re-activated)
        Map<Long, MenuItemSize> bySizeId = menuItem.getSizes().stream()
                .collect(Collectors.toMap(
                        mis -> mis.getSize().getId(),
                        Function.identity(),
                        (a, b) -> a // just in case duplicates exist in memory
                ));

        Set<Long> incomingSizeIds = new HashSet<>();

        for (MenuItemSizeUpsertRequest r : incoming) {
            if (r.getSizeId() == null) throw new RuntimeException("sizeId is required");
            if (r.getSellPrice() == null) throw new RuntimeException("sellPrice is required for sizeId=" + r.getSizeId());

            Long sizeId = r.getSizeId();
            if (!incomingSizeIds.add(sizeId)) {
                throw new RuntimeException("Duplicate sizeId in request: " + sizeId);
            }

            MenuItemSize existing = bySizeId.get(sizeId);

            if (existing != null) {
                existing.setOriginalPrice(r.getOriginalPrice());
                existing.setSellPrice(r.getSellPrice());
                existing.setDescription(r.getDesc());
                existing.setActive(true);
            } else {
                Size size = sizeRepository.findById(sizeId)
                        .orElseThrow(() -> new RuntimeException("Size not found: " + sizeId));

                MenuItemSize newMis = new MenuItemSize();
                newMis.setMenuItem(menuItem);
                newMis.setSize(size);
                newMis.setOriginalPrice(r.getOriginalPrice());
                newMis.setSellPrice(r.getSellPrice());
                newMis.setDescription(r.getDesc());
                newMis.setActive(true);
                menuItem.getSizes().add(newMis);
            }
        }

        menuItem.getSizes().forEach(mis -> {
            Long sizeId = mis.getSize().getId();
            if (!incomingSizeIds.contains(sizeId)) {
                mis.setActive(false);
            }
        });
    }

    private void validate(MenuItemSizeUpsertRequest r) {
        if (r.getSizeId() == null) throw new RuntimeException("sizeId is required");
        if (r.getSellPrice() == null) throw new RuntimeException("sellPrice is required for sizeId=" + r.getSizeId());
    }

    private MenuItemSize createNew(MenuItem menuItem, MenuItemSizeUpsertRequest r) {
        Size size = sizeRepository.findById(r.getSizeId())
                .orElseThrow(() -> new RuntimeException("Size not found: " + r.getSizeId()));

        return MenuItemSize.builder()
                .menuItem(menuItem)
                .size(size)
                .originalPrice(r.getOriginalPrice())
                .sellPrice(r.getSellPrice())
                .description(r.getDesc())
                .active(true)
                .build();
    }
}

