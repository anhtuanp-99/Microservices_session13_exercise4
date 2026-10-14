package com.pharmacy.identityservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_.]+$",
            message = "Username chỉ được chứa chữ, số, dấu chấm và gạch dưới")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 100, message = "Password phải từ 6 đến 100 ký tự")
    private String password;

    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN|ROLE_PHARMACIST",
            message = "Role chỉ nhận: ROLE_USER, ROLE_ADMIN, ROLE_PHARMACIST")
    private String role;
}