import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TCPServer1 {
    private static Map<String, String> store = new HashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            log("Usage: java TCPServer <port>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        System.out.println("wait for client connect...");

        // Initialize the HashMap with 5 keys and values
        for (int i = 1; i <= 5; i++) {
            store.put("key" + i, "value" + i);
        }

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    log("Connected to client at " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort());

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        log("Received: " + inputLine);
                        String[] parts = inputLine.split(" ");

                        // Handle GET requests
                        if (parts[0].equals("GET") && parts.length > 1) {
                            String key = parts[1];
                            String value = store.get(key);
                            if (value != null) {
                                out.println("SUCCESS: " + value);
                                out.println("END");
                                log("Sent: SUCCESS: " + value);
                            } else {
                                out.println("ERROR: key not found");
                                out.println("END");
                                log("Sent: ERROR: key not found");
                            }
                        }

                        // Handle PUT requests
                        else if (parts[0].equals("PUT") && parts.length > 2) {
                            String key = parts[1];
                            String value = parts[2];
                            store.put(key, value);
                            out.println("SUCCESS");
                            out.println("END");
                            log("Sent: SUCCESS");
                        }

                        // Handle DELETE requests
                        else if (parts[0].equals("DELETE") && parts.length > 1) {
                            String key = parts[1];
                            if (store.containsKey(key)) {
                                store.remove(key);
                                out.println("SUCCESS");
                                out.println("END");
                                log("Sent: SUCCESS");
                            } else {
                                out.println("ERROR: key not found");
                                out.println("END");
                                log("Sent: ERROR: key not found");
                            }
                        }

                        // Handle GETALL requests
                        else if (parts[0].equals("GETALL")) {
                            for (Map.Entry<String, String> entry : store.entrySet()) {
                                out.println(entry.getKey() + ": " + entry.getValue());
                            }
                            out.println("END"); // Send an "END" message after sending all key-value pairs
                            log("Sent all key-value pairs");
                        }

                        // Handle OVER requests
                        else if (parts[0].equals("Over")) {
                            out.println("END"); // Send an "END" message in response to "Over"
                            log("Sent: END");
                        }
                    }
                } catch (IOException e) {
                    log("Exception caught when trying to listen on port " + portNumber);
                    log(e.getMessage());
                }
            }
        }
    }

    private static void log(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println(timestamp + ": " + message);
    }
}
