package com.pharmacy.identityservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_username", columnNames = "username")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /**
     * Mật khẩu đã được băm bằng BCrypt — KHÔNG BAO GIỜ lưu plaintext.
     * Độ dài 60 ký tự do BCrypt sinh ra.
     */
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String role;      // ROLE_USER, ROLE_ADMIN, ROLE_PHARMACIST

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (active == null) active = true;
        if (role == null) role = "ROLE_USER";
    }
}