package org.oldvabik.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oldvabik.authservice.entity.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    @Email
    @Size(min = 8, max = 32)
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotNull
    private Role role;
}
