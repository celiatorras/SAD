/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hangmansad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class HangmanGUI {
    private final JFrame finestra;
    private final JPanel panellVides;
    private final JPanel panellParaula; // Panell on es mostrarà la paraula endevinada
    private final JPanel panellAbecedari;
    private final JButton[] botonsAbecedari;

    private final JLabel[] cors; // Imatges dels cors
    private final JLabel etiquetaParaula; // Per mostrar la paraula endevinada

    private final int vides = 6; // Nombre inicial de vides
    private int intents = 0; // Comptador d'intents
    private String paraulaOriginal;
    private char[] pjug; // Paraula endevinada fins ara
    private char[] pfinal; // Paraula original com a array de caràcters

    public HangmanGUI(int llargada) {
        finestra = new JFrame("Joc del Penjat");
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestra.setSize(700, 400);
        finestra.setLayout(new BorderLayout());

        // Secció per als cors (representant les vides)
        panellVides = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cors = new JLabel[vides];

        for (int i = 0; i < vides; i++) {
            ImageIcon corImatge = new ImageIcon("cor.png");
            Image scaledImage = corImatge.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            cors[i] = new JLabel(new ImageIcon(scaledImage));
            panellVides.add(cors[i]);
        }

        finestra.add(panellVides, BorderLayout.NORTH); // Panell per als cors a la part superior

        // Secció per a la paraula endevinada (al centre)
        panellParaula = new JPanel();
        etiquetaParaula = new JLabel(); 
        panellParaula.add(etiquetaParaula);
        finestra.add(panellParaula, BorderLayout.CENTER); // Al centre

        // Secció per al panell de l'abecedari (a la part inferior)
        panellAbecedari = new JPanel(new GridLayout(2, 13));
        botonsAbecedari = new JButton[26];
        char lletra = 'A';
        for (int i = 0; i < 26; i++) {
            botonsAbecedari[i] = new JButton(String.valueOf(lletra));
            botonsAbecedari[i].addActionListener(new GestorLletres());
            panellAbecedari.add(botonsAbecedari[i]);
            lletra++;
        }

        finestra.add(panellAbecedari, BorderLayout.PAGE_END); // Panell de lletres a la part inferior

        finestra.setVisible(true); 
    }

    private class GestorLletres implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton botoPremut = (JButton) e.getSource();
            String lletra = botoPremut.getText(); 
            botoPremut.setEnabled(false); 

            boolean encert = false;
            for (int i = 0; i < pfinal.length; i++) {
                if (pfinal[i] == lletra.charAt(0)) {
                    pjug[i] = lletra.charAt(0);
                    encert = true;
                }
            }

            if (!encert) {
                intents++;
                if (intents < vides) {
                    cors[intents].setVisible(false); // Oculta un cor
                } else {
                    JOptionPane.showMessageDialog(finestra, "Has perdut! La paraula era: " + paraulaOriginal);
                    desactivarTotsElsBotons();
                }
            }

            etiquetaParaula.setText(String.valueOf(pjug).replace("", " ")); 

            if (guanyar()) {
                JOptionPane.showMessageDialog(finestra, "Felicitats, has guanyat!");
                desactivarTotsElsBotons();
            }
        }
    }

    private boolean guanyar() {
        for (int i = 0; i < pfinal.length; i++) {
            if (pjug[i] != pfinal[i]) {
                return false;
            }
        }
        return true; // S'ha endevinat completament la paraula
    }

    private void desactivarTotsElsBotons() {
        for (JButton boto : botonsAbecedari) {
            boto.setEnabled(false);
        }
    }

    public static String paraula(String arxiu, int longitud) {
        java.util.List<String> llista = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(arxiu))) {
            while (scanner.hasNextLine()) {
                String pal = scanner.nextLine();
                if (pal.length() == longitud) {
                    llista.add(pal);
                }
            }
        } catch (FileNotFoundException e) {
        }

        if (!llista.isEmpty()) {
            Random rand = new Random();
            int indiceAleatori = rand.nextInt(llista.size());
            return llista.get(indiceAleatori);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        JDialog finestraBenvinguda = new JDialog((Frame) null, "Benvingut", true);
        finestraBenvinguda.setSize(500, 200);
        finestraBenvinguda.setLayout(new BorderLayout());

        JLabel missatge = new JLabel("Benvingut al Joc del Penjat!", SwingConstants.CENTER);
        missatge.setFont(new Font("Arial", Font.BOLD, 18));
        missatge.setForeground(Color.BLUE);

        JPanel panellInferior = new JPanel(new FlowLayout());
        JLabel instrucció = new JLabel("Introdueix la longitud de la paraula:");
        JTextField campLongitud = new JTextField(10);

        JButton botóInicia = new JButton("Començar");
        botóInicia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int llargada = Integer.parseInt(campLongitud.getText());
                    if (llargada <= 0) {
                        throw new NumberFormatException(); 
                    }

                    String paraula = paraula("pals", llargada);
                    if (paraula == null) {
                        JOptionPane.showMessageDialog(finestraBenvinguda, "No hi ha paraules amb aquesta longitud.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    finestraBenvinguda.dispose(); 

                    HangmanGUI joc = new HangmanGUI(llargada);
                    joc.paraulaOriginal = paraula;
                    joc.pfinal = paraula.toCharArray();
                    joc.pjug = new char[llargada];

                    for (int i = 0; i < llargada; i++) {
                        joc.pjug[i] = '_'; 
                    }

                    joc.etiquetaParaula.setText(String.valueOf(joc.pjug).replace("", " ")); 
                    joc.etiquetaParaula.setFont(new Font("Arial", Font.BOLD, 24));
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(finestraBenvinguda, "Introdueix un número vàlid.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panellInferior.add(instrucció);
        panellInferior.add(campLongitud);
        panellInferior.add(botóInicia);

        finestraBenvinguda.add(missatge, BorderLayout.CENTER);
        finestraBenvinguda.add(panellInferior, BorderLayout.PAGE_END);

        finestraBenvinguda.setVisible(true); 
    }
}
