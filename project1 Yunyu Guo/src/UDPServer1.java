import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class UDPServer1 {
    private static Map<String, String> store = new HashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            log("Usage: java UDPServer <port>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        System.out.println("wait for client connect...");

        // Initialize the HashMap with 5 keys and values
        for (int i = 1; i <= 5; i++) {
            store.put("key" + i, "value" + i);
        }

        try (DatagramSocket serverSocket = new DatagramSocket(portNumber)) {
            byte[] receiveData = new byte[1024];
            byte[] sendData;

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String inputLine = new String(receivePacket.getData(), 0, receivePacket.getLength());

                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                log("Received from " + IPAddress + ":" + port + ": " + inputLine);

                String[] parts = inputLine.split(" ");
                String response = "";

                // Handle GET requests
                if (parts[0].equals("GET") && parts.length > 1) {
                    String key = parts[1];
                    String value = store.get(key);
                    if (value != null) {
                        response = "SUCCESS: " + value;
                        log("Sent: SUCCESS: " + value);
                    } else {
                        response = "ERROR: key not found";
                        log("Sent: ERROR: key not found");
                    }
                }

                // Handle PUT requests
                else if (parts[0].equals("PUT") && parts.length > 2) {
                    String key = parts[1];
                    String value = parts[2];
                    store.put(key, value);
                    response = "SUCCESS";
                    log("Sent: SUCCESS");
                }

                // Handle DELETE requests
                else if (parts[0].equals("DELETE") && parts.length > 1) {
                    String key = parts[1];
                    if (store.containsKey(key)) {
                        store.remove(key);
                        response = "SUCCESS";
                        log("Sent: SUCCESS");
                    } else {
                        response = "ERROR: key not found";
                        log("Sent: ERROR: key not found");
                    }
                }

                // Handle GETALL requests
                else if (parts[0].equals("GETALL")) {
                    for (Map.Entry<String, String> entry : store.entrySet()) {
                        response += entry.getKey() + ": " + entry.getValue() + "\n";
                    }
                    response += "END"; // Send an "END" message after sending all key-value pairs
                    log("Sent all key-value pairs");
                }

                // Handle OVER requests
                else if (parts[0].equals("Over")) {
                    response = "END"; // Send an "END" message in response to "Over"
                    log("Sent: END");
                }

                sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
                // send "END" message after every response
                sendData = "END".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            }
        }
    }

    private static void log(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println(timestamp + ": " + message);
    }
}
