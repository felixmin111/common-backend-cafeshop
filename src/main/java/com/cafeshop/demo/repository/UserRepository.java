package com.cafeshop.demo.repository;

import com.cafeshop.demo.mode.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
