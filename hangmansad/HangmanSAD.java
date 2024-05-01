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
 * @author celia & sara
 */
public class HangmanSAD {

    static char[] pjug;
    static char[] pfinal;
    static int vides = 6; // num de vides a cada partida
    static int intents = 0; // num d'intents que porta el jugador
    static Scanner inputScanner = new Scanner(System.in); // Scanner global

    public static int demanarNum() {
        System.out.print("Cantidad de caracteres de la palabra a acertar (maximo 20 caracteres): ");
        int nc = inputScanner.nextInt();
        return nc; // aquest nc serà la longitud de la paraula a endevinar
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

    public static boolean comparador(char lletra) {
        boolean encert = false;
        for (int i = 0; i < pfinal.length; i++) {
            if (pfinal[i] == lletra) {
                pjug[i] = lletra;
                encert = true;
            }
        }
        if(!encert) intents++;
        return encert;
    }

    public static boolean guanyar() {
        boolean guanyat = true;
        for (int i = 0; i < pfinal.length; i++) {
            if (pjug[i] != pfinal[i])
                guanyat = false;
        }
        return guanyat;
    }

    public static char demanaLletra() {
        System.out.print("Digues una lletra: ");
        String slletra = inputScanner.next();
        char lletra = slletra.charAt(0);
        return lletra;
    }

    public static void main(String[] args) {
        int num = demanarNum();
        char aux;
        String Solucio = paraula("pals", num);
        pfinal = Solucio.toCharArray();
        pjug = new char[pfinal.length];
        System.out.println(Solucio); // imprimeix la paraula que s'ha d'endevinar
        do {
            aux = demanaLletra();
            if (!comparador(aux)) {
                System.out.println("Lletra incorrecta! Perds una vida.");
            } else {
                System.out.println(pjug);
            }

        } while (!guanyar() && intents < vides);
        if (intents >= vides)
            System.out.println("Has perdut");
        else
            System.out.println("Has guanyat");
    }
}
