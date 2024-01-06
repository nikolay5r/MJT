package bg.sofia.uni.fmi.mjt.order.server.shop;

import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;
import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;

import java.io.IOException;
import java.net.ServerSocket;

public class ShopServer {

    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server has started on port: " + SERVER_PORT + ". Ready to listen.");

            OrderRepository orderRepository = new MJTOrderRepository();
            while (true) {
                new Thread(new ShopClientRequestHandler(server.accept(), orderRepository)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
