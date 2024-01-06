package bg.sofia.uni.fmi.mjt.order.server.tshirt;

public enum Size {
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    UNKNOWN("UNKNOWN");

    private final String name;

    Size(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Size of(String name) {
        for (Size value: values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }

        return UNKNOWN;
    }
}