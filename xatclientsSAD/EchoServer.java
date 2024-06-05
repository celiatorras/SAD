package xatclients;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EchoServer {
    private static ConcurrentMap<String, MySocket> clients = new ConcurrentHashMap<>();

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
                new Thread(new ClientSad(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientSad implements Runnable {
        private MySocket socket;
        private String nick;

        public ClientSad(MySocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                
                nick = socket.readLine();
                clients.put(nick, socket);
                broadcast(nick + " esta dins del xat");
                System.out.println(nick + " esta dins del xat");

                String message;
                while ((message = socket.readLine()) != null) {
                    broadcast(nick + ": " + message);
                    System.out.println(nick + ": " + message);
                }
            } finally {
                clients.remove(nick);
                broadcast(nick + " ha marxat del chat");
                System.out.println(nick + " ha marxat del chat");
                socket.close();
            }
        }

        private void broadcast(String message) {
            for (MySocket client : clients.values()) {
                client.println(message);
            }
        }
    }
}

//javac -d build/classes/ src/xatclients/*.java
//java -cp build/classes/ xatclients.EchoServer 50000 &
//java -cp build/classes/ xatclients.Client localhost 50000 