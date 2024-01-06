package bg.sofia.uni.fmi.mjt.order.server.order;

import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.Objects;

public record Order(int id, TShirt tShirt, Destination destination) {
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Order) {
            return ((Order) obj).id() == id;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{\"id\":" + id + ", \"tShirt\":" + tShirt.toString() + ", \"destination\":\"" + destination + "\"}";
    }
}
