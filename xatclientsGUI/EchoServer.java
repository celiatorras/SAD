package xatclientsGUI;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Random;

public class EchoServer {
    private static ConcurrentMap<String, MySocket> clients = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, String> userColors = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try {
            MyServerSocket ss = new MyServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                MySocket clientSocket = ss.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private MySocket socket;
        private String nick;

        public ClientHandler(MySocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                nick = socket.readLine();
                clients.put(nick, socket);
                userColors.put(nick, getRandomColor());
                broadcastUserList();
                broadcast(nick + " has joined the chat");
                System.out.println(nick + " has joined the chat");

                String message;
                while ((message = socket.readLine()) != null) {
                    broadcast(nick + ": " + message);
                    System.out.println(nick + ": " + message);
                }
            } finally {
                clients.remove(nick);
                userColors.remove(nick);
                broadcastUserList();
                broadcast(nick + " has left the chat");
                System.out.println(nick + " has left the chat");
                socket.close();
            }
        }

        private void broadcast(String message) {
            for (MySocket client : clients.values()) {
                client.println(message);
            }
        }

        private void broadcastUserList() {
            StringBuilder userList = new StringBuilder("NICKLIST:");
            for (Map.Entry<String, String> entry : userColors.entrySet()) {
                userList.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }
            String userListStr = userList.toString();
            for (MySocket client : clients.values()) {
                client.println(userListStr);
            }
        }

        private String getRandomColor() {
            Random rand = new Random();
            int r = rand.nextInt(256);
            int g = rand.nextInt(256);
            int b = rand.nextInt(256);
            return String.format("#%02x%02x%02x", r, g, b);
        }
    }
}

//javac -d build/classes/ src/xatclients/*.java
//java -cp build/classes/ xatclients.EchoServer 50000 &
//java -cp build/classes/ xatclients.Client localhost 50000 