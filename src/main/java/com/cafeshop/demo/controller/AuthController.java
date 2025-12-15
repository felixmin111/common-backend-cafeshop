package com.cafeshop.demo.controller;

import com.cafeshop.demo.dto.LoginDto;
import com.cafeshop.demo.dto.UserDto;
import com.cafeshop.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserDto request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginDto request) {
        UserDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
