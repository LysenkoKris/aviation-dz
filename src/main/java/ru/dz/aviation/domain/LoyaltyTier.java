package ru.dz.aviation.domain;

public enum LoyaltyTier {
    BASIC,
    SILVER,
    GOLD;

    public static LoyaltyTier resolve(int miles) {
        if (miles >= 50_000) {
            return GOLD;
        }
        if (miles >= 10_000) {
            return SILVER;
        }
        return BASIC;
    }
}
