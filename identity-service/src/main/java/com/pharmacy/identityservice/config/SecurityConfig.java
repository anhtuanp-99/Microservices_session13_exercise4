package com.pharmacy.identityservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * SecurityFilterChain — cấu hình bảo mật cho toàn bộ ứng dụng.
     * <p>
     * - CSRF disabled vì là REST API (dùng JWT hoặc session sau này).
     * - Session stateless để phù hợp mô hình microservices.
     * - Permit tất cả request tới /api/auth/** (đăng ký, đăng nhập).
     * - Basic Auth disabled — chưa có UserDetailsService.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF — REST API không dùng form, chống CSRF bằng token riêng
                .csrf(csrf -> csrf.disable())

                // Không dùng session — stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Không dùng form login
                .formLogin(form -> form.disable())

                // Không dùng HTTP Basic (vì chưa có UserDetailsService)
                .httpBasic(basic -> basic.disable())

                // Phân quyền request
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/products/**"          // ← THÊM (chỉ để test routing)
                        ).permitAll()
                        .requestMatchers("/actuator/**", "/error").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * Bean BCryptPasswordEncoder để băm mật khẩu.
     * <p>
     * Strength mặc định = 10. Có thể tăng lên 12-14 cho production
     * nhưng sẽ chậm hơn ~2^strength lần.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}