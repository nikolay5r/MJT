package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MJTOrderRepository implements OrderRepository {

    private Collection<Order> allRequests;
    private Collection<Order> allSuccessfulRequests;
    private int countOfSuccessfulRequests;

    public MJTOrderRepository() {
        countOfSuccessfulRequests = 1;
        allRequests = new ArrayList<>();
        allSuccessfulRequests = new ArrayList<>();
    }

    private synchronized Response validRequest(Size size, Color color, Destination destination) {
        allRequests.add(new Order(countOfSuccessfulRequests, new TShirt(size, color), destination));
        allSuccessfulRequests.add(new Order(countOfSuccessfulRequests, new TShirt(size, color), destination));
        return Response.create(countOfSuccessfulRequests++);
    }

    private synchronized Response invalidRequest(Size size, Color color, Destination destination, String errorMessage) {
        allRequests.add(new Order(-1, new TShirt(size, color), destination));
        return Response.decline(errorMessage);
    }

    @Override
    public Response request(String size, String color, String destination) {
        if (size == null || color == null || destination == null) {
            throw new IllegalArgumentException("One of the input arguments is null.");
        }

        StringBuilder errorMessage = new StringBuilder("invalid: ");
        boolean isValid = true;

        Size tShirtSize = Size.of(size);
        if (tShirtSize == Size.UNKNOWN) {
            errorMessage.append("size");
            isValid = false;
        }

        Color tShirtColor = Color.of(color);
        if (tShirtColor == Color.UNKNOWN) {
            if (errorMessage.indexOf(",") == -1 && errorMessage.indexOf("size") != -1) {
                errorMessage.append(",");
            }
            errorMessage.append("color");
            isValid = false;
        }

        Destination orderDestination = Destination.of(destination);
        if (orderDestination == Destination.UNKNOWN) {
            if (errorMessage.indexOf(",") == -1 &&
                    (errorMessage.indexOf("size") != -1 || errorMessage.indexOf("color") != -1)) {
                errorMessage.append(",");
            }
            errorMessage.append("destination");
            isValid = false;
        }

        return isValid ? validRequest(tShirtSize, tShirtColor, orderDestination) :
                invalidRequest(tShirtSize, tShirtColor, orderDestination, errorMessage.toString());
    }

    @Override
    public Response getOrderById(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("Id cannot be non-positive number.");
        }

        for (Order order: allSuccessfulRequests) {
            if (order.id() == id) {
                return Response.ok(List.of(order));
            }
        }

        return Response.notFound(id);
    }

    @Override
    public Response getAllOrders() {
        return Response.ok(allRequests);
    }

    @Override
    public Response getAllSuccessfulOrders() {
        return Response.ok(allSuccessfulRequests);
    }
}
