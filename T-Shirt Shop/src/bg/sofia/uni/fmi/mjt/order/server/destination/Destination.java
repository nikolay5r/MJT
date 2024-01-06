package bg.sofia.uni.fmi.mjt.order.server.destination;

import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;

public enum Destination {
    EUROPE("EUROPE"),
    NORTH_AMERICA("NORTH_AMERICA"),
    AUSTRALIA("AUSTRALIA"),
    UNKNOWN("UNKNOWN");

    private final String name;

    Destination(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Destination of(String name) {
        for (Destination value: values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }

        return UNKNOWN;
    }
}