package ru.dz.aviation.domain;

public enum SeatClass {
    ECONOMY(1),
    BUSINESS(2),
    FIRST(3);

    private final int milesMultiplier;

    SeatClass(int milesMultiplier) {
        this.milesMultiplier = milesMultiplier;
    }

    public int milesMultiplier() {
        return milesMultiplier;
    }
}
