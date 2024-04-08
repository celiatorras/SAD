/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xatclients;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import practica1.CircularQ.CircularQueue;

/**
 *
 * @author sara
 */



//FER METODE GETNICK A LA CLASSE SOCKET

public class MyServerSocket {

    public static void main(String[] args) {

        Servidor2 serv = new Servidor2(Integer.parseInt(args[0]));
        System.out.println("Servidor amb Port : " + Integer.parseInt(args[0]) + "\n");
        new Thread(serv).start();

    }

}

class Servidor2 implements Runnable {

    protected ServerSocket ss;

    //llista sockets connectats
    protected Map<String, MySocket> llistaSocketsConnect ;

    protected Buffer strings;
    
    public Servidor2(int port) {
        try {

            ss = new ServerSocket(port);
            
            // inicialitzar buffer strings
            strings = new Buffer(100);
            llistaSocketsConnect = new HashMap<>();
            Broadcast broad = new Broadcast(strings, llistaSocketsConnect);
            new Thread(broad).start();

        } catch (IOException ex) {
            Logger.getLogger(Servidor2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        while (true) {
            try {
                
                MySocket s = new MySocket(ss.accept(), "nick_default");
                // POSAR SOCKET llista sockets connectats
                String nick = s.getNick(); // Obtener el nick del cliente
                llistaSocketsConnect.put(nick, s);
                Ajudant ajudant = new Ajudant(strings, s);
                new Thread(ajudant).start();
                
            } catch (IOException ex) {
                Logger.getLogger(Servidor2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class Ajudant implements Runnable {
//  while(true){
//    llegir del socket posar al buffer
//  }

    
    MySocket s;
    protected Buffer strings;
    
    public Ajudant(Buffer strings, MySocket s){
        this.strings = strings;
        this.s=s;
    }
    public void run() {
        while(true){
            String m=s.rebre();
            strings.put(m);
        }
    }


}

class Broadcast implements Runnable {
    
    protected Buffer strings;
    protected Map<String, MySocket> sockets;
    
    public Broadcast(Buffer strings, Map<String, MySocket> sockets){
        this.strings=strings;
        this.sockets= sockets;
    }
    
    public void run() {
//    while(true){
//        agafar string buffer recorrer llista sockets connectats i enviar a cada socket
//    }
        while(true){
            String m = strings.get().toString();
            for (MySocket socket : sockets.values()) {
                    socket.enviar(m);
            }

        }
    }



}

class Buffer {

    protected CircularQueue<String> q;
    protected Condition noBuida, noPlena;
    protected ReentrantLock mon;

    public Buffer(int capacitat) {
        q = new CircularQueue(capacitat);
        mon = new ReentrantLock();
        noBuida = mon.newCondition();
        noPlena = mon.newCondition();
    }

    public void put(String e) {
        mon.lock();
        try {
            while (q.full()) {
                noPlena.awaitUninterruptibly();
            }
            q.put(e);
            noBuida.signal();
        } finally {
            mon.unlock();
        }
    }

    public Object get() {
        mon.lock();
        while (q.empty()) {
            noBuida.awaitUninterruptibly();
        }
        String res = q.get();
        noPlena.signal();
        mon.unlock();
        return res;
    }

}
