package ru.dz.aviation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dz.aviation.domain.BonusAccount;

import java.util.Optional;

public interface BonusAccountRepository extends JpaRepository<BonusAccount, Long> {

    Optional<BonusAccount> findByPassengerId(Long passengerId);
}
