package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;
import bg.sofia.uni.fmi.mjt.gym.member.GymMemberByNameComparator;
import bg.sofia.uni.fmi.mjt.gym.member.GymMemberByProximityToAddressComparator;
import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class Gym implements GymAPI {
    private Address address;
    private TreeSet<GymMember> members;
    private int capacity;

    public Gym(int capacity, Address address) {
        this.capacity = capacity;
        this.members = new TreeSet<>();
        this.address = address;
    }

    @Override
    public SortedSet<GymMember> getMembers() {
        return members;
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByName() {
        TreeSet<GymMember> membersSortedByName = new TreeSet<>(new GymMemberByNameComparator());

        if (members == null || members.isEmpty())
            return membersSortedByName;

        membersSortedByName.addAll(members);

        return Collections.unmodifiableSortedSet(membersSortedByName);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByProximityToGym() {
        TreeSet<GymMember> membersSortedByProximity = new TreeSet<>(
                new GymMemberByProximityToAddressComparator(address)
        );

        if (members == null || members.isEmpty())
            return membersSortedByProximity;

        membersSortedByProximity.addAll(members);

        return Collections.unmodifiableSortedSet(membersSortedByProximity);
    }

    @Override
    public void addMember(GymMember member) throws GymCapacityExceededException {
        if (member == null) {
            throw new IllegalArgumentException("Member variable is null.");
        }

        if (members.size() == capacity) {
            throw new GymCapacityExceededException("You have reached the capacity of the gym.");
        }

        members.add(member);
    }

    @Override
    public void addMembers(Collection<GymMember> members) throws GymCapacityExceededException {
        if (members == null) {
            throw new IllegalArgumentException("Members variable is null.");
        }

        if (members.isEmpty()) {
            throw new IllegalArgumentException("Collection of members is empty.");
        }

        if (members.size() + this.members.size() > capacity) {
            throw new GymCapacityExceededException("You have reached the capacity of the gym.");
        }

        this.members.addAll(members);
    }

    @Override
    public boolean isMember(GymMember member) {
        if (member == null) {
            throw new IllegalArgumentException("Member variable is null.");
        }

        return members.contains(member);
    }

    @Override
    public boolean isExerciseTrainedOnDay(String exerciseName, DayOfWeek day) {
        if (exerciseName == null) {
            throw new IllegalArgumentException("ExerciseName variable is null.");
        }

        if (day == null) {
            throw new IllegalArgumentException("Day variable is null.");
        }

        if (exerciseName.isBlank()) {
            throw new IllegalArgumentException("Exercise name is empty.");
        }

        for (GymMember member: members) {
            if (member.getTrainingProgram().containsKey(day)) {
                if (member.getTrainingProgram().get(day).exercises().contains(new Exercise(exerciseName, 0 , 0))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Map<DayOfWeek, List<String>> getDailyListOfMembersForExercise(String exerciseName) {
        if (exerciseName == null) {
            throw new IllegalArgumentException("ExerciseName variable is null.");
        }

        if (exerciseName.isBlank()) {
            throw new IllegalArgumentException("Exercise name is empty.");
        }

        HashMap<DayOfWeek, List<String>> dailyList = new HashMap<>();

        for (GymMember member: members) {
            for (Map.Entry<DayOfWeek, Workout> dailyTrainingProgram: member.getTrainingProgram().entrySet()) {
                if (dailyTrainingProgram.getValue().exercises().contains(new Exercise(exerciseName, 0, 0))) {
                    if (dailyList.containsKey(dailyTrainingProgram.getKey())) {
                        dailyList.get(dailyTrainingProgram.getKey()).addLast(member.getName());
                    } else {
                        dailyList.put(dailyTrainingProgram.getKey(), new LinkedList<>());
                    }
                    break;
                }
            }
        }

        return dailyList;
    }
}
