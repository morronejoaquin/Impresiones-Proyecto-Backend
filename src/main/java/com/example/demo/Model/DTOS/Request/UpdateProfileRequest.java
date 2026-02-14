
package com.example.demo.Model.DTOS.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String surname;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\d+$", message = "El teléfono solo debe contener números")
    @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
    private String phone;
}