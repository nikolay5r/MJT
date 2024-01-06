package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.Map;

public class UtilityService implements UtilityServiceAPI {
    private final Map<UtilityType, Double> taxRates;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        this.taxRates = taxRates;
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        if (utilityType == null) {
            throw new IllegalArgumentException("UtilityType is null.");
        }

        if (billable == null) {
            throw new IllegalArgumentException("Billable is null.");
        }

        return switch (utilityType) {
            case WATER -> taxRates.get(UtilityType.WATER) * billable.getWaterConsumption();
            case NATURAL_GAS -> taxRates.get(UtilityType.NATURAL_GAS) * billable.getNaturalGasConsumption();
            case ELECTRICITY -> taxRates.get(UtilityType.ELECTRICITY) * billable.getElectricityConsumption();
        };
    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {
        if (billable == null) {
            throw new IllegalArgumentException("Billable is null.");
        }

        return billable.getWaterConsumption() * taxRates.get(UtilityType.WATER) +
                billable.getElectricityConsumption() * taxRates.get(UtilityType.ELECTRICITY) +
                billable.getNaturalGasConsumption() * taxRates.get(UtilityType.NATURAL_GAS);
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {
        if (firstBillable == null) {
            throw new IllegalArgumentException("First Billable is null.");
        }

        if (secondBillable == null) {
            throw new IllegalArgumentException("Second Billable is null.");
        }

        double waterCostDifference = Math.abs(
                firstBillable.getWaterConsumption() - secondBillable.getWaterConsumption()
        );

        double electricityCostDifference = Math.abs(
                firstBillable.getElectricityConsumption() - secondBillable.getElectricityConsumption()
        );

        double naturalGasCostDifference =  Math.abs(
                firstBillable.getNaturalGasConsumption() - secondBillable.getNaturalGasConsumption()
        );

        return Map.of(
                UtilityType.WATER, waterCostDifference * taxRates.get(UtilityType.WATER),
                UtilityType.ELECTRICITY, electricityCostDifference * taxRates.get(UtilityType.ELECTRICITY),
                UtilityType.NATURAL_GAS, naturalGasCostDifference * taxRates.get(UtilityType.NATURAL_GAS)
        );
    }
}
