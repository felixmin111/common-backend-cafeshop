package com.cafeshop.demo.mode;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 20, nullable = false, unique = true)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 50, nullable = false)
    private String role;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(name = "hashpassword", nullable = false)
    private String hashPassword;
}

