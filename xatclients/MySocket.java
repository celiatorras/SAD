/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt per canviar aquesta llicència
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java per editar aquesta plantilla
 */
package xatclients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sara i Cèlia
 */

public class MySocket {
    protected Socket s; // Socket per a la comunicació
    protected BufferedReader entradaTxt; // Per llegir missatges rebuts
    protected PrintWriter sortidaTxt; // Per enviar missatges
    protected String nick; // Nom de l'usuari del socket
        
    /**
     * Constructor que crea una nova connexió amb el servidor especificat.
     * @param ipServidor Adreça IP del servidor
     * @param portServidor Port del servidor
     */
    public MySocket(String ipServidor, int portServidor, String Nick){
        try {
            // Intenta establir una connexió amb el servidor utilitzant la IP i el port especificats
            s = new Socket(ipServidor, portServidor);
            // Inicialitza el lector d'entrada per llegir missatges rebuts
            entradaTxt = new BufferedReader(new InputStreamReader(s.getInputStream()));        
            // Inicialitza el gestor de sortida per enviar missatges
            sortidaTxt = new PrintWriter(s.getOutputStream(),true);
            
            this.nick=nick;
            
        } catch (IOException ex) {
            Logger.getLogger(MySocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Constructor que utilitza una connexió existent.
     * @param sc Socket ja existent
     */
    public MySocket(Socket sc, String Nick){
        try {
            // Utilitza el socket ja existent per comunicar-se
            s = sc;
            // Inicialitza el lector d'entrada per llegir missatges rebuts
            entradaTxt = new BufferedReader(new InputStreamReader(s.getInputStream()));        
            // Inicialitza el gestor de sortida per enviar missatges
            sortidaTxt = new PrintWriter(s.getOutputStream(),true);
           
            this.nick= nick;
            
        } catch (IOException ex) {
            Logger.getLogger(MySocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
   
    /**
     * Envia un missatge al servidor.
     * @param str Missatge a enviar
     */
    public void enviar(String str){
        // Envia el missatge al servidor a través del gestor de sortida
        sortidaTxt.println(str);
    }
   
    /**
     * Rep un missatge del servidor.
     * @return El missatge rebut
     */
    public String rebre(){
        try {
            // Llegeix un missatge rebut del servidor utilitzant el lector d'entrada
            return entradaTxt.readLine();
       
        } catch (IOException ex) {
            Logger.getLogger(MySocket.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String getNick(){
        return nick;
    }

}
