package com.cafeshop.demo.service.impl;

import com.cafeshop.demo.dto.size.SizeRequest;
import com.cafeshop.demo.dto.size.SizeResponse;
import com.cafeshop.demo.mode.Size;
import com.cafeshop.demo.repository.SizeRepository;
import com.cafeshop.demo.service.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository repo;

    @Override
    public SizeResponse create(SizeRequest req) {
        repo.findByNameIgnoreCase(req.name()).ifPresent(x -> {
            throw new RuntimeException("Size name already exists");
        });

        Size s = new Size();
        s.setName(req.name().trim());
        s.setShortName(req.shortName());
        s.setActive(req.active() != null ? req.active() : true);

        return toDto(repo.save(s));
    }

    @Override
    public List<SizeResponse> findAll(Boolean active) {
        return repo.findAll().stream()
                .filter(s -> active == null || Boolean.TRUE.equals(s.getActive()) == active)
                .map(this::toDto)
                .toList();
    }

    @Override
    public SizeResponse findById(Long id) {
        Size s = repo.findById(id).orElseThrow(() -> new RuntimeException("Size not found"));
        return toDto(s);
    }

    @Override
    public SizeResponse update(Long id, SizeRequest req) {
        Size s = repo.findById(id).orElseThrow(() -> new RuntimeException("Size not found"));

        repo.findByNameIgnoreCase(req.name()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Size name already exists");
            }
        });

        s.setName(req.name().trim());
        s.setShortName(req.shortName());

        if (req.active() != null) s.setActive(req.active());
        repo.save(s);
        return toDto(s);
    }

    @Override
    public void delete(Long id) {
        Size s = repo.findById(id).orElseThrow(() -> new RuntimeException("Size not found"));
        repo.delete(s);
    }

    private SizeResponse toDto(Size s) {
        return new SizeResponse(s.getId(), s.getName(), s.getShortName(), s.getActive());
    }
}
