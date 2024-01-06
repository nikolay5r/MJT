package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.order.Order;

import java.util.Collection;
import java.util.Objects;

public record Response(Status status, String additionalInfo, Collection<Order> orders) {
    private enum Status {
        OK, CREATED, DECLINED, NOT_FOUND
    }

    /**
     * Creates a response
     *
     * @param id order id
     * @return response with status Status.CREATED and with proper message for additional info
     */
    public static Response create(int id) {
        return new Response(Status.CREATED, "ORDER_ID=" + id, null);
    }


    /**
     * Creates a response
     *
     * @param orders the orders which will be returned to the client
     * @return response with status Status.OK and Collection of orders
     */
    public static Response ok(Collection<Order> orders) {
        return new Response(Status.OK, null, orders);
    }

    /**
     * Creates a response
     *
     * @param errorMessage the message which will be sent as additionalInfo
     * @return response with status Status.DECLINED and errorMessage as additionalInfo
     */
    public static Response decline(String errorMessage) {
        return new Response(Status.DECLINED, errorMessage, null);
    }

    /**
     * Creates a response
     *
     * @param id order id
     * @return response with status Status.NOT_FOUND and with proper message for additional info
     */
    public static Response notFound(int id) {
        return new Response(Status.NOT_FOUND, "Order with id = " + id + " does not exist.", null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return status == response.status && Objects.equals(additionalInfo, response.additionalInfo) && Objects.equals(orders, response.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, additionalInfo, orders);
    }

    @Override
    public String toString() {
        StringBuilder responseMessage = new StringBuilder();

        responseMessage.append("{\"status\":\"").append(status).append("\"");

        if (additionalInfo != null) {
            responseMessage.append(", \"additionalInfo\":\"").append(additionalInfo).append("\"");
        }

        if (orders != null) {
            responseMessage.append(", \"orders\":").append(orders);
        }

        responseMessage.append("}");

        return responseMessage.toString();
    }
}