package ru.dz.aviation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterPassengerRequest(
        @NotBlank(message = "Номер паспорта обязателен")
        @Size(max = 32, message = "Номер паспорта длиннее 32 символов")
        String passportNumber,

        @NotBlank(message = "Имя обязательно")
        @Size(max = 64, message = "Имя длиннее 64 символов")
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        @Size(max = 64, message = "Фамилия длиннее 64 символов")
        String lastName,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный email")
        @Size(max = 128, message = "Email длиннее 128 символов")
        String email
) {
}
