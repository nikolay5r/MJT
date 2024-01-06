import bg.sofia.uni.fmi.mjt.gym.Gym;
import bg.sofia.uni.fmi.mjt.gym.GymCapacityExceededException;
import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.Gender;
import bg.sofia.uni.fmi.mjt.gym.member.Member;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Gym gym = new Gym(2, new Address(1,0));
        try {
            gym.addMember(new Member(new Address(1,2), "AS", 2, "asd", Gender.MALE));
        }
        catch (GymCapacityExceededException err) {
            System.out.println(err.getMessage());
        }

        try {
            gym.addMember(new Member(new Address(21,2), "ASSS", 2, "assdd", Gender.FEMALE));
        }
        catch (GymCapacityExceededException err) {
            System.out.println(err.getMessage());
        }

        try {
            gym.addMember(new Member(new Address(21,2), "ASSSSS", 2, "assdsdsdd", Gender.OTHER));
        }
        catch (GymCapacityExceededException err) {
            System.out.println(err.getMessage());
        }

    }

}