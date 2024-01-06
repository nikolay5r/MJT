package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;
import bg.sofia.uni.fmi.mjt.itinerary.utils.AStarSearchCheapestPathOnTheMap;
import bg.sofia.uni.fmi.mjt.itinerary.utils.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SequencedCollection;

public class RideRight implements ItineraryPlanner {
    private List<Journey> schedule;

    public RideRight(List<Journey> schedule) {
        this.schedule = new ArrayList<>(schedule);
    }

    private boolean isStartIncludedInSchedule(City start) {
        for (Journey journey : schedule) {
            if (start.equals(journey.from())) {
                return true;
            }
        }

        return false;
    }

    private boolean isDestinationIncludedInSchedule(City destination) {
        for (Journey journey : schedule) {
            if (destination.equals(journey.to())) {
                return true;
            }
        }

        return false;
    }

    private Journey getCheaperJourney(Journey first, Journey second, City destination) {
        BigDecimal totalCostFirst = Utils.calcTotalCost(first, destination);
        BigDecimal totalCostSecond = Utils.calcTotalCost(first, destination);

        if (totalCostFirst.compareTo(totalCostSecond) < 0) {
            return second;
        }

        return first;
    }

    private Journey findCheapestJourneyDisallowedTransfer(City start, City destination) {
        Journey cheapestJourney = null;

        for (Journey journey : schedule) {
            if (journey.from().equals(start) && journey.to().equals(destination)) {
                if (cheapestJourney == null) {
                    cheapestJourney = journey;
                    continue;
                }

                cheapestJourney = getCheaperJourney(cheapestJourney, journey, destination);
            }
        }

        return cheapestJourney;
    }

    private SequencedCollection<Journey> findCheapestPathAllowedTransfer(City start, City destination) {
        AStarSearchCheapestPathOnTheMap algorithm = new AStarSearchCheapestPathOnTheMap(schedule);
        return algorithm.search(start, destination);
    }

    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
            throws CityNotKnownException, NoPathToDestinationException {
        if (start == null || destination == null) {
            throw new IllegalArgumentException("City is null.");
        }

        if (!isStartIncludedInSchedule(start)) {
            throw new CityNotKnownException("Start is not part of the journeys.");
        }

        if (!isDestinationIncludedInSchedule(destination)) {
            throw new CityNotKnownException("Destination is not part of the journeys.");
        }

        if (!allowTransfer) {
            Journey cheapestJourney = findCheapestJourneyDisallowedTransfer(start, destination);
            if (cheapestJourney == null) {
                throw new NoPathToDestinationException("There is no path to the destination with disallowed transfer.");
            } else {
                return new ArrayList<>(Collections.singleton(cheapestJourney));
            }
        } else {
            SequencedCollection<Journey> cheapestPath = findCheapestPathAllowedTransfer(start, destination);
            if (cheapestPath == null) {
                throw new NoPathToDestinationException("There is no path to the destination with allowed transfer.");
            } else {
                return cheapestPath;
            }
        }
    }
}
