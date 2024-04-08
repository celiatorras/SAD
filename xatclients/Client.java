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
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author usuari.aula
 */


/*
Es tracta de dos threads concurrents, un que llegeix línies del teclat i les envia al servidor (InputFil), 
i l’altre que llegeix línies del servidor—enviades per un altre client— i les imprimeix per pantalla (OutputFil). 
*/

public class Client {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce tu nick:");
        String nick = scanner.nextLine();
        
        new Thread(new ClientSad(args[0], Integer.parseInt(args[1]), nick)).start();
    }
   
   
}

class ClientSad implements Runnable{
    protected MySocket socket;
    protected InputFil teclatTreaballador;
    protected OutputFil socketTreballador;
    protected MonitorClient missatgeRebut;
    protected String nick;
    
    public ClientSad(String ip, int port, String nick){
        this.nick=nick;
        socket = new MySocket(ip, port, nick);
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
        String txt;
        while((txt = socket.rebre()) != null){  //mentre hi ha línia del servidor
            System.out.println("Missatge rebut : " + txt);
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
        String txt;
        try {
            while((txt = entradaUsuari.readLine()) != null){
                
                System.out.println("escriu missatge : ");

                System.out.println("");
                
                socket.enviar(txt);        
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
