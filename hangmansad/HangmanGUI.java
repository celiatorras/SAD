package hangmansad;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class HangmanGUI {
    private JFrame finestra;
    private JPanel panellVides;
    private JPanel panellParaula;
    private JPanel panellAbecedari;
    private JPanel panellRestart;
    private JPanel panellPenjat;

    private JButton[] botonsAbecedari;
    private JLabel[] cors;
    private JLabel etiquetaParaula;
    private JLabel penjatLabel;

    private int vides = 6;
    private int intents = 0;
    private String paraulaOriginal;
    private String[] fotoPenjat = {"0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png"};
    private int indexString = 0;
    private char[] pjug;
    private char[] pfinal;

    public HangmanGUI(int llargada) {
        // Configuració bàsica de la finestra
        configurarFinestra();

        // Inicialitza els panells
        configurarPanellRestart(); // Col·locar panell de reiniciar a la part superior
        configurarPanellVides();   // Col·locar panell de vides a la part superior
        configurarPanellParaula();
        configurarPanellAbecedari();
        configurarPanellPenjat();

        // Estableix la mida de la finestra i la fa visible
        finestra.pack();
        finestra.setLocationRelativeTo(null); // Aquesta línia fa que la finestra es mostri al centre de la pantalla
        finestra.setVisible(true);
    }

    // Configura la finestra principal del joc
    private void configurarFinestra() {
        finestra = new JFrame("Joc del Penjat");
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestra.setLayout(new GridBagLayout());
        finestra.getContentPane().setBackground(Color.WHITE); // Fons blanc per a la finestra principal
    }

    // Configura el panell dels cors
    private void configurarPanellVides() {
        panellVides = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panellVides.setBackground(Color.WHITE); // Fons blanc per al panell de vides
        cors = new JLabel[vides];

        for (int i = 0; i < vides; i++) {
            ImageIcon corImatge = new ImageIcon("cor.png");
            Image scaledImage = corImatge.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            cors[i] = new JLabel(new ImageIcon(scaledImage));
            panellVides.add(cors[i]);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        finestra.add(panellVides, gbc);
    }

    // Configura el panell per a la paraula endevinada
    private void configurarPanellParaula() {
        panellParaula = new JPanel();
        etiquetaParaula = new JLabel();
        panellParaula.add(etiquetaParaula);
        panellParaula.setBackground(Color.WHITE); // Fons blanc per al panell de la paraula

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        finestra.add(panellParaula, gbc);
    }

    // Configura el panell per a l'abecedari amb marges i separacions
    private void configurarPanellAbecedari() {
        panellAbecedari = new JPanel();
        GridLayout layout = new GridLayout(2, 13, 3, 3);
        panellAbecedari.setLayout(layout);
        panellAbecedari.setBackground(Color.WHITE); // Fons blanc per al panell de l'abecedari

        botonsAbecedari = new JButton[26];
        char lletra = 'A';
        for (int i = 0; i < 26; i++) {
            botonsAbecedari[i] = new JButton(String.valueOf(lletra));
            botonsAbecedari[i].setBackground(Color.PINK);
            botonsAbecedari[i].setForeground(Color.DARK_GRAY);
            botonsAbecedari[i].addActionListener(new GestorLletres());
            panellAbecedari.add(botonsAbecedari[i]);
            lletra++;
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        finestra.add(panellAbecedari, gbc);
    }

    // Configura el panell per a l'imatge del penjat
    private void configurarPanellPenjat() {
        panellPenjat = new JPanel();
        penjatLabel = new JLabel();
        updatePenjatImage();
        panellPenjat.add(penjatLabel);
        panellPenjat.setBackground(Color.WHITE); // Fons blanc per al panell del penjat

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        finestra.add(panellPenjat, gbc);
    }

    private void updatePenjatImage() {
        if (indexString < fotoPenjat.length) {
            ImageIcon penjatImatge = new ImageIcon(fotoPenjat[indexString]);
            Image scaledImage = penjatImatge.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            penjatLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    // Configura el panell per al botó de reiniciar
    private void configurarPanellRestart() {
        panellRestart = new JPanel();
        panellRestart.setBackground(Color.WHITE); // Fons blanc per al panell de reiniciar
        JButton botoRestart = new JButton("Reiniciar Joc");
        botoRestart.setBackground(Color.PINK);
        botoRestart.setForeground(Color.DARK_GRAY);
        botoRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finestra.dispose();
                Integer llargada = obtenirLongitudParaula();
                if (llargada != null) {
                    iniciarJoc(llargada);
                } else {
                    System.out.println("No se obtuvo una longitud válida.");
                }
            }
        });

        panellRestart.add(botoRestart);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        finestra.add(panellRestart, gbc);
    }

    // Gestor dels botons de l'abecedari
    private class GestorLletres implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton botoPremut = (JButton) e.getSource();
            String lletra = botoPremut.getText();
            botoPremut.setEnabled(false);

            // Mirem si la lletra és de la paraula o no
            boolean encert = false;
            for (int i = 0; i < pfinal.length; i++) {
                if (pfinal[i] == lletra.charAt(0)) {
                    pjug[i] = lletra.charAt(0);
                    encert = true;
                }
            }

            if (!encert) {
                if (intents < vides) {
                    cors[intents].setVisible(false); // Treure el cor
                    indexString++;
                    updatePenjatImage();
                }
                intents++;
                if (intents == vides) {
                    reproduirSo("perdut.wav");
                    mostrarDialogResultat("Has perdut! La paraula era: " + paraulaOriginal);
                    desactivarBotons();
                }

            } else {
                etiquetaParaula.setText(String.valueOf(pjug).replace("", " "));
            }

            if (guanyar()) {
                mostrarDialogResultat("Felicitats, has guanyat!");
                desactivarBotons();
            }
        }
    }

    // Mètode per reproduir un so
    private void reproduirSo(String ruta) {
        try {
            File fitxerSo = new File(ruta);
            if (fitxerSo.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(fitxerSo);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private boolean guanyar() {
        for (int i = 0; i < pfinal.length; i++) {
            if (pjug[i] != pfinal[i]) {
                return false;
            }
        }
        reproduirSo("guanya.wav");
        return true;
    }

    private void desactivarBotons() {
        for (JButton boto : botonsAbecedari) {
            boto.setEnabled(false);
        }
    }

    // Mètode que ens dona la paraula a endevinar
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
            e.printStackTrace();
        }

        if (!llista.isEmpty()) {
            Random rand = new Random();
            int indiceAleatori = rand.nextInt(llista.size());
            return llista.get(indiceAleatori);
        } else {
            return null;
        }
    }

    public static Integer obtenirLongitudParaula() {
        JDialog finestraBenvinguda = new JDialog((Frame) null, "SAD", true);
        finestraBenvinguda.setSize(500, 200);
        finestraBenvinguda.setLocationRelativeTo(null); // Aquesta línia fa que la finestra es mostri al centre de la pantalla
        finestraBenvinguda.setLayout(new BorderLayout());
        finestraBenvinguda.getContentPane().setBackground(Color.WHITE); // Fons blanc per a la finestra de benvinguda

        JLabel missatge = new JLabel("Benvingut al Joc del Penjat!", SwingConstants.CENTER);
        missatge.setFont(new Font("SansSerif", Font.BOLD, 25));
        missatge.setForeground(Color.DARK_GRAY);
        missatge.setOpaque(true);
        missatge.setBackground(Color.WHITE); // Fons blanc per al missatge

        JPanel panellInferior = new JPanel(new FlowLayout());
        panellInferior.setBackground(Color.WHITE); // Fons blanc per al panell inferior
        JLabel instruccio = new JLabel("Introdueix la longitud de la paraula:");
        JTextField campLongitud = new JTextField(10);

        JButton botoInicia = new JButton("Començar");
        botoInicia.setBackground(Color.PINK);
        botoInicia.setForeground(Color.DARK_GRAY);

        final Integer[] llargada = {null};

        botoInicia.addActionListener(e -> {
            try {
                int llargadaInput = Integer.parseInt(campLongitud.getText());
                if (llargadaInput <= 0) throw new NumberFormatException();

                llargada[0] = llargadaInput;  // Asigna la llargada que cal tornar
                finestraBenvinguda.dispose(); // Tanca la finestra
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(finestraBenvinguda, "Introdueix un número vàlid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panellInferior.add(instruccio);
        panellInferior.add(campLongitud);
        panellInferior.add(botoInicia);

        finestraBenvinguda.add(missatge, BorderLayout.CENTER);
        finestraBenvinguda.add(panellInferior, BorderLayout.PAGE_END);

        finestraBenvinguda.setVisible(true);

        return llargada[0];  // Retorna la llargada introduïda per l'usuari
    }

    public static void iniciarJoc(int llargada) {
        if (llargada <= 0) {
            System.out.println("Longitud no vàlida. El joc no es pot iniciar.");
            return;
        }

        String paraula = paraula("pals", llargada);
        if (paraula == null) {
            System.out.println("No hi ha paraules amb aquesta longitud.");
            return;
        }

        HangmanGUI joc = new HangmanGUI(llargada);
        joc.paraulaOriginal = paraula;
        joc.pfinal = paraula.toCharArray();
        joc.pjug = new char[llargada];

        for (int i = 0; i < llargada; i++) {
            joc.pjug[i] = '_';
        }

        joc.etiquetaParaula.setText(String.valueOf(joc.pjug).replace("", " "));
        joc.etiquetaParaula.setFont(new Font("SansSerif", Font.BOLD, 24));
    }

    private void mostrarDialogResultat(String missatge) {
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("Button.background", Color.red);
        UIManager.put("Button.foreground", Color.WHITE);
       
       
        //Posar-ho al centre
        JLabel missatgeLabel = new JLabel(missatge, SwingConstants.CENTER);
        missatgeLabel.setOpaque(true);
        missatgeLabel.setBackground(Color.WHITE);

        JOptionPane optionPane = new JOptionPane(missatgeLabel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{"Acceptar"});
        JDialog dialog = optionPane.createDialog(finestra, "Resultat");
        dialog.setVisible(true);
       
    }

    public static void main(String[] args) {
        Integer llargada = obtenirLongitudParaula();  // Obtenir la longitud de la finestra de benvinguda

        if (llargada != null) {
            iniciarJoc(llargada);  // Iniciar el joc amb la longitud obtinguda
        } else {
            System.out.println("No es va obtenir una longitud vàlida.");
        }
    }
}
