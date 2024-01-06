package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {
    private Collection<Mission> missions;
    private Collection<Rocket> rockets;
    private SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        this.secretKey = secretKey;

        try (BufferedReader bufferedMissionsReader = new BufferedReader(missionsReader);
             BufferedReader bufferedRocketsReader = new BufferedReader(rocketsReader)) {
            missions = bufferedMissionsReader.lines().skip(1).map(Mission::of).toList();
            rockets = bufferedRocketsReader.lines().skip(1).map(Rocket::of).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Something went wrong when opening files.", e);
        }
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return missions;
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status is null.");
        }

        return missions.stream().filter(mission -> mission.missionStatus().equals(missionStatus)).toList();
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null) {
            throw new IllegalArgumentException("From date is null.");
        }

        if (to == null) {
            throw new IllegalArgumentException("To date is null.");
        }

        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("From date cannot be after to date.");
        }

        Optional<Map.Entry<String, List<Mission>>> result = missions.stream()
                .filter(mission -> mission.missionStatus().equals(MissionStatus.SUCCESS) &&
                        isDateBetween(mission.date(), from, to))
                .collect(Collectors.groupingBy(Mission::company))
                .entrySet()
                .stream()
                .max(Comparator.comparing(entry -> entry.getValue().size()));

        return result.isPresent() ? result.get().getKey() : "";
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream()
                .collect(Collectors.groupingBy(
                        mission -> mission.location().substring(mission.location().lastIndexOf(",") + 1).strip(),
                        Collectors.toCollection(HashSet::new)
                ));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status is null.");
        }

        if (rocketStatus == null) {
            throw new IllegalArgumentException("Rocket status is null.");
        }

        if (n < 1) {
            throw new IllegalArgumentException("N is equal or less than 0.");
        }

        return missions.stream()
                .filter(
                        mission -> mission.missionStatus().equals(missionStatus) &&
                                mission.rocketStatus().equals(rocketStatus) &&
                                mission.cost().isPresent()
                )
                .sorted(Comparator.comparing(mission -> mission.cost().get()))
                .limit(n)
                .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        return missions.stream()
                .collect(Collectors.groupingBy(Mission::company))
                .entrySet()
                .stream()
                .map(entry -> Map.entry(
                        entry.getKey(),
                        entry.getValue()
                                .stream()
                                .collect(
                                        Collectors.groupingBy(
                                                Mission::location,
                                                Collectors.counting()
                                        )
                                )
                                .entrySet()
                                .stream()
                                .max(Map.Entry.comparingByValue())
                                .get()
                                .getKey()
                        )
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("One of the input dates is null.");
        }

        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("From date cannot be after to date.");
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus().equals(MissionStatus.SUCCESS) &&
                        isDateBetween(mission.date(), from, to))
                .collect(Collectors.groupingBy(Mission::company)).entrySet().stream()
                .map(entry -> Map.entry(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .collect(Collectors.groupingBy(Mission::location, Collectors.counting()))
                                        .entrySet().stream()
                                        .max(Map.Entry.comparingByValue())
                                        .orElse(Map.entry("-", 0L))
                                        .getKey()
                        )
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return rockets;
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("N is less or equal to 0.");
        }

        return rockets.stream()
                .filter(rocket -> rocket.height().isPresent())
                .sorted(Comparator.comparing(rocket -> rocket.height().get()))
                .toList()
                .reversed()
                .stream()
                .limit(n)
                .toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream()
                .collect(Collectors.toMap(
                        Rocket::name,
                        Rocket::wiki
                ));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(
            int n,
            MissionStatus missionStatus,
            RocketStatus rocketStatus
    ) {
        if (missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("One of the statuses is null.");
        }

        if (n < 1) {
            throw new IllegalArgumentException("N is less or equal to 0.");
        }

        List<String> rocketNamesInNMostExpensiveMissions = missions.stream()
                .filter(
                        mission -> mission.missionStatus().equals(missionStatus) &&
                                mission.rocketStatus().equals(rocketStatus) &&
                                mission.cost().isPresent()
                )
                .sorted(Comparator.comparing(mission -> mission.cost().get()))
                .toList()
                .reversed()
                .stream()
                .limit(n)
                .map(mission -> mission.detail().rocketName())
                .toList();

        return rockets.stream()
                .filter(rocket -> rocketNamesInNMostExpensiveMissions.contains(rocket.name()) &&
                        rocket.wiki().isPresent())
                .distinct()
                .map(rocket -> rocket.wiki().get())
                .toList();
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {

        if (outputStream == null || from == null || to == null) {
            throw new IllegalArgumentException("Input parameters are not valid.");
        }

        if (from.isAfter(to)) {
            throw new TimeFrameMismatchException("From date cannot be after to date.");
        }

        Rijndael rijndael = new Rijndael(secretKey);
        if (rockets.isEmpty()) {
            rijndael.encrypt(new ByteArrayInputStream("".getBytes()), outputStream);
            return;
        }

        Map<String, Integer> rocketRealibilities = getRocketRealibilities(from, to);

        String mostReliableRocketName = rocketRealibilities.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();

        rijndael.encrypt(new ByteArrayInputStream(mostReliableRocketName.getBytes()), outputStream);
    }

    private boolean isDateBetween(LocalDate date, LocalDate from, LocalDate to) {
        return (date.isAfter(from) && date.isBefore(to)) ||
                date.isEqual(from) || date.isEqual(to);
    }

    private Map<String, Integer> getRocketRealibilities(LocalDate from, LocalDate to) {
        List<String> rocketNames = rockets.stream().map(Rocket::name).toList();
        Map<String, Integer> rocketsSuccessfulMissions = new HashMap<>();
        Map<String, Integer> rocketsAllMissions = new HashMap<>();
        Map<String, Integer> rocketRealibilities = new HashMap<>();
        rocketNames.forEach(name -> {
            rocketsAllMissions.putIfAbsent(name, 0);
            rocketsSuccessfulMissions.putIfAbsent(name, 0); });
        missions.stream().filter(mission ->isDateBetween(mission.date(), from, to)).forEach(mission -> {
            if (mission.missionStatus().equals(MissionStatus.SUCCESS)) {
                rocketsSuccessfulMissions.put(mission.detail().rocketName(),
                        rocketsSuccessfulMissions.get(mission.detail().rocketName()) + 1
                );
            }
            rocketsAllMissions.put(mission.detail().rocketName(),
                    rocketsAllMissions.get(mission.detail().rocketName()) + 1
            );
        });
        rocketNames.forEach(name -> {
            if (rocketsAllMissions.get(name) != 0) {
                rocketRealibilities.put(name, ((2 * rocketsSuccessfulMissions.get(name)) +
                        (rocketsAllMissions.get(name) - rocketsSuccessfulMissions.get(name))) /
                        (2 * rocketsAllMissions.get(name))
                );
            } else {
                rocketRealibilities.put(name, 0);
            }
        });
        return rocketRealibilities;
    }
}
