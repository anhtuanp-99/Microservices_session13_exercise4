package com.pharmacy.identityservice.util;

import com.pharmacy.identityservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expirationMs) {

        // Chuyển String secret thành SecretKey cho HS256
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret phải tối thiểu 32 bytes (256-bit) cho HS256. " +
                            "Hiện tại: " + keyBytes.length + " bytes");
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;

        log.info("JwtUtil khởi tạo thành công — secret length: {} bytes, expiration: {}ms",
                keyBytes.length, expirationMs);
    }

    /**
     * Sinh JWT cho user.
     * <p>
     * Payload (claims):
     *   - sub  : username
     *   - role : custom claim
     *   - iat  : issued at (thời điểm tạo)
     *   - exp  : expiration (thời điểm hết hạn)
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        String token = Jwts.builder()
                .setSubject(user.getUsername())          // claim "sub"
                .claim("role", user.getRole())            // custom claim
                .claim("userId", user.getId().toString()) // bonus
                .setIssuedAt(now)                         // claim "iat"
                .setExpiration(expiryDate)                // claim "exp"
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        log.debug("Đã sinh JWT cho user={}, role={}, hết hạn sau {}ms",
                user.getUsername(), user.getRole(), expirationMs);

        return token;
    }

    /**
     * Trích username từ token.
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Trích role từ token.
     */
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * Trích userId từ token.
     */
    public String extractUserId(String token) {
        return parseClaims(token).get("userId", String.class);
    }

    /**
     * Kiểm tra token có hợp lệ không (chữ ký đúng, chưa hết hạn).
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Token không hợp lệ: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Parse token → Claims.
     * Ném exception nếu chữ ký sai hoặc hết hạn.
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}