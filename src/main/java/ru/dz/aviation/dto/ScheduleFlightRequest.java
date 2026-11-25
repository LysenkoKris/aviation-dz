package ru.dz.aviation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ScheduleFlightRequest(
        @NotBlank(message = "Номер рейса обязателен")
        @Size(max = 16, message = "Номер рейса длиннее 16 символов")
        String flightNumber,

        @NotBlank(message = "Аэропорт вылета обязателен")
        @Pattern(regexp = "[A-Za-z]{3}", message = "Код аэропорта вылета — 3 латинские буквы")
        String departureAirport,

        @NotBlank(message = "Аэропорт прилёта обязателен")
        @Pattern(regexp = "[A-Za-z]{3}", message = "Код аэропорта прилёта — 3 латинские буквы")
        String arrivalAirport,

        @NotNull(message = "Время вылета обязательно")
        LocalDateTime departureTime,

        @NotNull(message = "Время прилёта обязательно")
        LocalDateTime arrivalTime,

        @NotNull(message = "Самолёт обязателен")
        Long aircraftId
) {
}
