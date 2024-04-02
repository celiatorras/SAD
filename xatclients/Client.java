/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package xatclients;

/**
 *
 * @author sara
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author usuari.aula
 */
public class Client {

    public static void main(String[] args){
        new Thread(new ClientSad(Comms.IP_SERVIDOR, Comms.PORT_SERVIDOR)).start();
    }
   
   
}

class ClientSad implements Runnable{
    protected MySocket socket;
    protected InputFil teclatTreaballador;
    protected OutputFil socketTreballador;
    protected MonitorClient missatgeRebut;
   
    public ClientSad(String ip, int port){
        socket = new MySocket(ip, port);
        missatgeRebut = new MonitorClient();
        teclatTreaballador = new  InputFil(socket, missatgeRebut);
        socketTreballador = new OutputFil(socket, missatgeRebut);

    }
   
    public void run(){
        new Thread(teclatTreaballador).start();
        new Thread(socketTreballador).start();
    }
}

class OutputFil implements Runnable{ 
    protected MySocket socket;
    protected MonitorClient missatgeRebut;    

    public OutputFil(MySocket sc, MonitorClient mon){
        socket = sc;
        missatgeRebut = mon;
    }    
   
    public void run(){
        String txtEcho;
        while((txtEcho = socket.rebre()) != null){  //mentre hi ha línia del servidor
            System.out.println("Missatge rebut : " + txtEcho);
            System.out.println("");
            missatgeRebut.avisa();
        }
    }
}

class InputFil implements Runnable{
    protected MySocket socket;
    protected BufferedReader entradaUsuari;
    protected MonitorClient missatgeRebut;
   
    public InputFil(MySocket sc, MonitorClient mon){
        socket = sc;
        entradaUsuari = new BufferedReader (new InputStreamReader(System.in));
        missatgeRebut = mon;
    }
    public void run(){
        String txtUsuari;
        try {
            while((txtUsuari = entradaUsuari.readLine()) != null){
                
                System.out.println("escriu missatge : ");

                System.out.println("");
                
                socket.enviar(txtUsuari);        
            }
        } catch (IOException ex) {
            Logger.getLogger(InputFil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class MonitorClient{
    protected Lock mon;
    protected Condition Rebut;
   
    public MonitorClient(){
        mon = new ReentrantLock();
        Rebut = mon.newCondition();
    }
   
    public void espera(){
        mon.lock();
        try {
            Rebut.awaitUninterruptibly();
        } finally {
            mon.unlock();
        }
    }
   
    public void avisa(){
        mon.lock();
        try {
            Rebut.signal();
        } finally {
            mon.unlock();
        }
    }    
}