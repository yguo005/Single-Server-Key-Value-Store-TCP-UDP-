# CS 6650 Project #1: Single Server Key-Value Store (TCP & UDP)

## Project Overview

This project implements a single-threaded, single-client Key-Value store server in Java. The server supports basic operations: PUT (key, value), GET (key), and DELETE (key). A corresponding client application communicates with the server to perform these operations. A crucial requirement is that the client and server must be configurable to communicate using either TCP or UDP sockets. This can be achieved through two separate sets of applications (one for TCP, one for UDP) or a combined application configurable at runtime (though the project suggests having two instances or separate applications for each protocol).

The server listens for requests, processes them one at a time, and responds to the client. The client sends requests based on user input or predefined operations and handles server responses, including timeouts and malformed packets. Both client and server are required to log their activities with millisecond precision timestamps.

## Features

*   **Key-Value Store Operations:**
    *   `PUT key value`: Stores a value associated with a key.
    *   `GET key`: Retrieves the value associated with a key.
    *   `DELETE key`: Removes a key and its associated value.
*   **Dual Protocol Support:**
    *   Communication via **TCP** sockets.
    *   Communication via **UDP** sockets.
*   **Single-Threaded Server:** Handles one client request at a time.
*   **Client Robustness:**
    *   Timeout mechanism to handle unresponsive servers.
    *   Detection and logging of malformed or unsolicited response packets.
*   **Server Robustness:**
    *   Detection and logging of malformed request packets.
    *   Continuous operation until forcibly terminated.
*   **Time-Stamped Logging:** Both client and server logs include timestamps with millisecond precision.
*   **Custom Communication Protocol:** A simple protocol is designed to convey operations and data between client and server.
*   **Java Implementation:** Code written in Java, well-factored and commented.

## Prerequisites

*   Java Development Kit (JDK) installed (e.g., JDK 8 or later).

## Project Structure (Suggested)

You might organize your project with separate classes for TCP and UDP implementations, or combined classes with protocol selection logic.

*   `ServerTCP.java`: Server implementation using TCP.
*   `ClientTCP.java`: Client implementation using TCP.
*   `ServerUDP.java`: Server implementation using UDP.
*   `ClientUDP.java`: Client implementation using UDP.
*   (Optional) `KeyValueStore.java`: Class to manage the actual key-value data (e.g., using a HashMap).
*   (Optional) `ProtocolHandler.java`: Helper class for encoding/decoding messages for the custom protocol.

## Compilation

To compile the Java source files, navigate to the directory containing the `.java` files and run the following command in your terminal:

```bash
javac *.java
