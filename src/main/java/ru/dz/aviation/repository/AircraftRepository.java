package ru.dz.aviation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dz.aviation.domain.Aircraft;

import java.util.List;
import java.util.Optional;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    Optional<Aircraft> findByRegistrationNumber(String registrationNumber);

    @Query(
            value = """
                    SELECT id, registration_number, model, seats_capacity
                    FROM aircraft
                    WHERE seats_capacity >= :minCapacity
                    ORDER BY seats_capacity DESC, id
                    """,
            nativeQuery = true
    )
    List<Aircraft> findWithCapacityAtLeast(@Param("minCapacity") int minCapacity);
}
