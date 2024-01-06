package bg.sofia.uni.fmi.mjt.gym.member;

import java.util.Comparator;

public class GymMemberByProximityToAddressComparator implements Comparator<GymMember> {
    private Address address;

    public GymMemberByProximityToAddressComparator(Address address) {
        this.address = address;
    }

    @Override
    public int compare(GymMember o1, GymMember o2) {
        double dist1 = o1.getAddress().getDistanceTo(address);
        double dist2 = o2.getAddress().getDistanceTo(address);

        return Double.compare(dist1, dist2);
    }
}
