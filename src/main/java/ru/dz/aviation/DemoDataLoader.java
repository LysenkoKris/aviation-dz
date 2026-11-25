package ru.dz.aviation;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.dz.aviation.domain.Aircraft;
import ru.dz.aviation.domain.BonusAccount;
import ru.dz.aviation.domain.Flight;
import ru.dz.aviation.domain.FlightStatus;
import ru.dz.aviation.domain.LoyaltyTier;
import ru.dz.aviation.domain.Passenger;
import ru.dz.aviation.domain.SeatClass;
import ru.dz.aviation.domain.Ticket;
import ru.dz.aviation.repository.AircraftRepository;
import ru.dz.aviation.repository.BonusAccountRepository;
import ru.dz.aviation.repository.FlightRepository;
import ru.dz.aviation.repository.PassengerRepository;
import ru.dz.aviation.repository.TicketRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DemoDataLoader implements CommandLineRunner {

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final BonusAccountRepository bonusAccountRepository;
    private final TicketRepository ticketRepository;

    public DemoDataLoader(
            AircraftRepository aircraftRepository,
            FlightRepository flightRepository,
            PassengerRepository passengerRepository,
            BonusAccountRepository bonusAccountRepository,
            TicketRepository ticketRepository
    ) {
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.bonusAccountRepository = bonusAccountRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (aircraftRepository.count() > 0) {
            return;
        }

        Aircraft airbus = aircraft("RA-73001", "Airbus A320", 180);
        Aircraft boeing = aircraft("RA-96002", "Boeing 777-300", 350);

        Passenger anna = passenger("4510123456", "Анна", "Соколова", "anna@example.com");
        Passenger boris = passenger("4010987654", "Борис", "Орлов", "boris@example.com");
        account("BA-4510123456", 7500, LoyaltyTier.BASIC, anna);
        account("BA-4010987654", 52000, LoyaltyTier.GOLD, boris);

        Flight su100 = flight(
                "SU100", "SVO", "LED",
                LocalDateTime.of(2026, 12, 1, 8, 0),
                LocalDateTime.of(2026, 12, 1, 9, 30),
                FlightStatus.SCHEDULED, 1, airbus
        );
        flight(
                "SU200", "SVO", "AER",
                LocalDateTime.of(2026, 12, 2, 12, 0),
                LocalDateTime.of(2026, 12, 2, 15, 0),
                FlightStatus.SCHEDULED, 0, boeing
        );
        flight(
                "SU101", "LED", "SVO",
                LocalDateTime.of(2026, 12, 1, 18, 0),
                LocalDateTime.of(2026, 12, 1, 19, 30),
                FlightStatus.DEPARTED, 0, airbus
        );

        Ticket ticket = new Ticket();
        ticket.setSeatNumber("12A");
        ticket.setSeatClass(SeatClass.ECONOMY);
        ticket.setPrice(new BigDecimal("7500.00"));
        ticket.setMilesAccrued(7500);
        ticket.setPassenger(anna);
        ticket.setFlight(su100);
        ticketRepository.save(ticket);
    }

    private Aircraft aircraft(String registration, String model, int seats) {
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistrationNumber(registration);
        aircraft.setModel(model);
        aircraft.setSeatsCapacity(seats);
        return aircraftRepository.save(aircraft);
    }

    private Passenger passenger(String passport, String firstName, String lastName, String email) {
        Passenger passenger = new Passenger();
        passenger.setPassportNumber(passport);
        passenger.setFirstName(firstName);
        passenger.setLastName(lastName);
        passenger.setEmail(email);
        return passengerRepository.save(passenger);
    }

    private void account(String number, int miles, LoyaltyTier tier, Passenger passenger) {
        BonusAccount account = new BonusAccount();
        account.setAccountNumber(number);
        account.setMilesBalance(miles);
        account.setTier(tier);
        account.setPassenger(passenger);
        bonusAccountRepository.save(account);
        passenger.setBonusAccount(account);
    }

    private Flight flight(
            String number,
            String from,
            String to,
            LocalDateTime departure,
            LocalDateTime arrival,
            FlightStatus status,
            int bookedSeats,
            Aircraft aircraft
    ) {
        Flight flight = new Flight();
        flight.setFlightNumber(number);
        flight.setDepartureAirport(from);
        flight.setArrivalAirport(to);
        flight.setDepartureTime(departure);
        flight.setArrivalTime(arrival);
        flight.setStatus(status);
        flight.setBookedSeats(bookedSeats);
        flight.setAircraft(aircraft);
        return flightRepository.save(flight);
    }
}
