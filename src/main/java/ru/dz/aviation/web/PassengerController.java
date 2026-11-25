package ru.dz.aviation.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dz.aviation.domain.BonusAccount;
import ru.dz.aviation.domain.Passenger;
import ru.dz.aviation.dto.RegisterPassengerRequest;
import ru.dz.aviation.service.PassengerService;

import java.net.URI;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    public ResponseEntity<Passenger> register(@Valid @RequestBody RegisterPassengerRequest request) {
        Passenger passenger = passengerService.register(request);
        return ResponseEntity.created(URI.create("/api/passengers/" + passenger.getId())).body(passenger);
    }

    @GetMapping("/{id}")
    public Passenger get(@PathVariable Long id) {
        return passengerService.get(id);
    }

    @GetMapping("/{id}/bonus-account")
    public BonusAccount bonusAccount(@PathVariable Long id) {
        return passengerService.getBonusAccount(id);
    }
}
