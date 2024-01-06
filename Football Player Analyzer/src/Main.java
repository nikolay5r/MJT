import bg.sofia.uni.fmi.mjt.football.FootballPlayerAnalyzer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

public class Main {
    public static void main(String[] args) {
        try (Reader reader = new FileReader("fifa_players_clean.csv")) {
            FootballPlayerAnalyzer analyzer = new FootballPlayerAnalyzer(reader);

            System.out.println(analyzer.getHighestPaidPlayerByNationality("Bulgaria"));
        } catch (IOException e) {
            throw new UncheckedIOException("Something went wrong when reading the file.", e);
        }
    }
}