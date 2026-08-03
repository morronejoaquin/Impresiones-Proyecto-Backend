package com.example.demo.Security.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String oldPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, max = 18, message = "La nueva contraseña debe tener entre 8 y 18 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-zñ])(?=.*[A-ZÑ])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-zñA-ZÑ\\d@$!%*?&]{8,}$",
            message = "La nueva contraseña debe contener al menos una minúscula, una mayúscula, un número y un carácter especial"
    )
    private String newPassword;
}
