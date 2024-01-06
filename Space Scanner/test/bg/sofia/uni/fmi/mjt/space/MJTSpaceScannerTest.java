package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MJTSpaceScannerTest {
    private static SecretKey secretKey;

    private static MJTSpaceScanner spaceScanner;
    private static MJTSpaceScanner emptySpaceScanner;

    private static Rocket rocketFalcon9Block5;
    private static Rocket rocketLongMarch2D;
    private static Rocket rocketStarshipPrototype;
    private static Rocket rocketProtonMBrizM;
    private static Rocket rocketTsyklon3;

    private static Mission mission0SpaceX;
    private static Mission mission1Casc;
    private static Mission mission2SpaceX;
    private static Mission mission3Roscosmos;
    private static Mission mission4Roscosmos;
    private static Mission mission5Roscosmos;

    private static final String WIKI_FALCON_9_BLOCK_5_DATA = "https://en.wikipedia.org/wiki/Falcon_9";
    private static final String WIKI_LONG_MARCH_2D = "https://en.wikipedia.org/wiki/Long_March_2D";
    private static final String WIKI_STARSHIP_PROTOTYPE = "https://en.wikipedia.org/wiki/SpaceX_Starship";
    private static final String WIKI_PROTON_M_BRIZ_M = "https://en.wikipedia.org/wiki/Proton-M";

    private static final String ROCKETS_HEADER = "\"\",Name,Wiki,Rocket Height";
    private static final String ROCKET_FALCON_9_BLOCK_5_DATA =
            "169,Falcon 9 Block 5," + WIKI_FALCON_9_BLOCK_5_DATA + ",70.0 m";
    private static final String ROCKET_LONG_MARCH_2D_DATA =
            "213,Long March 2D," + WIKI_LONG_MARCH_2D + ",41.06 m";
    private static final String ROCKET_STARSHIP_PROTOTYPE_DATA =
            "371,Starship Prototype," + WIKI_STARSHIP_PROTOTYPE + ",";
    private static final String ROCKET_PROTON_M_BRIZ_M_DATA =
            "294,Proton-M/Briz-M," + WIKI_PROTON_M_BRIZ_M + ",58.2 m";
    private static final String ROCKET_TSYKLON_3 =
            "0,Tsyklon-3,,39.0 m";

    private static final String COUNTRY_USA = "USA";
    private static final String COUNTRY_BULGARIA = "Bulgaria";
    private static final String COUNTRY_KAZAKHSTAN = "Kazakhstan";
    private static final String COUNTRY_CHINA = "China";

    private static final String MISSION_0_LOCATION =
            "LC-39A, Kennedy Space Center, Florida, " + COUNTRY_USA;
    private static final String MISSION_1_LOCATION =
            "LSite 9401 (SLS-2), Jiuquan Satellite Launch Center, " + COUNTRY_CHINA;
    private static final String MISSION_2_LOCATION =
            "LC-39A, Kennedy Space Center, Florida, " + COUNTRY_USA;
    private static final String MISSION_3_LOCATION =
            "Site 200/39, Baikonur Cosmodrome, " + COUNTRY_KAZAKHSTAN;
    private static final String MISSION_4_LOCATION =
            "Site 200/39, Baikonur Cosmodrome, " + COUNTRY_BULGARIA;
    private static final String MISSION_5_LOCATION =
            "Site 200/39, Baikonur Cosmodrome, " + COUNTRY_KAZAKHSTAN;

    private static final String MISSIONS_HEADER =
            "Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket,\" Rocket\",Status Mission";
    private static final String MISSION_0_SPACE_X =
            "0,SpaceX,\"" + MISSION_0_LOCATION + " \",\"Fri Aug 07, 2020\"," +
                    "Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \"," +
                    MissionStatus.SUCCESS;
    private static final String MISSION_1_CASC =
            "1,CASC,\"" + MISSION_1_LOCATION + "\",\"Thu Aug 06, 2020\"," +
                    "Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,\"29.75 \"," +
                    MissionStatus.FAILURE;
    private static final String MISSION_2_SPACE_X =
            "2,SpaceX,\"" + MISSION_2_LOCATION + "\",\"Wed Aug 04, 2021\"," +
                    "Starship Prototype | 150 Meter Hop,StatusActive,," +
                    MissionStatus.SUCCESS;
    private static final String MISSION_3_ROSCOSMOS =
            "3,Roscosmos,\"" + MISSION_3_LOCATION + " \",\"Thu Jul 30, 2020\"," +
                    "Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,\"65.0 \"," +
                    MissionStatus.PARTIAL_FAILURE;
    private static final String MISSION_4_ROSCOSMOS =
            "4,Roscosmos,\"" + MISSION_4_LOCATION + "\",\"Fri Jul 30, 2060\"," +
                    "Falcon 9 Block 5 | Ekspress-80 & Ekspress-103,StatusActive,\"65.0 \"," +
                    MissionStatus.SUCCESS;
    private static final String MISSION_5_ROSCOSMOS =
            "5,Roscosmos,\"" + MISSION_5_LOCATION + "\",\"Fri Jul 30, 2010\"," +
                    "Falcon 9 Block 5 | Ekspress-80 & Ekspress-103,StatusActive,\"65.0 \"," +
                    MissionStatus.PRELAUNCH_FAILURE;

    private static final String MISSIONS_DATA = MISSIONS_HEADER + System.lineSeparator() +
            MISSION_0_SPACE_X + System.lineSeparator() +
            MISSION_1_CASC + System.lineSeparator() +
            MISSION_2_SPACE_X + System.lineSeparator() +
            MISSION_3_ROSCOSMOS + System.lineSeparator() +
            MISSION_4_ROSCOSMOS + System.lineSeparator() +
            MISSION_5_ROSCOSMOS;

    private static final String ROCKETS_DATA = ROCKETS_HEADER + System.lineSeparator() +
            ROCKET_FALCON_9_BLOCK_5_DATA + System.lineSeparator() +
            ROCKET_LONG_MARCH_2D_DATA + System.lineSeparator() +
            ROCKET_STARSHIP_PROTOTYPE_DATA + System.lineSeparator() +
            ROCKET_PROTON_M_BRIZ_M_DATA + System.lineSeparator() +
            ROCKET_TSYKLON_3;

    @BeforeAll
    public static void init() throws NoSuchAlgorithmException {
        secretKey = generateSecretKey();

        spaceScanner = new MJTSpaceScanner(
                new StringReader(MISSIONS_DATA), new StringReader(ROCKETS_DATA), secretKey
        );

        emptySpaceScanner = new MJTSpaceScanner(
                new StringReader(MISSIONS_HEADER), new StringReader(ROCKETS_HEADER), secretKey
        );

        rocketFalcon9Block5 = Rocket.of(ROCKET_FALCON_9_BLOCK_5_DATA);
        rocketTsyklon3 = Rocket.of(ROCKET_TSYKLON_3);
        rocketProtonMBrizM = Rocket.of(ROCKET_PROTON_M_BRIZ_M_DATA);
        rocketLongMarch2D = Rocket.of(ROCKET_LONG_MARCH_2D_DATA);
        rocketStarshipPrototype = Rocket.of(ROCKET_STARSHIP_PROTOTYPE_DATA);

        mission0SpaceX = Mission.of(MISSION_0_SPACE_X);
        mission1Casc = Mission.of(MISSION_1_CASC);
        mission2SpaceX = Mission.of(MISSION_2_SPACE_X);
        mission3Roscosmos = Mission.of(MISSION_3_ROSCOSMOS);
        mission4Roscosmos = Mission.of(MISSION_4_ROSCOSMOS);
        mission5Roscosmos = Mission.of(MISSION_5_ROSCOSMOS);
    }

    @Test
    public void testGetAllMissions() {
        List<Mission> missions = List.of(mission0SpaceX,
                mission1Casc,
                mission2SpaceX,
                mission3Roscosmos,
                mission4Roscosmos,
                mission5Roscosmos);

        assertEquals(missions,
                spaceScanner.getAllMissions(),
                "When tested getAllMissions() with valid input data, the method didn't return what was expected.");
    }

    @Test
    public void testGetAllMissionsWithNoMissions() {
        assertEquals(Collections.emptyList(),
                emptySpaceScanner.getAllMissions(),
                "When tested getAllMissions() with no missions, the method didn't return empty collection.");
    }

    @Test
    public void testGetAllMissionsWithNullMissionStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> spaceScanner.getAllMissions(null),
                "When tested getAllMissions(missionStatus) with null, " +
                        "the method didn't throw IllegalArgumentException."
        );
    }

    @Test
    public void testGetAllMissionsWithMissionStatusWithNoMissions() {
        assertTrue(emptySpaceScanner.getAllMissions(MissionStatus.SUCCESS).isEmpty(),
                "When tested getAllMissions(missionStatus) with no missions, " +
                        "the method didn't return empty collection.");
    }

    @Test
    public void testGetAllMissionsWithMissionStatus() {
        List<Mission> expected = List.of(mission0SpaceX, mission2SpaceX, mission4Roscosmos);

        assertEquals(expected,
                spaceScanner.getAllMissions(MissionStatus.SUCCESS),
                "When tested getAllMissions(missionStatus) with success status, " +
                        "the method didn't return what was expected.");
    }

    @Test
    public void testGetCompanyWithMostSuccessfulMissionsWithNullFromDate() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getCompanyWithMostSuccessfulMissions(null, LocalDate.of(2019, 1, 1)),
                "When tested getCompanyWithMostSuccessfulMissions(...) with null from date, " +
                        "the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetCompanyWithMostSuccessfulMissionsWithNullToDate() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getCompanyWithMostSuccessfulMissions(LocalDate.of(2019, 1, 1), null),
                "When tested getCompanyWithMostSuccessfulMissions(...) with null to date, " +
                        "the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetCompanyWithMostSuccessfulMissionsWithInvalidTimeFrame() {
        assertThrows(TimeFrameMismatchException.class,
                () -> spaceScanner.getCompanyWithMostSuccessfulMissions(
                        LocalDate.of(2019, 1, 1), LocalDate.of(2018, 1, 1)
                ),
                "When tested getCompanyWithMostSuccessfulMissions(...) with invalid time frame, the method didn't throw TimeFrameMismatchException.");
    }


    @Test
    public void testGetCompanyWithMostSuccessfulMissionsWithNoMissionsInTimeFrame() {
        assertTrue(spaceScanner.getCompanyWithMostSuccessfulMissions(
                        LocalDate.of(2010, 1, 1), LocalDate.of(2011, 1, 1)
                ).isEmpty(),
                "When tested getCompanyWithMostSuccessfulMissions(...) with no missions in time frame, " +
                        "the method didn't return empty string.");
    }

    @Test
    public void testGetCompanyWithMostSuccessfulMissions() {
        assertEquals("Roscosmos",
                spaceScanner.getCompanyWithMostSuccessfulMissions(LocalDate.of(2060, 1, 1), LocalDate.of(2061, 1, 1)),
                "When tested getCompanyWithMostSuccessfulMissions(...), the method didn't return what was expected.");
    }

    @Test
    public void testGetMissionsPerCountry() {
        Map<String, Collection<Mission>> expected = Map.of(
                COUNTRY_USA, Set.of(mission0SpaceX, mission2SpaceX),
                COUNTRY_CHINA, Set.of(mission1Casc),
                COUNTRY_BULGARIA, Set.of(mission4Roscosmos),
                COUNTRY_KAZAKHSTAN, Set.of(mission3Roscosmos, mission5Roscosmos)
        );

        Map<String, Collection<Mission>> actual = spaceScanner.getMissionsPerCountry();

        assertEquals(spaceScanner.getMissionsPerCountry(),
                expected,
                "When tested getMissionsPerCountry(), the method didn't return what was expected.");
    }

    @Test
    public void testGetMissionsPerCountryWithNoMissions() {
        assertTrue(emptySpaceScanner.getMissionsPerCountry().isEmpty(),
                "When tested getMissionsPerCountry(), the method didn't return what was expected.");
    }

    @Test
    public void testGetTopNLeastExpensiveMissionsWithNullMissionStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getTopNLeastExpensiveMissions(1, null, RocketStatus.STATUS_ACTIVE),
                "When tested getTopNLeastExpensiveMissions(...) with null mission status," +
                        " the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetTopNLeastExpensiveMissionsWithNullRocketStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, null),
                "When tested getTopNLeastExpensiveMissions(...) with null rocket status," +
                        " the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetTopNLeastExpensiveMissionsWithNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getTopNLeastExpensiveMissions(-1, MissionStatus.SUCCESS, null),
                "When tested getTopNLeastExpensiveMissions(...) with negative n," +
                        " the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetTopNLeastExpensiveMissions() {
        List<Mission> expected = List.of(mission0SpaceX);

        assertEquals(expected,
                spaceScanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "When tested getTopNLeastExpensiveMissions(...) with negative n," +
                        " the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetTopNLeastExpensiveMissionsWithNoMissions() {
        assertEquals(Collections.emptyList(),
                emptySpaceScanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE),
                "When tested getTopNLeastExpensiveMissions(...) with no missions," +
                        " the method didn't return empty list.");
    }

    @Test
    public void testGetTopNLeastExpensiveMissionsWithNoMissionsEqualToInput() {
        assertEquals(Collections.emptyList(),
                spaceScanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, RocketStatus.STATUS_RETIRED),
                "When tested getTopNLeastExpensiveMissions(...) with no missions equal to input," +
                        " the method didn't return empty list.");
    }

    @Test
    public void testGetMostDesiredLocationForMissionsPerCompanyWithNoMissions() {
        assertEquals(Collections.emptyMap(),
                emptySpaceScanner.getMostDesiredLocationForMissionsPerCompany(),
                "When tested getMostDesiredLocationForMissionsPerCompany() with no missions," +
                        " the method didn't return empty map.");
    }

    @Test
    public void testGetMostDesiredLocationForMissionsPerCompany() {
        Map<String, String> expected = Map.of(
                "CASC", MISSION_1_LOCATION,
                "SpaceX", MISSION_0_LOCATION,
                "Roscosmos", MISSION_5_LOCATION
        );

        assertEquals(expected,
                spaceScanner.getMostDesiredLocationForMissionsPerCompany(),
                "When tested getMostDesiredLocationForMissionsPerCompany()," +
                        " the method didn't return what was expected.");
    }

    @Test
    public void testGetLocationWithMostSuccessfulMissionsPerCompanyWithNullFromDate() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(null, LocalDate.of(2019, 1, 1)),
                "When tested getLocationWithMostSuccessfulMissionsPerCompany(...) with null from date," +
                        " the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetLocationWithMostSuccessfulMissionsPerCompanyWithNullToDate() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.of(2019, 1, 1), null),
                "When tested getLocationWithMostSuccessfulMissionsPerCompany(...) with null to date," +
                        " the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetLocationWithMostSuccessfulMissionsPerCompanyWithInvalidTimeFrame() {
        assertThrows(TimeFrameMismatchException.class,
                () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(
                        LocalDate.of(2019, 1, 1), LocalDate.of(2018, 1, 1)
                ),
                "When tested getLocationWithMostSuccessfulMissionsPerCompany(...) with invalid timeframe," +
                        " the method didn't throw TimeFrameMismatchException.");
    }

    @Test
    public void testGetLocationWithMostSuccessfulMissionsPerCompanyWithNoMissionsInTimeFrame() {
        assertEquals(Collections.emptyMap(),
                spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(
                        LocalDate.of(2100, 1, 1), LocalDate.of(2100, 2, 1)
                ),
                "When tested getLocationWithMostSuccessfulMissionsPerCompany(...) with no missions in time frame," +
                        " the method didn't return empty map.");
    }

    @Test
    public void testGetLocationWithMostSuccessfulMissionsPerCompanyWithNoMissions() {
        assertEquals(Collections.emptyMap(),
                emptySpaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(
                        LocalDate.of(2100, 1, 1), LocalDate.of(2100, 2, 1)
                ),
                "When tested getLocationWithMostSuccessfulMissionsPerCompany(...) with no missions," +
                        " the method didn't return empty map.");
    }

    @Test
    public void testGetLocationWithMostSuccessfulMissionsPerCompany() {
        Map<String, String> expected = Map.of(
                "SpaceX", MISSION_0_LOCATION,
                "Roscosmos", MISSION_4_LOCATION
        );

        assertEquals(expected,
                spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(
                        LocalDate.of(2020, 1, 1), LocalDate.of(2061, 1, 1)
                ),
                "When tested getLocationWithMostSuccessfulMissionsPerCompany(...) with no missions," +
                        " the method didn't return empty map.");
    }

    @Test
    public void testGetAllRockets() {
        Collection<Rocket> rockets = List.of(
                rocketFalcon9Block5,
                rocketLongMarch2D,
                rocketStarshipPrototype,
                rocketProtonMBrizM,
                rocketTsyklon3
        );

        assertTrue(rockets.containsAll(spaceScanner.getAllRockets()),
                "When tested getRockets(), the method didn't return what was expected.");
    }

    @Test
    public void testGetAllRocketsWithNoMissions() {
        assertTrue(emptySpaceScanner.getAllRockets().isEmpty(),
                "When tested getRockets() with no missions, the method didn't return empty collection.");
    }

    @Test
    public void testGetTopNTallestRocketsWithNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getTopNTallestRockets(-1),
                "When tested getTopNTallestRockets(...) with negative n, " +
                        "the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetTopNTallestRocketsWithZero() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getTopNTallestRockets(0),
                "When tested getTopNTallestRockets(...) with negative 0, " +
                        "the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetTopNTallestRockets() {
        assertTrue(List.of(rocketFalcon9Block5, rocketProtonMBrizM).containsAll(spaceScanner.getTopNTallestRockets(2)),
                "When tested getTopNTallestRockets(...), the method didn't return what was expected.");
    }

    @Test
    public void testGetTopNTallestRocketsWithNoRockets() {
        assertTrue(emptySpaceScanner.getTopNTallestRockets(2).isEmpty(),
                "When tested getTopNTallestRockets(...) with no missions, " +
                        "the method didn't return empty collection.");
    }

    @Test
    public void testGetWikiPageForRocket() {
        Map<String, Optional<String>> expected = Map.of(
                rocketTsyklon3.name(), Optional.empty(),
                rocketFalcon9Block5.name(), Optional.of(WIKI_FALCON_9_BLOCK_5_DATA),
                rocketLongMarch2D.name(), Optional.of(WIKI_LONG_MARCH_2D),
                rocketProtonMBrizM.name(), Optional.of(WIKI_PROTON_M_BRIZ_M),
                rocketStarshipPrototype.name(), Optional.of(WIKI_STARSHIP_PROTOTYPE)
        );

        assertEquals(spaceScanner.getWikiPageForRocket(),
                expected,
                "When tested getWikiPageForRocket(), " +
                        "the method didn't return what was expected.");
    }

    @Test
    public void testGetWikiPageForRocketWithNoMissions() {
        Map<String, Optional<String>> expected = Map.of(
                rocketTsyklon3.name(), Optional.empty(),
                rocketFalcon9Block5.name(), Optional.of(WIKI_FALCON_9_BLOCK_5_DATA),
                rocketLongMarch2D.name(), Optional.of(WIKI_LONG_MARCH_2D),
                rocketProtonMBrizM.name(), Optional.of(WIKI_PROTON_M_BRIZ_M),
                rocketStarshipPrototype.name(), Optional.of(WIKI_STARSHIP_PROTOTYPE)
        );

        assertTrue(emptySpaceScanner.getWikiPageForRocket().isEmpty(),
                "When tested getWikiPageForRocket() with no missions, " +
                        "the method didn't return empty map.");
    }

    @Test
    public void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        -1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE
                ),
                "When tested getWikiPagesForRocketsUsedInMostExpensiveMissions(...) with negative n, " +
                        "the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithZero() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE
                ),
                "When tested getWikiPagesForRocketsUsedInMostExpensiveMissions(...) with 0, " +
                        "the method didn't throw IllegalArgumentException.");
    }


    @Test
    public void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithNullMissionStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        1, null, RocketStatus.STATUS_ACTIVE
                ),
                "When tested getWikiPagesForRocketsUsedInMostExpensiveMissions(...) with null mission status, " +
                        "the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithNullRocketStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        1, MissionStatus.SUCCESS, null
                ),
                "When tested getWikiPagesForRocketsUsedInMostExpensiveMissions(...) with null rocket status, " +
                        "the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsWithNoMissions() {
        assertTrue(emptySpaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE
                ).isEmpty(),
                "When tested getWikiPagesForRocketsUsedInMostExpensiveMissions(...) with no missions, " +
                        "the method didn't return empty list.");
    }

    @Test
    public void testGetWikiPagesForRocketsUsedInMostExpensiveMissions() {
        assertEquals(List.of(WIKI_FALCON_9_BLOCK_5_DATA),
                spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                        2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE
                ),
                "When tested getWikiPagesForRocketsUsedInMostExpensiveMissions(...) with no missions, " +
                        "the method didn't return empty list.");
    }

    @Test
    public void testSaveMostReliableRocketWithNoRockets() {
        String expected = "";
        Rijndael rijndael = new Rijndael(secretKey);

        try (ByteArrayOutputStream actualOutputStream = new ByteArrayOutputStream();
             ByteArrayOutputStream expectedOutputStream = new ByteArrayOutputStream()) {
            emptySpaceScanner.saveMostReliableRocket(
                    actualOutputStream, LocalDate.of(2019, 1, 1), LocalDate.of(2020, 1, 1)
            );
            rijndael.encrypt(new ByteArrayInputStream(expected.getBytes()), expectedOutputStream);

            expected = expectedOutputStream.toString();
            assertEquals(expected,
                    actualOutputStream.toString(),
                    "When tested saveMostReliableRocket(...) with no rockets, " +
                            "the method didn't return empty encrypted string.");
        } catch (IOException | CipherException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSaveMostReliableRocketWithNullFromDate() {
        try (ByteArrayOutputStream actualOutputStream = new ByteArrayOutputStream()) {

            assertThrows(IllegalArgumentException.class,
                    () -> spaceScanner.saveMostReliableRocket(actualOutputStream, null, LocalDate.of(2019, 1, 1)),
                    "When tested saveMostReliableRocket(...) with null from date, " +
                            "the method didn't throw IllegalArgumentException.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSaveMostReliableRocketWithNullToDate() {
        try (ByteArrayOutputStream actualOutputStream = new ByteArrayOutputStream()) {
            assertThrows(IllegalArgumentException.class,
                    () -> spaceScanner.saveMostReliableRocket(actualOutputStream, LocalDate.of(2019, 1, 1), null),
                    "When tested saveMostReliableRocket(...) with null to date, " +
                            "the method didn't throw IllegalArgumentException.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSaveMostReliableRocketWithNullOutputStream() {
        assertThrows(IllegalArgumentException.class,
                () -> spaceScanner.saveMostReliableRocket(null, LocalDate.of(2019, 1, 1), LocalDate.of(2019, 2, 1)),
                "When tested saveMostReliableRocket(...) with null output stream, " +
                        "the method didn't throw IllegalArgumentException.");
    }

    @Test
    public void testSaveMostReliableRocketWithInvalidTimeFrame() {
        try (ByteArrayOutputStream actualOutputStream = new ByteArrayOutputStream()) {
            assertThrows(TimeFrameMismatchException.class,
                    () -> spaceScanner.saveMostReliableRocket(
                            actualOutputStream, LocalDate.of(2029, 1, 1), LocalDate.of(2019, 1, 1)
                    ),
                    "When tested saveMostReliableRocket(...) with invalid time frame, " +
                            "the method didn't throw TimeFrameMismatchException.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSaveMostReliableRocket() {
        String expected = "Falcon 9 Block 5";
        Rijndael rijndael = new Rijndael(secretKey);

        try (ByteArrayOutputStream actualOutputStream = new ByteArrayOutputStream();
             ByteArrayOutputStream expectedOutputStream = new ByteArrayOutputStream()) {
            spaceScanner.saveMostReliableRocket(
                    actualOutputStream, LocalDate.of(2060, 1, 1), LocalDate.of(2062, 1, 1)
            );
            rijndael.encrypt(new ByteArrayInputStream(expected.getBytes()), expectedOutputStream);

            expected = expectedOutputStream.toString();
            assertEquals(expected,
                    actualOutputStream.toString(),
                    "When tested saveMostReliableRocket(...), " +
                            "the method didn't return expected result.");
        } catch (IOException | CipherException e) {
            throw new RuntimeException(e);
        }
    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey;
    }
}
