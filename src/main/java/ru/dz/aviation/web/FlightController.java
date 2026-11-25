package ru.dz.aviation.web;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dz.aviation.domain.Flight;
import ru.dz.aviation.domain.FlightStatus;
import ru.dz.aviation.dto.ScheduleFlightRequest;
import ru.dz.aviation.service.FlightService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<Flight> schedule(@Valid @RequestBody ScheduleFlightRequest request) {
        Flight flight = flightService.schedule(request);
        return ResponseEntity.created(URI.create("/api/flights/" + flight.getId())).body(flight);
    }

    @GetMapping("/{id}")
    public Flight get(@PathVariable Long id) {
        return flightService.get(id);
    }

    @GetMapping("/by-status")
    public List<Flight> byStatus(@RequestParam FlightStatus status) {
        return flightService.findByStatus(status);
    }

    @GetMapping("/search")
    public List<Flight> search(
            @RequestParam(required = false) FlightStatus status,
            @RequestParam(required = false) String departureAirport,
            @RequestParam(required = false) String arrivalAirport,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departTo
    ) {
        return flightService.search(status, departureAirport, arrivalAirport, departFrom, departTo);
    }
}
