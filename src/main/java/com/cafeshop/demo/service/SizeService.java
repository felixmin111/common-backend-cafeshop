package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.size.SizeRequest;
import com.cafeshop.demo.dto.size.SizeResponse;

import java.util.List;

public interface SizeService {
    SizeResponse create(SizeRequest req);
    List<SizeResponse> findAll(Boolean active);
    SizeResponse findById(Long id);
    SizeResponse update(Long id, SizeRequest req);
    void delete(Long id);
}
