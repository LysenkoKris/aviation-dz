package ru.dz.aviation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dz.aviation.domain.Passenger;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    Optional<Passenger> findByPassportNumber(String passportNumber);
}
