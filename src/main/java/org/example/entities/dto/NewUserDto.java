package org.example.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserDto {

    @NotBlank(message = "Login is required")
    private String login;

    @Size(min = 4, message = "Password must be at least 4 characters long")
    @Pattern(regexp = "^(?!.*\\s$).*", message = "Password cannot contain only spaces or end with space")
    private String password;

    private String repeatPassword;
}
