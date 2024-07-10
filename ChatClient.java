import java.io.*;
import java.net.*;
import java.util.function.Consumer;
import java.util.Scanner;

public class ChatClient {
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;
    public Consumer<String> onMessageReceived;

    public ChatClient(String serverAddress, int serverPort, Consumer<String> onMessageReceived) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.onMessageReceived = onMessageReceived;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void startClient() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    onMessageReceived.accept(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ChatClient <server_address> <server_port>");
            return;
        }

        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try {
            ChatClient client = new ChatClient(serverAddress, serverPort, System.out::println);
            client.startClient();

            Scanner scanner = new Scanner(System.in);
            System.out.println("Connected to the server. You can start sending messages:");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                client.sendMessage(input);
            }
        } catch (IOException e) {
            System.err.println("Failed to connect to the server: " + e.getMessage());
        }
    }
}

