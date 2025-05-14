import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TCPClient1 {
    // initialize socket and input output streams
    private Socket socket = null;
    private BufferedReader input = null;
    private PrintWriter out = null;

    // constructor to put ip address and port
    public TCPClient1(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);
            socket.setSoTimeout(5000); // set a timeout for reading from the server
            log("Connected");

            // takes input from terminal
            input = new BufferedReader(new InputStreamReader(System.in));

            // sends output to the socket
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (UnknownHostException u) {
            log(u.getMessage());
            return;
        } catch (IOException i) {
            log(i.getMessage());
            return;
        }

        // string to read message from input
        String line = "";

        // keep reading until "Over" is input
        while (!line.equals("Over")) {
            try {
                System.out.println("Enter a command: PUT key value, GET key, DELETE key, GETALL or 'Over' to quit: ");
                line = input.readLine();
                out.println(line);
                log("Sent: " + line);

                // read response from server
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response;
                while ((response = in.readLine()) != null && !response.equals("END")) {
                    if (!validResponse(response)) {
                        log("Received malformed or unrequested packet: " + response);
                        continue;
                    }
                    log("Received: " + response);
                }

            } catch (SocketTimeoutException e) {
                log("Error: server time out");
            } catch (IOException i) {
                log(i.getMessage());
            }
        }

        // close the connection
        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            log(i.getMessage());
        }
    }

    private static boolean validResponse(String response) {
        // check if response is null
        if (response == null) {
            return false;
        }

        // check if response is one of the expected responses
        if (response.equals("SUCCESS") || response.equals("ERROR: key not found")) {
            return true;
        }

        // If the response is not one of the above expected responses, it should be a value returned by a GET command
        // assume that values are non-empty strings

        return !response.isEmpty();
    }

    // Every line the client prints to the client log should be time-stamped with the current system time
    private static void log(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println(timestamp + ": " + message);
    }

    public static void main(String args[]) {
        if (args.length < 2) {
            log("Usage: java Client <hostname> <port>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        TCPClient1 client = new TCPClient1(hostName, portNumber);
    }
}
