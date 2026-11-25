package ru.dz.aviation.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dz.aviation.domain.Aircraft;
import ru.dz.aviation.domain.Flight;
import ru.dz.aviation.domain.FlightStatus;
import ru.dz.aviation.domain.exception.NotFoundException;
import ru.dz.aviation.dto.ScheduleFlightRequest;
import ru.dz.aviation.repository.AircraftRepository;
import ru.dz.aviation.repository.FlightRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final EntityManager entityManager;

    public FlightService(
            FlightRepository flightRepository,
            AircraftRepository aircraftRepository,
            EntityManager entityManager
    ) {
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public Flight schedule(ScheduleFlightRequest request) {
        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() -> NotFoundException.of("Самолёт", request.aircraftId()));
        if (!request.arrivalTime().isAfter(request.departureTime())) {
            throw new IllegalArgumentException("Время прилёта должно быть позже вылета");
        }

        Flight flight = new Flight();
        flight.setFlightNumber(request.flightNumber().trim().toUpperCase());
        flight.setDepartureAirport(request.departureAirport().trim().toUpperCase());
        flight.setArrivalAirport(request.arrivalAirport().trim().toUpperCase());
        flight.setDepartureTime(request.departureTime());
        flight.setArrivalTime(request.arrivalTime());
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setBookedSeats(0);
        flight.setAircraft(aircraft);
        return flightRepository.save(flight);
    }

    @Transactional
    public Flight get(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Рейс", id));
    }

    @Transactional
    public List<Flight> findByStatus(FlightStatus status) {
        return flightRepository.findByStatus(status);
    }

    @Transactional
    public List<Flight> search(
            FlightStatus status,
            String departureAirport,
            String arrivalAirport,
            LocalDateTime departFrom,
            LocalDateTime departTo
    ) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Flight> query = builder.createQuery(Flight.class);
        Root<Flight> flight = query.from(Flight.class);

        List<Predicate> predicates = new ArrayList<>();
        if (status != null) {
            predicates.add(builder.equal(flight.get("status"), status));
        }
        if (departureAirport != null && !departureAirport.isBlank()) {
            predicates.add(builder.equal(flight.get("departureAirport"), departureAirport.trim().toUpperCase()));
        }
        if (arrivalAirport != null && !arrivalAirport.isBlank()) {
            predicates.add(builder.equal(flight.get("arrivalAirport"), arrivalAirport.trim().toUpperCase()));
        }
        if (departFrom != null) {
            predicates.add(builder.greaterThanOrEqualTo(flight.get("departureTime"), departFrom));
        }
        if (departTo != null) {
            predicates.add(builder.lessThanOrEqualTo(flight.get("departureTime"), departTo));
        }

        query.select(flight)
                .where(predicates.toArray(Predicate[]::new))
                .orderBy(builder.asc(flight.get("departureTime")), builder.asc(flight.get("id")));
        return entityManager.createQuery(query).getResultList();
    }
}
