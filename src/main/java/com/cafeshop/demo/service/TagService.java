package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.tag.TagRequest;
import com.cafeshop.demo.dto.tag.TagResponse;
import com.cafeshop.demo.mapper.TagMapper;
import com.cafeshop.demo.mode.Tag;
import com.cafeshop.demo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepo;
    private final TagMapper tagMapper;

    public List<TagResponse> findAll(String q) {
        List<Tag> tags = (q == null || q.isBlank())
                ? tagRepo.findAll(Sort.by("name").ascending())
                : tagRepo.findByNameContainingIgnoreCase(q);

        return tags.stream().map(tagMapper::toResponse).toList();
    }

    public TagResponse findById(Long id) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        return tagMapper.toResponse(tag);
    }

    public TagResponse create(TagRequest req) {
        String name = req.name().trim();

        tagRepo.findByNameIgnoreCase(name).ifPresent(t -> {
            throw new RuntimeException("Tag already exists");
        });

        Tag tag = tagMapper.toEntity(new TagRequest(name));
        return tagMapper.toResponse(tagRepo.save(tag));
    }

    public TagResponse update(Long id, TagRequest req) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        String name = req.name().trim();
        tagRepo.findByNameIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Tag name already exists");
            }
        });

        tagMapper.update(tag, new TagRequest(name));
        return tagMapper.toResponse(tag);
    }

    public void delete(Long id, boolean force) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        int usedCount = tag.getProducts().size();

        if (usedCount > 0 && !force) {
            throw new RuntimeException("Tag is used by products. Use force=true to remove.");
        }
        if (usedCount > 0) {
            tag.getProducts().forEach(p -> p.getTags().remove(tag));
            tag.getProducts().clear();
        }

        tagRepo.delete(tag);
    }
}
