import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ShopClient {

    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("Client-" + socket.getLocalPort());

            String line = "";
            while (!line.equals("disconnect")) {
                line = scanner.nextLine();

                writer.println(line);

                String reply = reader.readLine();

                System.out.println(reply);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Something went wrong when trying to connect to server on port: " + SERVER_PORT + ".",
                    e
            );
        }
    }
}

