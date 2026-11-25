package ru.dz.aviation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dz.aviation.domain.SeatClass;
import ru.dz.aviation.domain.Ticket;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t WHERE t.seatClass = :seatClass ORDER BY t.id")
    List<Ticket> findBySeatClass(@Param("seatClass") SeatClass seatClass);

    boolean existsByFlightIdAndSeatNumber(Long flightId, String seatNumber);

    boolean existsByPassengerIdAndFlightId(Long passengerId, Long flightId);
}
