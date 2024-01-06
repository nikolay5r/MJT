package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.utils.CSVSpliter;

import java.util.List;

public record Detail(String rocketName, String payload) {
    private static final char DATA_DELIMITER = '|';
    private static final int ROCKET_NAME_INDEX = 0;
    private static final int PAYLOAD_INDEX = 1;

    public static Detail of(String line) {
        List<String> data = CSVSpliter.splitCSV(line, DATA_DELIMITER);

        return new Detail(data.get(ROCKET_NAME_INDEX), data.get(PAYLOAD_INDEX));
    }
}
