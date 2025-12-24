package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.tag.TagRequest;
import com.cafeshop.demo.dto.tag.TagResponse;
import com.cafeshop.demo.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService service;

    @GetMapping
    public List<TagResponse> findAll(@RequestParam(required = false) String q) {
        return service.findAll(q);
    }

    @GetMapping("/{id}")
    public TagResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public TagResponse create(@RequestBody TagRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public TagResponse update(@PathVariable Long id, @RequestBody TagRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force
    ) {
        service.delete(id, force);
    }
}

