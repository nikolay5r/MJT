package bg.sofia.uni.fmi.mjt.order.server.shop;

import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ShopClientRequestHandler implements Runnable {

    private static final String REQUEST_KEYWORD = "request";
    private static final String GET_KEYWORD = "get";
    private static final String DISCONNECT_KEYWORD = "disconnect";
    private static final String ALL_REQUESTS_KEYWORD = "all";
    private static final String ALL_SUCCESSFUL_REQUESTS_KEYWORD = "all-successful";
    private static final String MY_ORDER_KEYWORD = "my-order";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command";
    private static final String DISCONNECT_COMMAND_MESSAGE = "Disconnected from the server";
    private static final String INPUT_DATA_DELIMITER = " ";
    private static final String PROPERTIES_DELIMITER = "=";
    private static final String SIZE_KEYWORD = "size";
    private static final String DESTINATION_KEYWORD = "destination";
    private static final String COLOR_KEYWORD = "color";

    private static final int REQUEST_DATA_MAXIMUM_COUNT_OF_WORDS = 4;
    private static final int GET_DATA_MAXIMUM_COUNT_OF_WORDS = 3;
    private static final int GET_DATA_MINIMUM_COUNT_OF_WORDS = 2;
    private static final int PROPERTIES_MAXIMUM_COUNT_OF_WORDS = 2;

    private Socket socket;
    private OrderRepository orderRepository;

    private boolean quit = false;

    public ShopClientRequestHandler(Socket socket, OrderRepository orderRepository) {
        this.socket = socket;
        this.orderRepository = orderRepository;
    }

    private String requestFromRepository(String[] data) {
        if (data.length != REQUEST_DATA_MAXIMUM_COUNT_OF_WORDS) {
            return UNKNOWN_COMMAND_MESSAGE;
        }

        String[] sizeData = data[1].split(PROPERTIES_DELIMITER);
        if (!sizeData[0].equals(SIZE_KEYWORD) || sizeData.length != PROPERTIES_MAXIMUM_COUNT_OF_WORDS) {
            return UNKNOWN_COMMAND_MESSAGE;
        }

        String[] colorData = data[2].split(PROPERTIES_DELIMITER);
        if (!colorData[0].equals(COLOR_KEYWORD) || colorData.length != PROPERTIES_MAXIMUM_COUNT_OF_WORDS) {
            return UNKNOWN_COMMAND_MESSAGE;
        }

        String[] destinationData = data[3].split(PROPERTIES_DELIMITER);
        if (!destinationData[0].equals(DESTINATION_KEYWORD)
                || destinationData.length != PROPERTIES_MAXIMUM_COUNT_OF_WORDS) {
            return UNKNOWN_COMMAND_MESSAGE;
        }

        return orderRepository.request(sizeData[1], colorData[1], destinationData[1]).toString();
    }

    private String getFromRepository(String[] data) {
        if (data.length < GET_DATA_MINIMUM_COUNT_OF_WORDS || data.length > GET_DATA_MAXIMUM_COUNT_OF_WORDS) {
            return UNKNOWN_COMMAND_MESSAGE;
        }

        if (data[1].equals(ALL_REQUESTS_KEYWORD)) {
            return orderRepository.getAllOrders().toString();
        }

        if (data[1].equals(ALL_SUCCESSFUL_REQUESTS_KEYWORD)) {
            return orderRepository.getAllSuccessfulOrders().toString();
        }

        if (data[1].equals(MY_ORDER_KEYWORD)) {
            if (data.length != GET_DATA_MAXIMUM_COUNT_OF_WORDS) {
                return UNKNOWN_COMMAND_MESSAGE;
            }

            String[] idData = data[2].split("=");

            if (idData.length != PROPERTIES_MAXIMUM_COUNT_OF_WORDS || !idData[0].equals("id")) {
                return UNKNOWN_COMMAND_MESSAGE;
            }

            try {
                return orderRepository.getOrderById(Integer.parseInt(idData[1])).toString();
            } catch (NumberFormatException e) {
                return UNKNOWN_COMMAND_MESSAGE;
            }
        }

        return UNKNOWN_COMMAND_MESSAGE;
    }

    private String disconnect(String[] data) {
        if (data.length != 1) {
            return UNKNOWN_COMMAND_MESSAGE;
        }

        quit = true;
        return DISCONNECT_COMMAND_MESSAGE;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("Client handler is waiting for requests from server.");

            Thread.currentThread().setName("ClientHandler" + Thread.activeCount());

            String inputLine = "";
            while ((inputLine = reader.readLine()) != null && !quit) {
                System.out.println("Message received: \"" + inputLine + "\".");

                String[] input = inputLine.split(INPUT_DATA_DELIMITER);

                String responseMessage = switch (input[0]) {
                    case REQUEST_KEYWORD -> requestFromRepository(input);
                    case GET_KEYWORD -> getFromRepository(input);
                    case DISCONNECT_KEYWORD -> disconnect(input);
                    default -> UNKNOWN_COMMAND_MESSAGE;
                };

                writer.println(responseMessage);
                System.out.println("Message sent: \"" + responseMessage + "\".");
            }
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong when opening the input/output streams.", e);
        } finally {
            try {
                socket.close();
                System.out.println("Client disconnected from the server.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
