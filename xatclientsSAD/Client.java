package xatclients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Client <host> <port>");
            return;
        }

        try {
            MySocket s = new MySocket(args[0], Integer.parseInt(args[1]));

            // Input thread
            new Thread()  {
                public void run(){
                    String line;
                    BufferedReader kbd = new BufferedReader(new InputStreamReader(System.in));
                    try {
                        while ((line = kbd.readLine()) != null) {
                            s.println(line);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        s.close();
                    }
                }
            }.start();

            // Output thread
            new Thread() {
                public void run(){
                    System.out.println("Introdueix el teu nick: ");
                    String line;
                    while ((line = s.readLine()) != null) {
                        System.out.println(line);
                    }
                    s.close();
                }
            }.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
