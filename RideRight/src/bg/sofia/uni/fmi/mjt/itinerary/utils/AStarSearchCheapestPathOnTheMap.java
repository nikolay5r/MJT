package bg.sofia.uni.fmi.mjt.itinerary.utils;

import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SequencedCollection;

public class AStarSearchCheapestPathOnTheMap {
    private HashMap<City, ArrayList<Journey>> map;
    private HashSet<Journey> openSet;
    private HashSet<Journey> closedSet;
    private HashMap<City, Journey> cameFrom;
    private HashMap<Journey, BigDecimal> costs;

    private record Route(Journey journey, BigDecimal cost, BigDecimal totalCost) {

    }

    public AStarSearchCheapestPathOnTheMap(List<Journey> schedule) {
        openSet = new HashSet<>();
        closedSet = new HashSet<>();
        cameFrom = new HashMap<>();
        costs = new HashMap<>();
        map = new HashMap<>();

        for (Journey journey : schedule) {
            costs.put(journey, Utils.calcPriceCost(journey));
        }

        for (Journey journey : schedule) {
            map.putIfAbsent(journey.from(), new ArrayList<>());
            map.get(journey.from()).add(journey);
        }

    }

    private SequencedCollection<Journey> createCheapestPath(Journey cheapest, City start) {
        ArrayList<Journey> cheapestPath = new ArrayList<>();

        cheapestPath.add(cheapest);
        City currCity = cheapest.from();
        while (!currCity.equals(start)) {
            Journey currJourney = cameFrom.get(currCity);
            cheapestPath.add(currJourney);
            currCity = currJourney.from();
        }

        return cheapestPath.reversed();
    }

    private boolean isCheaper(BigDecimal firstCost, BigDecimal secondCost, City firstCity, City secondCity) {
        return firstCost.compareTo(secondCost) < 0 ||
                (firstCost.compareTo(secondCost) == 0 && firstCity.name().compareTo(secondCity.name()) < 0);
    }

    public SequencedCollection<Journey> search(City start, City destination) {
        openSet.addAll(map.get(start));

        if (!map.containsKey(start)) {
            return null;
        }
        cameFrom.put(start, null);

        while (!openSet.isEmpty()) {
            Route cheapest = null;

            for (Journey journey : openSet) {
                //g function
                BigDecimal journeyCost = costs.get(journey);
                if (cameFrom.get(journey.from()) != null) {
                    journeyCost = journeyCost.add(costs.get(cameFrom.get(journey.from())));
                }
                //function f = g + h (destination price cost)
                BigDecimal journeyTotalCost = Utils.calcTotalCost(journey, destination);
                //find the cheapest journey of all
                if (cheapest == null ||
                        isCheaper(journeyTotalCost, cheapest.totalCost(), journey.to(), cheapest.journey().to())) {
                    cheapest = new Route(journey, journeyCost, journeyTotalCost);
                }
            }

            //check if the cheapest journey destination equals the wanted destination
            if (cheapest.journey().to().equals(destination)) {
                return createCheapestPath(cheapest.journey(), start);
            }

            cameFrom.put(cheapest.journey().to(), cheapest.journey());
            closedSet.add(cheapest.journey());
            openSet.remove(cheapest.journey());
            costs.put(cheapest.journey(), cheapest.cost());

            //add only those who are not in the closed set
            for (Journey j : map.get(cheapest.journey().to())) {
                if (!closedSet.contains(j)) {
                    openSet.add(j);
                }
            }
        }

        return null;
    }
}
