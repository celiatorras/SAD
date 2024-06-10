package xatclientsGUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServerSocket {
    private ServerSocket serverSocket;

    public MyServerSocket(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public MySocket accept() throws IOException {
        Socket socket = serverSocket.accept();
        return new MySocket(socket);
    }

    public void close() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
