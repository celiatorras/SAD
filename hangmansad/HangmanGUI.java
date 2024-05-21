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
    private char[] pjug;
    private char[] pfinal;

    //Constructor
    public HangmanGUI(int llargada) {
        //Configuració bàsica de la finestra
        configurarFinestra();

        //Inicialitza els panells
        configurarPanellRestart(); 
        configurarPanellVides();
        configurarPanellParaula();
        configurarPanellAbecedari();
        configurarPanellPenjat();

        //Estableix la mida de la finestra i la fem visible
        finestra.pack(); //ajusta la mida de la finestra sense tenir tamany fix
        finestra.setLocationRelativeTo(null); //així surt al centre de la pantalla
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
        panellVides.setBackground(Color.WHITE);
        cors = new JLabel[vides]; //array de labels on aniran les icones dels cors

        for (int i = 0; i < vides; i++){ //omplim l'array amb els cors
            ImageIcon corImatge = new ImageIcon("cor.png");
            Image scaledImage = corImatge.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            cors[i] = new JLabel(new ImageIcon(scaledImage));
            panellVides.add(cors[i]); //afegim els cors al panell de vides
        }

        //posició dins la finestra
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5); //estableix marges
        finestra.add(panellVides, gbc);
    }

    // Configura el panell per a la paraula endevinada
    private void configurarPanellParaula() {
        panellParaula = new JPanel();
        etiquetaParaula = new JLabel();
        panellParaula.add(etiquetaParaula);
        panellParaula.setBackground(Color.WHITE);

        //posició dins de la finestra
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
        GridLayout layout = new GridLayout(2, 13, 3, 3); //creem 2 files amb 13 columnes
        panellAbecedari.setLayout(layout);
        panellAbecedari.setBackground(Color.WHITE);

        botonsAbecedari = new JButton[26]; //creem array de botons
        char lletra = 'A';
        for (int i = 0; i < 26; i++) { //posem cada lletra a cada botó corresponent
            botonsAbecedari[i] = new JButton(String.valueOf(lletra));
            botonsAbecedari[i].setBackground(Color.PINK);
            botonsAbecedari[i].setForeground(Color.DARK_GRAY);
            botonsAbecedari[i].addActionListener(new GestorLletres()); //quan es prem el botó es gestiona la lletra
            panellAbecedari.add(botonsAbecedari[i]); //afegim cada botó al panell
            lletra++;
        }

        //posició dins de la finestra
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
        updatePenjatImage(); //perquè surti la imatge inicial en blanc
        panellPenjat.add(penjatLabel);
        panellPenjat.setBackground(Color.WHITE); 

        //posició dins de la finestra
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        finestra.add(panellPenjat, gbc);
    }

    // Anem recorrent l'array amb les imatges per actualitzar el penjat
    private void updatePenjatImage() {
        if (intents < fotoPenjat.length) {
            ImageIcon penjatImatge = new ImageIcon(fotoPenjat[intents]);
            Image scaledImage = penjatImatge.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            penjatLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    // Configura el panell per al botó de reiniciar
    private void configurarPanellRestart() {
        panellRestart = new JPanel();
        panellRestart.setBackground(Color.WHITE);
        JButton botoRestart = new JButton("Reiniciar Joc");
        botoRestart.setBackground(Color.PINK);
        botoRestart.setForeground(Color.DARK_GRAY);
        botoRestart.addActionListener(new ActionListener(){ 
            @Override
            public void actionPerformed(ActionEvent e){
                finestra.dispose(); //treiem la fiestra principal
                Integer llargada = obtenirLongitudParaula(); //tornem a la finestra de benvinguda
                if (llargada != null) {
                    iniciarJoc(llargada); //tornem a la finestra principal
                } else {
                    System.out.println("No s'ha obtingut una longitud vàlida");
                }
            }
        });

        panellRestart.add(botoRestart); //afegim el botó al panell

        //posició dins de la finestra
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
            JButton botoPremut = (JButton) e.getSource(); //retorna el botó premut
            String lletra = botoPremut.getText(); //la lletra que hem clicat 
            botoPremut.setEnabled(false); //fem que no el poguem tornar a prémer

            // Mirem si la lletra és de la paraula o no
            boolean encert = false;
            for (int i = 0; i < pfinal.length; i++) {
                if (pfinal[i] == lletra.charAt(0)) {
                    pjug[i] = lletra.charAt(0);
                    encert = true;
                }
            }

            //si no encertem la lletra de la paraula:
            if (!encert){
                cors[intents].setVisible(false); //treiem un cor
                intents++; //sumem un intent
                updatePenjatImage(); //actualitzem imatge penjat
                
                //cas en que hem perdut
                if (intents == vides){
                    reproduirSo("perdut.wav");
                    mostrarDialogResultat("Has perdut! La paraula era: " + paraulaOriginal);
                    desactivarBotons(); //desactivem tot el tecat
                }

            //si encertem la lletra:    
            }else{
                //posem la paraula que es va omplint amb les lletres que s'encerten de manera espaiada
                etiquetaParaula.setText(String.valueOf(pjug).replace("", " "));
            }

            //cas en que hem guanyat
            if (guanyar()) {
                mostrarDialogResultat("Felicitats, has guanyat!");
                desactivarBotons(); //desactivem tot el tecat
            }
        }
    }

    // Mètode per reproduir el so
    private void reproduirSo(String ruta) {
        try {
            //creem un objecte file amb la ruta proporcionada
            File fitxerSo = new File(ruta);

            if (fitxerSo.exists()) {
                //obtenir un flux d'entrada d'àudio a partir del fitxer
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(fitxerSo);
                Clip clip = AudioSystem.getClip(); //obtenir un clip d'àudio
                clip.open(audioIn); //obrir el clip amb el flux d'àudio
                clip.start(); //reproduir el clip
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    //retonta true si la paraula pjug és igual a pfinal
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
        ArrayList<String> llista = new ArrayList<>(); //lista de paraules amb la longitud desitjada

        try (Scanner scanner = new Scanner(new File(arxiu))) {
            while (scanner.hasNextLine()) {
                String pal = scanner.nextLine();
                if (pal.length() == longitud){
                    llista.add(pal); //afegim les paraules que tinguin la longitud passada
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //dins de la llista triem una paraula random
        if (!llista.isEmpty()){ 
            Random rand = new Random();
            int indexAleatori = rand.nextInt(llista.size());
            return llista.get(indexAleatori);
        } else {
            return null;
        }
    }

    //crea finestra de benvinguda i retorna la llargada introduida per l'usuari
    public static Integer obtenirLongitudParaula() {
        //creem finesta i configurem els paràmetres
        JDialog finestraBenvinguda = new JDialog((Frame) null, "SAD", true);
        finestraBenvinguda.setSize(500, 200);
        finestraBenvinguda.setLocationRelativeTo(null); //fa que la finestra es mostri al centre de la pantalla
        finestraBenvinguda.setLayout(new BorderLayout());
        finestraBenvinguda.getContentPane().setBackground(Color.WHITE);

        //creem el missatge de la finestra
        JLabel missatge = new JLabel("Benvingut al Joc del Penjat!", SwingConstants.CENTER);
        missatge.setFont(new Font("SansSerif", Font.BOLD, 25));
        missatge.setForeground(Color.DARK_GRAY);
        missatge.setOpaque(true);
        missatge.setBackground(Color.WHITE);

        //creem panell on demanem longitud
        JPanel panellInferior = new JPanel(new FlowLayout());
        panellInferior.setBackground(Color.WHITE);
        JLabel instruccio = new JLabel("Introdueix la longitud de la paraula:");
        JTextField campLongitud = new JTextField(10); //per introduïr la longitud

        //creem botó per començar
        JButton botoInicia = new JButton("Començar");
        botoInicia.setBackground(Color.PINK);
        botoInicia.setForeground(Color.DARK_GRAY);

        final Integer[] llargada = {null}; //per guardar la longitud

        botoInicia.addActionListener(e->{
            try {
                int llargadaInput = Integer.parseInt(campLongitud.getText()); //agafem la longitud introduïda
                if (llargadaInput <= 0) throw new NumberFormatException(); //mirem que sigui viable

                llargada[0] = llargadaInput; //asigna la llargada que cal tornar
                finestraBenvinguda.dispose(); //tanca la finestra
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(finestraBenvinguda, "Introdueix un número vàlid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        //afegim al panell els 3 widgets
        panellInferior.add(instruccio);
        panellInferior.add(campLongitud);
        panellInferior.add(botoInicia);

        finestraBenvinguda.add(missatge, BorderLayout.CENTER);
        finestraBenvinguda.add(panellInferior, BorderLayout.PAGE_END);
        finestraBenvinguda.setVisible(true);

        return llargada[0]; //retorna la llargada introduïda per l'usuari
    }

    public static void iniciarJoc(int llargada) {
        //comprovem que la llargada és vàlida
        if (llargada <= 0) {
            System.out.println("Longitud no vàlida. El joc no es pot iniciar.");
            return;
        }

        String paraula = paraula("pals", llargada); //guardem la paraula que hem d'endevinar
        //comprovem que exitèixin paraules amb la longitud que volem
        if (paraula == null) {
            System.out.println("No hi ha paraules amb aquesta longitud.");
            return;
        }

        //CREEM EL JOC
        HangmanGUI joc = new HangmanGUI(llargada);
        joc.paraulaOriginal = paraula;
        joc.pfinal = paraula.toCharArray();
        joc.pjug = new char[llargada];

        //posem barres baixes per mostrar a la finestra la longitud de la paraula
        for (int i = 0; i < llargada; i++) {
            joc.pjug[i] = '_';
        }

        joc.etiquetaParaula.setText(String.valueOf(joc.pjug).replace("", " "));
        joc.etiquetaParaula.setFont(new Font("SansSerif", Font.BOLD, 24));
    }

    //fiestra del resultat quan guanyem o perdem amb el missatge corresponent
    private void mostrarDialogResultat(String missatge) {
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("Button.background", Color.red);
        UIManager.put("Button.foreground", Color.WHITE);
       
        JLabel missatgeLabel = new JLabel(missatge, SwingConstants.CENTER);
        missatgeLabel.setOpaque(true);
        missatgeLabel.setBackground(Color.WHITE);

        JOptionPane optionPane = new JOptionPane(missatgeLabel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{"Acceptar"});
        JDialog dialog = optionPane.createDialog(finestra, "Resultat");
        dialog.setVisible(true);
       
    }

    public static void main(String[] args) {
        Integer llargada = obtenirLongitudParaula(); //obtenir la longitud de la finestra de benvinguda

        if (llargada != null) {
            iniciarJoc(llargada); //iniciar el joc amb la longitud obtinguda
        } else {
            System.out.println("No es va obtenir una longitud vàlida.");
        }
    }
}
