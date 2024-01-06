package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public record Player(
        String name,
        String fullName,
        LocalDate birthDate,
        int age,
        double heightCm,
        double weightKg,
        List<Position> positions,
        String nationality,
        int overallRating,
        int potential,
        long valueEuro,
        long wageEuro,
        Foot preferredFoot
) {
    private static final String PLAYER_DELIMITER = ";";
    private static final String POSITIONS_DELIMITER = ",";
    private static final String BIRTH_DATE_FORMAT = "M/d/yyyy";

    private static final int TOKEN_NAME_INDEX = 0;
    private static final int TOKEN_FULL_NAME_INDEX = 1;
    private static final int TOKEN_BIRTH_DATE_INDEX = 2;
    private static final int TOKEN_AGE_INDEX = 3;
    private static final int TOKEN_HEIGHT_INDEX = 4;
    private static final int TOKEN_WEIGHT_INDEX = 5;
    private static final int TOKEN_POSITIONS_INDEX = 6;
    private static final int TOKEN_NATIONALITY_INDEX = 7;
    private static final int TOKEN_OVERALL_RATING_INDEX = 8;
    private static final int TOKEN_POTENTIAL_INDEX = 9;
    private static final int TOKEN_VALUE_INDEX = 10;
    private static final int TOKEN_WAGE_INDEX = 11;
    private static final int TOKEN_FOOT_INDEX = 12;

    public static Player of(String line) {
        String[] tokens = line.split(PLAYER_DELIMITER);

        List<Position> positions = Arrays.stream(tokens[TOKEN_POSITIONS_INDEX].split(POSITIONS_DELIMITER))
                .map(Position::valueOf)
                .toList();

        DateTimeFormatter birthDateFormatter = DateTimeFormatter.ofPattern(BIRTH_DATE_FORMAT);

        return new Player(
                tokens[TOKEN_NAME_INDEX],
                tokens[TOKEN_FULL_NAME_INDEX],
                LocalDate.parse(tokens[TOKEN_BIRTH_DATE_INDEX], birthDateFormatter),
                Integer.parseInt(tokens[TOKEN_AGE_INDEX]),
                Double.parseDouble(tokens[TOKEN_HEIGHT_INDEX]),
                Double.parseDouble(tokens[TOKEN_WEIGHT_INDEX]),
                positions,
                tokens[TOKEN_NATIONALITY_INDEX],
                Integer.parseInt(tokens[TOKEN_OVERALL_RATING_INDEX]),
                Integer.parseInt(tokens[TOKEN_POTENTIAL_INDEX]),
                Long.parseLong(tokens[TOKEN_VALUE_INDEX]),
                Long.parseLong(tokens[TOKEN_WAGE_INDEX]),
                Foot.valueOf(tokens[TOKEN_FOOT_INDEX].toUpperCase())
        );
    }
}
