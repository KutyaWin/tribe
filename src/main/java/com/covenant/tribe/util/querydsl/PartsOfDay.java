package com.covenant.tribe.util.querydsl;

public enum PartsOfDay {
    MORNING("6"),
    AFTERNOON("12"),
    EVENING("18"),
    NIGHT("0");

    private final String hour;

    PartsOfDay(String hour) {
        this.hour = hour;
    }
    public String getHour() {
        return hour;
    }

    public static PartsOfDay getNextEnumValue(PartsOfDay partsOfDay) {
        return PartsOfDay.values()[(partsOfDay.ordinal() + 1) % PartsOfDay.values().length];
    }
}
