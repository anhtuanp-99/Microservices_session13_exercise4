package com.pharmacy.identityservice.service;

import com.pharmacy.identityservice.dto.request.RegisterRequest;
import com.pharmacy.identityservice.dto.response.UserResponse;
import com.pharmacy.identityservice.entity.User;
import com.pharmacy.identityservice.exception.DuplicateResourceException;
import com.pharmacy.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Đăng ký user mới:
     *   1. Check username trùng.
     *   2. Băm password bằng BCrypt.
     *   3. Lưu vào DB.
     *   4. Trả về UserResponse (KHÔNG có password).
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Bước 1: Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username '" + request.getUsername() + "' đã tồn tại!");
        }

        // Bước 2: Băm password
        String rawPassword = request.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        log.debug("Đã băm password cho user '{}' — plain length={}, hash length={}",
                request.getUsername(), rawPassword.length(), hashedPassword.length());

        // Bước 3: Lưu user
        User user = User.builder()
                .username(request.getUsername())
                .password(hashedPassword)                        // ← BĂM
                .role(request.getRole() != null ? request.getRole() : "ROLE_USER")
                .active(true)
                .build();

        User saved = userRepository.save(user);

        log.info("Đã tạo user: id={}, username={}, role={}",
                saved.getId(), saved.getUsername(), saved.getRole());

        // Bước 4: Trả về DTO (không chứa password)
        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}