package com.pubx.authservice.dto;
import com.pubx.authservice.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message="Role is required - PERSON or PROJECT")
    private String password;

    @NotNull(message = "Role is required - PERSON or PROJECT")
    private Role role;
}
