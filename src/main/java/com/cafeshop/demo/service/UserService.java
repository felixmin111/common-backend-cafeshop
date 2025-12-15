package com.cafeshop.demo.service;

import com.cafeshop.demo.dto.UserDto;
import com.cafeshop.demo.mapper.UserMapper;
import com.cafeshop.demo.mode.User;
import com.cafeshop.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("user not found: " + id));
        return userMapper.toDto(user);
    }
    public UserDto create(UserDto request) {
        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    public UserDto update(Long id, UserDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("user not found: " + id));
        userMapper.updateEntityFromDto(request, user);
        return userMapper.toDto(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("user not found: " + id);
        }
        userRepository.deleteById(id);
    }

}