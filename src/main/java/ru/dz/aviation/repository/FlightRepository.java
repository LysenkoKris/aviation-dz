package ru.dz.aviation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dz.aviation.domain.Flight;
import ru.dz.aviation.domain.FlightStatus;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByStatus(FlightStatus status);
}
