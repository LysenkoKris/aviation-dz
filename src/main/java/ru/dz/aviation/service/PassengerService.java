package ru.dz.aviation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dz.aviation.domain.BonusAccount;
import ru.dz.aviation.domain.LoyaltyTier;
import ru.dz.aviation.domain.Passenger;
import ru.dz.aviation.domain.exception.BusinessRuleException;
import ru.dz.aviation.domain.exception.NotFoundException;
import ru.dz.aviation.dto.RegisterPassengerRequest;
import ru.dz.aviation.repository.BonusAccountRepository;
import ru.dz.aviation.repository.PassengerRepository;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final BonusAccountRepository bonusAccountRepository;

    public PassengerService(PassengerRepository passengerRepository, BonusAccountRepository bonusAccountRepository) {
        this.passengerRepository = passengerRepository;
        this.bonusAccountRepository = bonusAccountRepository;
    }

    @Transactional
    public Passenger register(RegisterPassengerRequest request) {
        String passport = request.passportNumber().replaceAll("\\s+", "").toUpperCase();
        if (passengerRepository.findByPassportNumber(passport).isPresent()) {
            throw new BusinessRuleException("Пассажир с паспортом " + passport + " уже есть");
        }

        Passenger passenger = new Passenger();
        passenger.setPassportNumber(passport);
        passenger.setFirstName(request.firstName().trim());
        passenger.setLastName(request.lastName().trim());
        passenger.setEmail(request.email().trim().toLowerCase());
        passengerRepository.save(passenger);

        BonusAccount account = new BonusAccount();
        account.setAccountNumber("BA-" + passport);
        account.setMilesBalance(0);
        account.setTier(LoyaltyTier.BASIC);
        account.setPassenger(passenger);
        bonusAccountRepository.save(account);
        passenger.setBonusAccount(account);
        return passenger;
    }

    @Transactional
    public Passenger get(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Пассажир", id));
    }

    @Transactional
    public BonusAccount getBonusAccount(Long passengerId) {
        return bonusAccountRepository.findByPassengerId(passengerId)
                .orElseThrow(() -> new NotFoundException("Бонусный счёт не найден для пассажира: " + passengerId));
    }
}
