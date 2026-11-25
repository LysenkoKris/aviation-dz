package ru.dz.aviation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bonus_account")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BonusAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 40)
    private String accountNumber;

    @Column(name = "miles_balance", nullable = false)
    private int milesBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LoyaltyTier tier;

    @OneToOne(optional = false)
    @JoinColumn(name = "passenger_id", nullable = false, unique = true)
    private Passenger passenger;

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getMilesBalance() {
        return milesBalance;
    }

    public void setMilesBalance(int milesBalance) {
        this.milesBalance = milesBalance;
    }

    public LoyaltyTier getTier() {
        return tier;
    }

    public void setTier(LoyaltyTier tier) {
        this.tier = tier;
    }

    @JsonIgnore
    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
}
