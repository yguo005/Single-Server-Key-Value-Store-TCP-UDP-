import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UDPClient1 {
    // initialize socket and input output streams
    private DatagramSocket socket = null;
    private BufferedReader input = null;
    private InetAddress IPAddress = null;
    private int port;

    // constructor to put ip address and port
    public UDPClient1(String address, int port) {
        // establish a connection
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(5000); // set a timeout for reading from the server
            IPAddress = InetAddress.getByName(address);
            this.port = port;
            log("Connected");

            // takes input from terminal
            input = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException u) {
            log(u.getMessage());
            return;
        } catch (SocketException i) {
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
                byte[] sendData = line.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                socket.send(sendPacket);
                log("Sent: " + line);

                // read response from server
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                // stop receiving when "END" message is received
                while (!response.trim().equals("END")) {
                    log("Received: " + response);
                    socket.receive(receivePacket);
                    response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                }

            } catch (SocketTimeoutException e) {
                log("Error: server time out");
            } catch (IOException i) {
                log(i.getMessage());
            }
        }

        // close the connection
        socket.close();
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

        UDPClient1 client = new UDPClient1(hostName, portNumber);
    }
}
