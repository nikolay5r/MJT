package bg.sofia.uni.fmi.mjt.space.utils;

import java.util.ArrayList;
import java.util.List;

public class CSVSpliter {
    private static final char DEFAULT_INSIDE_DELIMITER = '"';

    public static List<String> splitCSV(String input, char delimiter) {
        List<String> result = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean isInside = false;

        for (char c : input.toCharArray()) {
            if (c == DEFAULT_INSIDE_DELIMITER) {
                isInside = !isInside;
            } else if (c == delimiter && !isInside) {
                result.add(currentToken.toString().strip());
                currentToken.setLength(0);
            } else {
                currentToken.append(c);
            }
        }

        result.add(currentToken.toString().strip());

        return result;
    }
}
