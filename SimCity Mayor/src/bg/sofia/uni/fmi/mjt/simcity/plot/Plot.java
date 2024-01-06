package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;

import java.util.HashMap;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<E> {
    private int currentOccupiedArea;
    private final int buildableArea;
    private Map<String, E> buildables;

    public Plot(int buildableArea) {
        this.buildableArea = buildableArea;
        this.buildables = new HashMap<>();
    }

    @Override
    public void construct(String address, E buildable) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address was passed empty or as null.");
        }

        if (buildable == null) {
            throw new IllegalArgumentException("Buildable was passed as null.");
        }

        if (buildable.getArea() > getRemainingBuildableArea()) {
            throw new InsufficientPlotAreaException("The area of the buildable is too big.");
        }

        if (buildables.containsKey(address)) {
            throw new BuildableAlreadyExistsException("The buildable already exists in that plot.");
        }

        buildables.put(address, buildable);
        currentOccupiedArea += buildable.getArea();
    }

    @Override
    public void demolish(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address was passed empty or as null.");
        }

        if (!buildables.containsKey(address)) {
            throw new BuildableNotFoundException("Buildable with such address does not exist.");
        }

        currentOccupiedArea -= buildables.get(address).getArea();
        buildables.remove(address);
    }

    @Override
    public void demolishAll() {
        currentOccupiedArea = 0;
        buildables.clear();
    }

    @Override
    public Map<String, E> getAllBuildables() {
        return Map.copyOf(buildables);
    }

    @Override
    public int getRemainingBuildableArea() {
        return buildableArea - currentOccupiedArea;
    }

    @Override
    public void constructAll(Map<String, E> buildables) {
        if (buildables == null) {
            throw new IllegalArgumentException("Buildables was passed as null.");
        }

        if (buildables.isEmpty()) {
            throw new IllegalArgumentException("Buildables was empty.");
        }

        int sumOfArea = 0;
        for (Map.Entry<String, E> entry : buildables.entrySet()) {
            if (this.buildables.containsKey(entry.getKey())) {
                throw new BuildableAlreadyExistsException(
                        "Building with address " + entry.getKey() + " already exists."
                );
            }

            sumOfArea += entry.getValue().getArea();
        }

        if (sumOfArea > getRemainingBuildableArea()) {
            throw new InsufficientPlotAreaException(
                    "The combined area of the buildables exceeds the remaining plot area."
            );
        }

        currentOccupiedArea += sumOfArea;
        this.buildables.putAll(buildables);
    }
}
