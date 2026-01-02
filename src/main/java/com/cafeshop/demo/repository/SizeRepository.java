package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findByNameIgnoreCase(String name);
}
