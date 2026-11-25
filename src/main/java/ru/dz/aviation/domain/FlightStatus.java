package ru.dz.aviation.domain;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum FlightStatus {
    SCHEDULED,
    BOARDING,
    DEPARTED,
    ARRIVED,
    CANCELLED;

    private static final Map<FlightStatus, Set<FlightStatus>> TRANSITIONS = Map.of(
            SCHEDULED, EnumSet.of(BOARDING, CANCELLED),
            BOARDING, EnumSet.of(DEPARTED, CANCELLED),
            DEPARTED, EnumSet.of(ARRIVED),
            ARRIVED, EnumSet.noneOf(FlightStatus.class),
            CANCELLED, EnumSet.noneOf(FlightStatus.class)
    );

    public boolean canTransitTo(FlightStatus target) {
        return TRANSITIONS.get(this).contains(target);
    }
}
