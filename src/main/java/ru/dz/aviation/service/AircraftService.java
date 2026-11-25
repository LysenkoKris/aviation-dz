package ru.dz.aviation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dz.aviation.domain.Aircraft;
import ru.dz.aviation.domain.exception.BusinessRuleException;
import ru.dz.aviation.domain.exception.NotFoundException;
import ru.dz.aviation.dto.CreateAircraftRequest;
import ru.dz.aviation.repository.AircraftRepository;

import java.util.List;

@Service
public class AircraftService {

    private final AircraftRepository aircraftRepository;

    public AircraftService(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    @Transactional
    public Aircraft create(CreateAircraftRequest request) {
        String registration = request.registrationNumber().trim().toUpperCase().replaceAll("\\s+", "");
        if (aircraftRepository.findByRegistrationNumber(registration).isPresent()) {
            throw new BusinessRuleException("Самолёт с бортовым номером " + registration + " уже есть");
        }
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistrationNumber(registration);
        aircraft.setModel(request.model().trim());
        aircraft.setSeatsCapacity(request.seatsCapacity());
        return aircraftRepository.save(aircraft);
    }

    @Transactional
    public Aircraft get(Long id) {
        return aircraftRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Самолёт", id));
    }

    @Transactional
    public List<Aircraft> findByMinCapacity(int minCapacity) {
        return aircraftRepository.findWithCapacityAtLeast(minCapacity);
    }
}
