/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xatclients;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sara
 */
public class MyServerSocket {

    public static void main(String[] args) {

        Servidor2 serv = new Servidor2(Comms.PORT_SERVIDOR);
        System.out.println("Servidor Eco Escoltant ... Port : " + Comms.PORT_SERVIDOR + "\n");
        new Thread(serv).start();

    }

}

class Servidor2 implements Runnable {

    protected ServerSocket ss;

    // broadcast
    protected ArrayList<MySocket> llistaSocketsConnect ;
//buffer llista sockets connectats

//**buffer strings:
    protected Buffer strings;
    
    public Servidor2(int port) {
        try {

            ss = new ServerSocket(port);
            
            //** inicialitzar buffer strings
            strings = new Buffer(100);
         //  inicialitzar buffer
            llistaSocketsConnect = new ArrayList();
        // crear thread broadcast
            Broadcast broad = new Broadcast(strings, llistaSocketsConnect);
            new Thread(broad).start();

        } catch (IOException ex) {
            Logger.getLogger(Servidor2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        while (true) {
            try {
                
                MySocket s = new MySocket(ss.accept());
                // POSAR SOCKET llista sockets connectats
                // iniciar thread ajudant
                llistaSocketsConnect.add(s);
                Ajudant ajudant = new Ajudant(strings, s);
                new Thread(ajudant).start();
                
            } catch (IOException ex) {
                Logger.getLogger(Servidor2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class Ajudant implements Runnable {
    
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
    
    // run
   
//        while(true){
//        llegir del socket posar al buffer
//    }

}

class Broadcast implements Runnable {
    
    protected Buffer strings;
    protected  ArrayList<MySocket> sockets;
    
    public Broadcast(Buffer strings, ArrayList<MySocket> sockets){
        this.strings=strings;
        this.sockets= sockets;
    }
    
    public void run() {
        while(true){
            String m = strings.get().toString();
            for(int i = 0;i<sockets.size();i++){
                sockets.get(i).enviar(m);
            }
//            Iterator<AstSocket> iterador2 = sockets.q.iterator();
//            
//                while (iterador2.hasNext()) {
//                    AstSocket so = iterador2.next();
//                    so.enviar(m);
//                }    
//                
//            }
        }
    }
//    // run
//   
//        while(true){
//        agafar string buffer recorrer llista sockets connectats i enviar a cada socket
//    }

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
        while (q.full()) {
            noPlena.awaitUninterruptibly();
        }
        q.put(e);
        noBuida.signal();
        mon.unlock();
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


/*

class Servidor2 implements Runnable {

    protected ServerSocket ss;

    public Servidor2(int port) {
        try {

            ss = new ServerSocket(port);

        } catch (IOException ex) {
            Logger.getLogger(Servidor2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {

        while (true) {
            try {
                MonitorSync mon = new MonitorSync();
                AstSocket s = new AstSocket(ss.accept());
                FilTeclat ft = new FilTeclat(s, mon);
                FilSocket fs = new FilSocket(s, mon);
                new Thread(ft).start();
                new Thread(fs).start();

            } catch (IOException ex) {
                Logger.getLogger(Servidor2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

*/