import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    public static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    public static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(8080);
        System.out.println("Server started. Waiting for clients...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);

            ClientHandler clientThread = new ClientHandler(clientSocket);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }

    public static void stopServer() throws IOException {
        serverSocket.close();
        System.out.println("Server stopped.");
    }
}

class ClientHandler implements Runnable {
    public Socket clientSocket;
    public PrintWriter out;
    public BufferedReader in;

    public ClientHandler(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received message: " + inputLine);
                broadcast(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            try {
                removeClient();
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client resources: " + e.getMessage());
            }
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : ChatServer.clients) {
            client.out.println(message);
        }
    }

    public void removeClient() {
        ChatServer.clients.remove(this);
    }
}

