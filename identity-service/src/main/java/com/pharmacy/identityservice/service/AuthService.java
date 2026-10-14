package com.pharmacy.identityservice.service;

import com.pharmacy.identityservice.dto.request.LoginRequest;
import com.pharmacy.identityservice.dto.response.LoginResponse;
import com.pharmacy.identityservice.entity.User;
import com.pharmacy.identityservice.exception.BadCredentialsException;
import com.pharmacy.identityservice.repository.UserRepository;
import com.pharmacy.identityservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.jwt.expiration}")
    private long expirationMs;

    /**
     * Xác thực username/password và sinh JWT.
     * <p>
     * NGUYÊN TẮC BẢO MẬT:
     *   - Không dùng equals() để so password — phải dùng matches() của BCrypt.
     *   - Message lỗi LUÔN giống nhau → không tiết lộ username hay password sai.
     *   - Log chi tiết ở server — client chỉ nhận "Bad credentials".
     */
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String rawPassword = request.getPassword();

        // Bước 1: Tìm user (không phân biệt được lỗi nào với client)
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            log.warn("Login FAIL — username không tồn tại: {}", username);
            throw new BadCredentialsException();
        }

        User user = userOpt.get();

        // Bước 2: Verify password bằng BCrypt.matches()
        //   matches(raw, encoded) tự hash lại raw + so sánh với encoded
        //   KHÔNG dùng equals(raw, encoded) vì BCrypt có salt ngẫu nhiên
        boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword());

        if (!passwordMatches) {
            log.warn("Login FAIL — password sai cho username: {}", username);
            throw new BadCredentialsException();
        }

        // Bước 3: Kiểm tra user còn active không
        if (!Boolean.TRUE.equals(user.getActive())) {
            log.warn("Login FAIL — user bị vô hiệu hóa: {}", username);
            throw new BadCredentialsException();
        }

        // Bước 4: Sinh JWT
        String token = jwtUtil.generateToken(user);

        log.info("Login OK — username={}, role={}", username, user.getRole());

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(expirationMs / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}