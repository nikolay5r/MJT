package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class FootballPlayerAnalyzerTest {

    private static final String BERBATOV_NATIONALITY = "Bulgaria";
    private static final String MESSI_NATIONALITY = "Argentina";
    private static final String POGBA_NATIONALITY = "France";
    private static final String KOLEV_NATIONALITY = "Bulgaria";

    private static final long BERBATOV_WAGE = 200000L;
    private static final long KOLEV_WAGE = 20000L;

    private static final String BERBATOV_DATA = "D. Berbatov;Dimitar Ivanov Berbatov;1/30/1981;" +
            "42;189;79;ST;" + BERBATOV_NATIONALITY + ";91;93;20000000;" + BERBATOV_WAGE + ";Left";
    private static final String MESSI_DATA = "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;" +
            "31;170.18;72.1;CF,ST;" + MESSI_NATIONALITY + ";94;94;110500000;565000;Left";
    private static final String POGBA_DATA = "P. Pogba;Paul Pogba;3/15/1993;" +
            "25;190.5;83.9;CAM;" + POGBA_NATIONALITY + ";88;91;73000000;255000;Right";
    private static final String KOLEV_DATA = "A. Kolev;Alexander Kolev;12/8/1992;" +
            "26;190.5;81.2;ST;" + KOLEV_NATIONALITY + ";65;68;700000;" + KOLEV_WAGE + ";Right";
    private static final String HEADERS = "name;full_name;birth_date;age;height_cm;weight_kgs;positions;" +
            "nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot";

    private static final String INPUT = HEADERS + System.lineSeparator() +
            BERBATOV_DATA + System.lineSeparator() +
            MESSI_DATA + System.lineSeparator() +
            KOLEV_DATA + System.lineSeparator() +
            POGBA_DATA + System.lineSeparator();
    private static final String EMPTY_INPUT = HEADERS + System.lineSeparator();

    private static FootballPlayerAnalyzer analyzer;
    private static FootballPlayerAnalyzer emptyAnalyzer;
    private static Player berbatov;
    private static Player messi;
    private static Player pogba;
    private static Player kolev;

    @BeforeAll
    public static void init() throws FileNotFoundException {
        try (var input = new StringReader(INPUT);
             var emptyInput = new StringReader(EMPTY_INPUT)) {
            analyzer = new FootballPlayerAnalyzer(input);
            emptyAnalyzer = new FootballPlayerAnalyzer(emptyInput);
        }

        berbatov = Player.of(BERBATOV_DATA);
        messi = Player.of(MESSI_DATA);
        pogba = Player.of(POGBA_DATA);
        kolev = Player.of(KOLEV_DATA);
    }

    @Test
    void testGetAllPlayers() throws FileNotFoundException {
        List<Player> players = List.of(
                berbatov,
                messi,
                kolev,
                pogba
        );

        assertEquals(
                players,
                analyzer.getAllPlayers(),
                "When tested getAllPlayers(), " +
                        "method should return" + players
        );
    }

    @Test
    void testGetAllPlayersWithNoPlayers() throws FileNotFoundException {
        assertTrue(
                emptyAnalyzer.getAllPlayers().isEmpty(),
                "When tested getAllPlayers() with no players, " +
                        "method should return empty list."
        );
    }

    @Test
    void testGetAllNationalities() throws FileNotFoundException {
        Set<String> expected = Set.of(BERBATOV_NATIONALITY, MESSI_NATIONALITY, POGBA_NATIONALITY);

        assertEquals(
                expected,
                analyzer.getAllNationalities(),
                "When called getAllNationalities() with input, " +
                        "method should return set of strings equal to " + expected
        );
    }

    @Test
    void testGetAllNationalitiesWithNoPlayers() throws FileNotFoundException {
        assertTrue(
                emptyAnalyzer.getAllNationalities().isEmpty(),
                "When called getAllNationalities() with no players, " +
                        "method should return empty set."
        );
    }

    @Test
    void testGetHighestPaidPlayerByNationalityWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.getHighestPaidPlayerByNationality(null),
                "When tested getHighestPaidPlayerByNationality(...) with null," +
                        "method should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetHighestPaidPlayerByNationalityWithNoPlayers() {
        assertThrows(
                NoSuchElementException.class,
                () -> emptyAnalyzer.getHighestPaidPlayerByNationality(BERBATOV_NATIONALITY),
                "When tested getHighestPaidPlayerByNationality(...) with no players analyzer," +
                        "method should throw NoSuchElementException."
        );
    }

    @Test
    void testGetHighestPaidPlayerByNationalityWithNoPlayersFromNationality() {
        assertThrows(
                NoSuchElementException.class,
                () -> analyzer.getHighestPaidPlayerByNationality("Congo"),
                "When tested getHighestPaidPlayerByNationality(...) with no players from nationality," +
                        "method should throw NoSuchElementException."
        );
    }

    @Test
    void testGetHighestPaidPlayerByNationalityWithPlayersFromNationality() {
        Player berbatov = Player.of(BERBATOV_DATA);

        assertEquals(
                berbatov,
                analyzer.getHighestPaidPlayerByNationality(BERBATOV_NATIONALITY),
                "When tested getHighestPaidPlayerByNationality(...) with players from nationality," +
                        "method should return the correct player."
        );
    }

    @Test
    void testGroupByPositionWithNoPlayers() {
        assertTrue(
                emptyAnalyzer.groupByPosition().isEmpty(),
                "When called groupByPosition() with no players, " +
                        "method should return empty map."
        );
    }

    @Test
    void testGroupByPosition() {
        Map<Position, Set<Player>> expected = Map.of(
                Position.ST, Set.of(berbatov, kolev, messi),
                Position.CF, Set.of(messi),
                Position.CAM, Set.of(pogba)
        );

        assertEquals(
                expected,
                analyzer.groupByPosition(),
                "When called groupByPosition() with no players, " +
                        "method should return correct map."
        );
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetWithNoPlayers() {
        assertTrue(
                emptyAnalyzer.getTopProspectPlayerForPositionInBudget(Position.CF, 1).isEmpty(),
                "When called getTopProspectPlayerForPositionInBudget(...) with no players, " +
                        "method should return empty optional of player."
        );
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetWithNullPosition() {
        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.getTopProspectPlayerForPositionInBudget(null, 1),
                "When called getTopProspectPlayerForPositionInBudget(...) with null position, " +
                        "method should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetWithNegativeBudget() {
        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.getTopProspectPlayerForPositionInBudget(Position.CF, -1),
                "When called getTopProspectPlayerForPositionInBudget(...) with negative budget, " +
                        "method should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetWithSmallBudget() {
        assertTrue(
                analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, 1).isEmpty(),
                "When called getTopProspectPlayerForPositionInBudget(...) with small budget, " +
                        "method should return empty optional of player."
        );
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetWithNonexistentPosition() {
        assertTrue(
                analyzer.getTopProspectPlayerForPositionInBudget(Position.GK, 110500000).isEmpty(),
                "When called getTopProspectPlayerForPositionInBudget(...) with nonexistent budget, " +
                        "method should return empty optional of player."
        );
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudget() {
        assertEquals(
                messi,
                analyzer.getTopProspectPlayerForPositionInBudget(Position.ST, 110500000).get(),
                "When called getTopProspectPlayerForPositionInBudget(...) with correct data, " +
                        "method should return correct player."
        );
    }

    @Test
    void testGetSimilarPlayersWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.getSimilarPlayers(null),
                "When called getSimilarPlayers(...) with null" +
                        "method should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetSimilarPlayersWithNoPlayers() {
        assertTrue(
                emptyAnalyzer.getSimilarPlayers(berbatov).isEmpty(),
                "When called getSimilarPlayers(...) with no players" +
                        "method should return empty set."
        );
    }

    @Test
    void testGetSimilarPlayersWithNoSimilarPlayersAndPlayerInAnalyzer() {
        assertEquals(
                Set.of(pogba),
                analyzer.getSimilarPlayers(pogba),
                "When called getSimilarPlayers(...) with no similar players" +
                        "method should return set with only the player that is called."
        );
    }

    @Test
    void testGetSimilarPlayersWithNoSimilarPlayersAndPlayerNotInAnalyzer() {
        Player player = Player.of("H. Maguire;Harry Maguire;3/5/1993;" +
                "26;193.04;99.8;CB;England;82;85;23500000;77000;Right");

        assertTrue(
                analyzer.getSimilarPlayers(player).isEmpty(),
                "When called getSimilarPlayers(...) with no similar players" +
                        "method should return empty set."
        );
    }

    @Test
    void testGetSimilarPlayers() {
        assertEquals(
                Set.of(berbatov, messi),
                analyzer.getSimilarPlayers(messi),
                "When called getSimilarPlayers(...) with correct data" +
                        "method should return correct set of players."
        );
    }

    @Test
    void testGetPlayersByFullNameKeywordWithNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.getPlayersByFullNameKeyword(null),
                "When called getPlayersByFullNameKeyword(...) with null, " +
                        "method should throw IllegalArgumentException."
        );
    }

    @Test
    void testGetPlayersByFullNameKeywordWithKeywordWithoutMatch() {
        assertTrue(
                analyzer.getPlayersByFullNameKeyword("asd").isEmpty(),
                "When called getPlayersByFullNameKeyword(...) with keyword that has no match, " +
                        "method should return empty set."
        );
    }

    @Test
    void testGetPlayersByFullNameKeywordWithNoPlayers() {
        assertTrue(
                emptyAnalyzer.getPlayersByFullNameKeyword("e").isEmpty(),
                "When called getPlayersByFullNameKeyword(...) with no players, " +
                        "method should return empty set."
        );
    }

    @Test
    void testGetPlayersByFullNameKeyword() {
        assertEquals(
                Set.of(berbatov, messi, kolev),
                analyzer.getPlayersByFullNameKeyword("e"),
                "When called getPlayersByFullNameKeyword(...) with correct data, " +
                        "method should return correct set of players."
        );
    }
}
