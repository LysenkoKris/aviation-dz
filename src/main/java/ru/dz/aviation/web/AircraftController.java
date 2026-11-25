package ru.dz.aviation.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dz.aviation.domain.Aircraft;
import ru.dz.aviation.dto.CreateAircraftRequest;
import ru.dz.aviation.service.AircraftService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/aircraft")
public class AircraftController {

    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @PostMapping
    public ResponseEntity<Aircraft> create(@Valid @RequestBody CreateAircraftRequest request) {
        Aircraft aircraft = aircraftService.create(request);
        return ResponseEntity.created(URI.create("/api/aircraft/" + aircraft.getId())).body(aircraft);
    }

    @GetMapping("/{id}")
    public Aircraft get(@PathVariable Long id) {
        return aircraftService.get(id);
    }

    @GetMapping("/by-capacity")
    public List<Aircraft> byCapacity(@RequestParam int minCapacity) {
        return aircraftService.findByMinCapacity(minCapacity);
    }
}
