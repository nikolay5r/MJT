package bg.sofia.uni.fmi.mjt.space.rocket;

import bg.sofia.uni.fmi.mjt.space.utils.CSVSpliter;

import java.util.List;
import java.util.Optional;

public record Rocket(
        String id,
        String name,
        Optional<String> wiki,
        Optional<Double> height
) {
    private static final char DATA_DELIMITER = ',';
    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int WIKI_INDEX = 2;
    private static final int HEIGHT_INDEX = 3;

    public static Rocket of(String line) {
        List<String> data = CSVSpliter.splitCSV(line, DATA_DELIMITER);

        return new Rocket(
                data.get(ID_INDEX),
                data.get(NAME_INDEX),
                data.get(WIKI_INDEX).isBlank() ?
                        Optional.empty() : Optional.of(data.get(WIKI_INDEX)),
                data.get(HEIGHT_INDEX).isBlank() ? Optional.empty() : Optional.of(
                        Double.parseDouble(
                                CSVSpliter.splitCSV(data.get(HEIGHT_INDEX), ' ').get(0).replaceAll(",", "")
                        )
                )
        );
    }
}
