/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hangmansad;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
/**
 *
 * @author celia
 */
public class HangmanSAD {
    
    final static int MAX_PAL = 20;
    final static int FALSE = 0;
    final static int TRUE = 1;
    final static int MAX_CADENA = 100;
    
    /*static class Tahorcado {
        char[] pjug = new char[MAX_PAL];
        char[] pfinal = new char[MAX_PAL];
        int nletras;
        int vidas;
    }
    
    static class LlistaParaules{
        int npal;
        Tahorcado[] vpal = new Tahorcado[MAX_CADENA];
    }*/

    public static int demanarNum(){
        Scanner scanner;
        System.out.print("Cantidad de caracteres de la palabra a acertar (maximo 20 caracteres): ");
        Scanner inputScanner = new Scanner(System.in);
        int nc = inputScanner.nextInt();
        inputScanner.close();
        return nc; //aquest nc serà la longitud de la paraula a endevinar
    }

    public static String paraula(String archivo, int longitudDeseada) {
        List<String> llista = new ArrayList<>();
 
        try (Scanner scanner = new Scanner(new File(archivo))) {
            while (scanner.hasNextLine()) {
                String pal = scanner.nextLine();
                if (pal.length() == longitudDeseada) {
                    llista.add(pal);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (!llista.isEmpty()) {
            Random rand = new Random();
            int indiceAleatorio = rand.nextInt(llista.size());
            return llista.get(indiceAleatorio);
        } else {
            return null;
        }
    }
  
    public static void main(String[] args){
        int num=demanarNum();
        String Solucio= paraula("pals",num);
    }
}
