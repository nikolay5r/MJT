package bg.sofia.uni.fmi.mjt.gym.member;

public record Address(double longitude, double latitude) {
    public double getDistanceTo(Address other) {
        if (other == null) {
            throw new IllegalArgumentException("Other address is null!");
        }

        return Math.sqrt(Math.pow(other.longitude - longitude, 2) + Math.pow(other.latitude - latitude, 2));
    }
}
