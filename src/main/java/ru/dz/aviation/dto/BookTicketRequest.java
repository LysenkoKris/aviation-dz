package ru.dz.aviation.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.dz.aviation.domain.SeatClass;

import java.math.BigDecimal;

public record BookTicketRequest(
        @NotNull(message = "Пассажир обязателен")
        Long passengerId,

        @NotNull(message = "Рейс обязателен")
        Long flightId,

        @NotBlank(message = "Место обязательно")
        @Size(max = 8, message = "Номер места длиннее 8 символов")
        String seatNumber,

        @NotNull(message = "Класс обслуживания обязателен")
        SeatClass seatClass,

        @NotNull(message = "Цена обязательна")
        @Positive(message = "Цена должна быть больше нуля")
        @Digits(integer = 10, fraction = 2, message = "Цена: не больше 10 цифр до запятой и 2 после")
        BigDecimal price
) {
}
