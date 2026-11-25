package ru.dz.aviation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAircraftRequest(
        @NotBlank(message = "Бортовой номер обязателен")
        @Size(max = 16, message = "Бортовой номер длиннее 16 символов")
        String registrationNumber,

        @NotBlank(message = "Модель самолёта обязательна")
        @Size(max = 64, message = "Модель длиннее 64 символов")
        String model,

        @NotNull(message = "Вместимость обязательна")
        @Min(value = 1, message = "Вместимость должна быть больше нуля")
        @Max(value = 1000, message = "Вместимость не больше 1000")
        Integer seatsCapacity
) {
}
