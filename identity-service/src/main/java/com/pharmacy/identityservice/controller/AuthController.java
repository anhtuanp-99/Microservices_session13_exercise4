package com.pharmacy.identityservice.controller;

import com.pharmacy.identityservice.dto.request.LoginRequest;
import com.pharmacy.identityservice.dto.request.RegisterRequest;
import com.pharmacy.identityservice.dto.response.LoginResponse;
import com.pharmacy.identityservice.dto.response.UserResponse;
import com.pharmacy.identityservice.service.AuthService;
import com.pharmacy.identityservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Endpoint công khai — từ Bài 1.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    /**
     * POST /api/auth/login
     * Xác thực username/password → sinh JWT.
     * Trả về 200 + token nếu OK, 401 nếu fail.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}