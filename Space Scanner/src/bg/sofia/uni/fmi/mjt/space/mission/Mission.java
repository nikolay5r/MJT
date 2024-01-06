package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import bg.sofia.uni.fmi.mjt.space.utils.CSVSpliter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public record Mission(
        String id,
        String company,
        String location,
        LocalDate date,
        Detail detail,
        RocketStatus rocketStatus,
        Optional<Double> cost,
        MissionStatus missionStatus
) {
    private static final String DATE_FORMAT = "EEE MMM dd, yyyy";
    private static final char DATA_DELIMITER = ',';
    private static final int ID_INDEX = 0;
    private static final int COMPANY_INDEX = 1;
    private static final int LOCATION_INDEX = 2;
    private static final int DATE_INDEX = 3;
    private static final int DETAIL_INDEX = 4;
    private static final int ROCKET_STATUS_INDEX = 5;
    private static final int COST_INDEX = 6;
    private static final int MISSION_STATUS_INDEX = 7;

    public static Mission of(String line) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        List<String> data = CSVSpliter.splitCSV(line, DATA_DELIMITER);

        return new Mission(
                data.get(ID_INDEX),
                data.get(COMPANY_INDEX),
                data.get(LOCATION_INDEX),
                LocalDate.parse(data.get(DATE_INDEX), formatter),
                Detail.of(data.get(DETAIL_INDEX)),
                RocketStatus.fromValue(data.get(ROCKET_STATUS_INDEX)),
                data.get(COST_INDEX).isBlank() ?
                        Optional.empty() : Optional.of(Double.parseDouble(data.get(COST_INDEX).replaceAll(",", ""))),
                MissionStatus.fromValue(data.get(MISSION_STATUS_INDEX))
        );
    }
}
