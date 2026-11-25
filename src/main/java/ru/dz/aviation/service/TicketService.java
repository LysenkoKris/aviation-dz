package ru.dz.aviation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dz.aviation.domain.BonusAccount;
import ru.dz.aviation.domain.Flight;
import ru.dz.aviation.domain.FlightStatus;
import ru.dz.aviation.domain.LoyaltyTier;
import ru.dz.aviation.domain.Passenger;
import ru.dz.aviation.domain.SeatClass;
import ru.dz.aviation.domain.Ticket;
import ru.dz.aviation.domain.exception.BusinessRuleException;
import ru.dz.aviation.domain.exception.NotFoundException;
import ru.dz.aviation.dto.BookTicketRequest;
import ru.dz.aviation.repository.BonusAccountRepository;
import ru.dz.aviation.repository.FlightRepository;
import ru.dz.aviation.repository.PassengerRepository;
import ru.dz.aviation.repository.TicketRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final BonusAccountRepository bonusAccountRepository;

    public TicketService(
            TicketRepository ticketRepository,
            FlightRepository flightRepository,
            PassengerRepository passengerRepository,
            BonusAccountRepository bonusAccountRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.bonusAccountRepository = bonusAccountRepository;
    }

    @Transactional
    public Ticket book(BookTicketRequest request) {
        Flight flight = flightRepository.findById(request.flightId())
                .orElseThrow(() -> NotFoundException.of("Рейс", request.flightId()));
        Passenger passenger = passengerRepository.findById(request.passengerId())
                .orElseThrow(() -> NotFoundException.of("Пассажир", request.passengerId()));
        if (flight.getStatus() != FlightStatus.SCHEDULED && flight.getStatus() != FlightStatus.BOARDING) {
            throw new BusinessRuleException("Бронирование закрыто: рейс в статусе " + flight.getStatus());
        }

        String seat = request.seatNumber().trim().toUpperCase().replaceAll("\\s+", "");
        if (ticketRepository.existsByFlightIdAndSeatNumber(flight.getId(), seat)) {
            throw new BusinessRuleException("Место уже занято: " + seat);
        }
        if (ticketRepository.existsByPassengerIdAndFlightId(passenger.getId(), flight.getId())) {
            throw new BusinessRuleException("Пассажир уже зарегистрирован на этот рейс");
        }
        if (flight.getBookedSeats() >= flight.getAircraft().getSeatsCapacity()) {
            throw new BusinessRuleException("Нет свободных мест");
        }

        int miles = milesFor(request.price(), request.seatClass());
        Ticket ticket = new Ticket();
        ticket.setSeatNumber(seat);
        ticket.setSeatClass(request.seatClass());
        ticket.setPrice(request.price());
        ticket.setMilesAccrued(miles);
        ticket.setPassenger(passenger);
        ticket.setFlight(flight);
        ticketRepository.save(ticket);

        flight.setBookedSeats(flight.getBookedSeats() + 1);
        BonusAccount account = bonusAccountRepository.findByPassengerId(passenger.getId())
                .orElseThrow(() -> new NotFoundException("Бонусный счёт не найден для пассажира: " + passenger.getId()));
        account.setMilesBalance(account.getMilesBalance() + miles);
        account.setTier(LoyaltyTier.resolve(account.getMilesBalance()));
        return ticket;
    }

    @Transactional
    public List<Ticket> findBySeatClass(SeatClass seatClass) {
        return ticketRepository.findBySeatClass(seatClass);
    }

    private static int milesFor(BigDecimal price, SeatClass seatClass) {
        int base = price.setScale(0, RoundingMode.HALF_UP).intValueExact();
        return base * seatClass.milesMultiplier();
    }
}
