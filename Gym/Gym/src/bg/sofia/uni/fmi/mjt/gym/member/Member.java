package bg.sofia.uni.fmi.mjt.gym.member;

import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Member implements GymMember, Comparable<GymMember>  {
    private String name;
    private Address address;
    private int age;
    private String personalIdNumber;
    private Gender gender;
    private Map<DayOfWeek, Workout> trainingProgram;

    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        return obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(personalIdNumber);
    }

    public Member(Address address, String name, int age, String personalIdNumber, Gender gender) {
        this.address = address;
        this.name = name;
        this.age = age;
        this.personalIdNumber = personalIdNumber;
        this.gender = gender;
        this.trainingProgram = new HashMap<>(NUMBER_OF_DAYS_IN_WEEK);
    }

    private void checkIfDayIsNull(DayOfWeek day) {
        if (day == null) {
            throw new IllegalArgumentException("DayOfWeek is null.");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getPersonalIdNumber() {
        return personalIdNumber;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Map<DayOfWeek, Workout> getTrainingProgram() {
        return Collections.unmodifiableMap(trainingProgram);
    }

    @Override
    public void setWorkout(DayOfWeek day, Workout workout) {
        checkIfDayIsNull(day);

        if (workout == null) {
            throw new IllegalArgumentException("Workout is null.");
        }

        trainingProgram.put(day, workout);
    }

    @Override
    public Collection<DayOfWeek> getDaysFinishingWith(String exerciseName) {
        if (exerciseName == null) {
            throw new IllegalArgumentException("Exercise name is null.");
        }

        if (exerciseName.isBlank()) {
            throw new IllegalArgumentException("Exercise name is empty.");
        }

        ArrayList<DayOfWeek> resultDays = new ArrayList<>();

        for (Map.Entry<DayOfWeek, Workout> workoutProgram: trainingProgram.entrySet()) {
            if (workoutProgram.getValue().exercises().getLast().name().equals(exerciseName)) {
                resultDays.add(workoutProgram.getKey());
            }
        }

        return resultDays;
    }

    @Override
    public void addExercise(DayOfWeek day, Exercise exercise) {
        checkIfDayIsNull(day);

        if (exercise == null) {
            throw new IllegalArgumentException("Exercise is null.");
        }

        Workout workoutProgram = trainingProgram.get(day);

        if (workoutProgram == null) {
            throw new DayOffException("Workout on this day does not exist.");
        }

        workoutProgram.exercises().add(exercise);
    }

    @Override
    public void addExercises(DayOfWeek day, List<Exercise> exercises) {
        checkIfDayIsNull(day);

        if (exercises == null) {
            throw new IllegalArgumentException("Exercises variable is null.");
        }

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("List of exercises is empty.");
        }

        Workout workoutProgram = trainingProgram.get(day);

        if (workoutProgram == null) {
            throw new DayOffException("Workout on this day does not exist.");
        }

        workoutProgram.exercises().addAll(exercises);
    }

    @Override
    public int compareTo(GymMember obj) {
        if (obj == this) {
            return 0;
        }

        if (obj == null) {
            return -1;
        }

        return personalIdNumber.compareTo(obj.getPersonalIdNumber());
    }

}