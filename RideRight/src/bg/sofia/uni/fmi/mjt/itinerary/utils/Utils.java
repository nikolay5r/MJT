package bg.sofia.uni.fmi.mjt.itinerary.utils;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;

import java.math.BigDecimal;

public class Utils {
    private static final int KM_TO_METERS = 1000;
    private static final BigDecimal DOLLARS_PER_KM = new BigDecimal(20);

    public static BigDecimal calcPriceCost(Journey journey) {
        BigDecimal greenTax = journey.vehicleType().getGreenTax();
        BigDecimal price = journey.price();

        return price.add(greenTax.multiply(price)).add(calcDistanceInKMCost(journey.to(), journey.from()));
    }

    public static BigDecimal calcDistanceInKM(City first, City second) {
        int p1 = Math.abs(first.location().x() - second.location().x());
        int p2 = Math.abs(first.location().y() - second.location().y());

        return new BigDecimal(p1 + p2 / KM_TO_METERS);
    }

    public static BigDecimal calcDistanceInKMCost(City first, City second) {
        return calcDistanceInKM(first, second).multiply(DOLLARS_PER_KM);
    }

    public static BigDecimal calcTotalCost(Journey journey, City end) {
        return calcPriceCost(journey).add(calcDistanceInKMCost(journey.to(), end));
    }
}
